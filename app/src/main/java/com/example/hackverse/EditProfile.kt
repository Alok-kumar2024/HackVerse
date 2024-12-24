package com.example.hackverse

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.hackverse.databinding.ActivityEditProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditProfile : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var binding : ActivityEditProfileBinding

    private lateinit var imageview : ImageView
    private lateinit var useridEdit : TextView
    private lateinit var usernameEdit : EditText
    private lateinit var fullnameEdit :EditText
    private lateinit var emailIDEdit : TextView


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding =  ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editProfile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = FirebaseDatabase.getInstance().getReference("USERS")

        val getuseridfromProfileForEdit = getSharedPreferences("SharingToEditActivity", MODE_PRIVATE)

        val userIDfromProfile :String = getuseridfromProfileForEdit.getString("UseridFromProfileToEditProfile",null) ?: "Not Got"

        Log.d("UseridGotFromProfile","The userId from profile is $userIDfromProfile")


        //val check = intent.getBooleanExtra("FromImageFragment",false)






        if(userIDfromProfile == "Not Got" || userIDfromProfile.isEmpty())
        {
            Toast.makeText(this,"Couldn't Fetch your Details",Toast.LENGTH_SHORT).show()
            finish()
        }





         imageview = binding.ImageForProfile


        binding.BackToMyProfileButton.setOnClickListener {
            finish()
        }
        binding.DontSave.setOnClickListener {
            finish()
        }

        onBackPressedDispatcher.addCallback()
        {
            finish()
        }

        binding.cardview.setOnClickListener{

//            binding.ConstraintLayoutEditDataImage.visibility = View.VISIBLE
//            val intent = Intent(this , ImageProfileFragment::class.java)
//            intent.putExtra("UserIDFromEditProfile",userIDfromProfile)

//            val bundle = Bundle()
//            bundle.putString("UserIDFromEditProfile",userIDfromProfile)
//            ImageProfileFragment().arguments = bundle

//            binding.ConstraintLayoutEditData.visibility = View.GONE
            binding.UserId.visibility = View.GONE
            binding.UserIDShowEdit.visibility = View.GONE
            binding.Username.visibility = View.GONE
            binding.UsernameShowEdit.visibility = View.GONE
            binding.fullname.visibility = View.GONE
            binding.fullnameShowEdit.visibility = View.GONE
            binding.emailid.visibility = View.GONE
            binding.emailShowEdit.visibility = View.GONE
            binding.profileSavechanges.visibility = View.GONE
            binding.DontSave.visibility = View.GONE
            binding.ConstraintLayoutEditDataImage.visibility = View.VISIBLE

            val share = getSharedPreferences("USERIDFROMEDITPROFILE", MODE_PRIVATE)
            val editor = share.edit()
            editor.putString("UserIDFromEditProfile",userIDfromProfile).apply()

            supportFragmentManager.beginTransaction().replace(R.id.FrameLayoutForImageFragment,ImageProfileFragment())
                .addToBackStack(null).commit()
        }



         useridEdit = binding.UserIDShowEdit
         usernameEdit = binding.UsernameShowEdit
         fullnameEdit = binding.fullnameShowEdit
         emailIDEdit = binding.emailShowEdit

        binding.UserIDShowEdit.setOnClickListener{

            Toast.makeText(this@EditProfile, "Cannot Edit this Field.",Toast.LENGTH_SHORT).show()
        }
        binding.emailShowEdit.setOnClickListener{

            Toast.makeText(this@EditProfile,"Cannot Edit this Field.",Toast.LENGTH_SHORT).show()
        }

//        database.child(userIDfromProfile).addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists())
//                {
//                    // URL_Profile= snaphshot.child("url").value
//
//                    useridEdit.text = snapshot.key.toString()
//                    val usernameFromDatabase = snapshot.child("username").value.toString()
//                    val fullnameFromDatabase = snapshot.child("name").value.toString()
//                    val emailFromRealTimeDatabase = snapshot.child("email").value.toString()
//
//                    if (geturl == "No URL" || geturl == ""){
//
//                        Glide.with(this@EditProfile)
//                            .load(R.drawable.profile_menu)
//                            .into(imageview)
//
//                    }else{
//
//                        if (check)
//                        {
//                            urlInEdit = geturl
//                        }else{
//                            urlInEdit = snapshot.child("url").value.toString()
//                        }
//
//                        Glide.with(this@EditProfile)
//                            .load(urlInEdit)
//                            .placeholder(R.drawable.loading_for_image_vector)
//                            .error(R.drawable.profile_menu)
//                            .into(imageview)
//
//                    }
//
//
//                    Log.d("ImageData Fetched From Realtime database","The userID ${useridEdit.text} " +
//                            " The username $usernameFromDatabase " +
//                            "The full name $fullnameFromDatabase " +
//                            "The email $emailFromRealTimeDatabase " +
//                            "The URL is $urlInEdit")
//
//                    usernameEdit.setText(usernameFromDatabase)
//                    fullnameEdit.setText(fullnameFromDatabase)
//                    emailIDEdit.setText(emailFromRealTimeDatabase)

        LoadUserProfile(userIDfromProfile)


                    binding.profileSavechanges.setOnClickListener {

//                        val changedUserName= binding.UsernameShowEdit.text.toString()
//                        val changedFullName = binding.fullnameShowEdit.text.toString()
//
//                        val updateInfoProfile = mapOf(
//                            "username" to changedUserName,
//                            "name" to changedFullName,
//                            "url" to urlInEdit
//                        )
//
//                        Log.d("Data in Edit box at clicking save ","The username $changedUserName" +
//                                "The Full name $changedFullName .")
//
//                        database.child(userIDfromProfile).updateChildren(updateInfoProfile).addOnSuccessListener {
//
//                            Toast.makeText(this@EditProfile,"SuccessFully Updated your information.",Toast.LENGTH_SHORT).show()
//
//                            finish()
//                        }.addOnFailureListener {
//
//                            Toast.makeText(this@EditProfile,"Some Error Occurred, Couldn't update your information.",Toast.LENGTH_SHORT).show()
//                        }
//
//

                        SaveUserProfile(userIDfromProfile)


                    }


//                }
//            }

//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@EditProfile,"Due to some error fetching get cancelled.",Toast.LENGTH_SHORT).show()
//            }
//
//        })


    }

    private fun SaveUserProfile(userIDfromProfile: String) {

        val changedUserName= binding.UsernameShowEdit.text.toString()
        val changedFullName = binding.fullnameShowEdit.text.toString()

        val updateInfoProfile = mapOf(
            "username" to changedUserName,
            "name" to changedFullName,
        )
        if (changedUserName.isEmpty() || changedFullName.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("Data in Edit box at clicking save ","The username $changedUserName" +
                "The Full name $changedFullName .")

        database.child(userIDfromProfile).updateChildren(updateInfoProfile).addOnSuccessListener {

            Toast.makeText(this@EditProfile,"SuccessFully Updated your information.",Toast.LENGTH_SHORT).show()

            finish()
        }.addOnFailureListener {

            Toast.makeText(this@EditProfile,"Some Error Occurred, Couldn't update your information.",Toast.LENGTH_SHORT).show()
        }




    }

    private fun LoadUserProfile(userIDfromProfile: String) {

        database.child(userIDfromProfile).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // URL_Profile= snaphshot.child("url").value

                    useridEdit.text = snapshot.key.toString()
                    val usernameFromDatabase = snapshot.child("username").value.toString()
                    val fullnameFromDatabase = snapshot.child("name").value.toString()
                    val emailFromRealTimeDatabase = snapshot.child("email").value.toString()
                    val urlFromRealTimeDatabase = snapshot.child("url").value.toString()

                    if (urlFromRealTimeDatabase.isEmpty()) {

                        Glide.with(this@EditProfile)
                            .load(R.drawable.profile_menu)
                            .into(imageview)

                    } else {

                        Glide.with(this@EditProfile)
                            .load(urlFromRealTimeDatabase)
                            .placeholder(R.drawable.loading_for_image_vector)
                            .error(R.drawable.profile_menu)
                            .into(imageview)

                    }


                    Log.d(
                        "ImageData Fetched From Realtime database",
                        "The userID ${useridEdit.text} " +
                                " The username $usernameFromDatabase " +
                                "The full name $fullnameFromDatabase " +
                                "The email $emailFromRealTimeDatabase "
                    )

                    usernameEdit.setText(usernameFromDatabase)
                    fullnameEdit.setText(fullnameFromDatabase)
                    emailIDEdit.setText(emailFromRealTimeDatabase)

                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }
}
