package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.combine

class ProfileFragment : Fragment() {

    private var getuserid : String = ""

    private lateinit var database : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

//        val editbutton = view.findViewById<Button>(R.id.Edit_button)

//        editbutton.setOnClickListener {
//            edit()
//
//        }

        val userid = view.findViewById<TextView>(R.id.UserId)
        val useridShow = view.findViewById<TextView>(R.id.UserID_show)

        val username = view.findViewById<TextView>(R.id.Username)
        val usernameShow = view.findViewById<TextView>(R.id.Username_show)

        val fullname = view.findViewById<TextView>(R.id.fullname)
        val fullnameShow = view.findViewById<TextView>(R.id.fullname_show)

        val emailid = view.findViewById<TextView>(R.id.emailid)
        val emailidShow = view.findViewById<TextView>(R.id.email_show)
        val ImageView = view.findViewById<ImageView>(R.id.ProfileImageOfMyProfile)


         getuserid = arguments?.getString("UserId").toString()
        Log.d("GetUserID","UserId i got from main header is $getuserid")

        database = FirebaseDatabase.getInstance().getReference("USERS")

        if(getuserid.isEmpty())
        {
            useridShow.text = "Not Found"
            usernameShow.text = "Unable To Fetch"
            fullnameShow.text = "Unable To Fetch"
            emailidShow.text = "Unable To Fetch"

        }
        else{

            database.child(getuserid).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.exists())
                    {
                        useridShow.text = getuserid
                        usernameShow.text = snapshot.child("username").value.toString()
                        fullnameShow.text = snapshot.child("name").value.toString()
                        emailidShow.text = snapshot.child("email").value.toString()
                        val urlGet = snapshot.child("url").value.toString()


                        if (isAdded && !isRemoving && view != null) {
                            Glide.with(this@ProfileFragment)
                                .load(urlGet)
                                .placeholder(R.drawable.loading_for_image_vector)
                                .error(R.drawable.default_image_of_profile)
                                .into(ImageView)
                        }

                        Log.d("Fetched Data","useridshow after fetching $useridShow" +
                                "usernameshow after fetching $usernameShow" +
                                "full name after fetching $fullnameShow" +
                                "emailid after fetching $emailidShow" +
                                "URL fetched is $urlGet")


                    }else{


                        useridShow.text = "Does not Exists"
                        usernameShow.text = "Does not Exists"
                        fullnameShow.text = "Does not Exists"
                        emailidShow.text = "Does not Exists"

                        Log.e("Not exists","Error of not exsiting is ${snapshot.value}")

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),"Failed To show users Details",Toast.LENGTH_SHORT).show()
                }

            })






        }

        val settingsbutton : ImageButton= view.findViewById(R.id.buttonSettingProfile)

        settingsbutton.setOnClickListener { view->

            val popup = PopupMenu(requireContext(),view)
            popup.menuInflater.inflate(R.menu.profile_menu,popup.menu)
            popup.setOnMenuItemClickListener { item ->

                when(item.itemId)
                {
                    R.id.Edit_Profile -> {

                        val shareuserid = requireActivity().getSharedPreferences("SharingToEditActivity", MODE_PRIVATE)
                        val editor = shareuserid.edit()
                        editor.putString("UseridFromProfileToEditProfile",getuserid).apply()

                        val intent= Intent(requireContext(),EditProfile::class.java)
                        startActivity(intent)


                        true
                    }

                    R.id.ResetPassword_profile -> {

                        val intent = Intent(activity,PasswordChange::class.java)
                        intent.putExtra("UserIDFromProfile",getuserid)
                        startActivity(intent)

                        true

                    }

                    else->false
                }


            }
            popup.show()

        }

//            val changepassbtn = view.findViewById<Button>(R.id.profile_changepassword)
//
//            changepassbtn.setOnClickListener {
//
//                val intent = Intent(activity,PasswordChange::class.java)
//                intent.putExtra("UserIDFromProfile",getuserid)
//                startActivity(intent)
//            }




        return view
    }

//    private fun edit(){
//
//        val shareuserid = requireActivity().getSharedPreferences("SharingToEditActivity", MODE_PRIVATE)
//        val editor = shareuserid.edit()
//        editor.putString("UseridFromProfileToEditProfile",getuserid).apply()
//
//        val intent= Intent(requireContext(),EditProfile::class.java)
//        startActivity(intent)
//    }


}