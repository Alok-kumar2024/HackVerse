package com.example.hackverse

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.hackverse.databinding.ActivityEditProfileBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditProfile : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var binding : ActivityEditProfileBinding

    private  var urlInEdit = ""
    private var geturl =""


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

        var check : Boolean = true
        //val check = intent.getBooleanExtra("FromImageFragment",false)

        supportFragmentManager.setFragmentResultListener("ImageFragmentResult",this){_, bundle ->
             check = bundle.getBoolean("FromImageFragment",false)
            geturl = bundle.getString("URL_From_ImageFragment",null)?: "No URL"
        }



        Log.d("InfoFromImageFragment","The check is $check "+
                "The url i get is $geturl")

        if (check)
        {
            binding.ConstraintLayoutEditData.visibility = View.VISIBLE
            binding.ConstraintLayoutEditDataImage.visibility = View.GONE
        }

        if(userIDfromProfile == "Not Got" || userIDfromProfile.isEmpty())
        {
            Toast.makeText(this,"Couldn't Fetch your Details",Toast.LENGTH_SHORT).show()
            finish()
        }







        val imageview = binding.ImageForProfile


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



        val useridEdit = binding.UserIDShowEdit
        val usernameEdit = binding.UsernameShowEdit
        val fullnameEdit = binding.fullnameShowEdit
        val emailIDEdit = binding.emailShowEdit

        database.child(userIDfromProfile).get().addOnSuccessListener { snaphshot ->

            if(snaphshot.exists())
            {
               // URL_Profile= snaphshot.child("url").value

                useridEdit.text = snaphshot.key.toString()
                val usernameFromDatabase = snaphshot.child("username").value.toString()
                val fullnameFromDatabase = snaphshot.child("name").value.toString()
                val emailFromRealTimeDatabase = snaphshot.child("email").value.toString()

                if (geturl == "No URL" || geturl == ""){

                    Glide.with(this)
                        .load(R.drawable.profile_menu)
                        .into(imageview)

                }else{

                if (check)
                {
                    urlInEdit = geturl
                }else{
                    urlInEdit = snaphshot.child("url").value.toString()
                }

                Glide.with(this)
                    .load(urlInEdit)
                    .placeholder(R.drawable.loading_for_image_vector)
                    .error(R.drawable.profile_menu)
                    .into(imageview)

                    }


                Log.d("Data Fetched From Realtime database","The userID ${useridEdit.text} " +
                        " The username $usernameFromDatabase " +
                        "The full name $fullnameFromDatabase " +
                        "The email $emailFromRealTimeDatabase " +
                        "The URL is $urlInEdit")

                usernameEdit.setText(usernameFromDatabase)
                fullnameEdit.setText(fullnameFromDatabase)
                emailIDEdit.setText(emailFromRealTimeDatabase)

                binding.UserIDShowEdit.setOnClickListener{

                    Toast.makeText(this, "Cannot Edit this Field.",Toast.LENGTH_SHORT).show()
                }
                binding.emailShowEdit.setOnClickListener{

                    Toast.makeText(this,"Cannot Edit this Field.",Toast.LENGTH_SHORT).show()
                }

                binding.profileSavechanges.setOnClickListener {

                    val changedUserName= binding.UsernameShowEdit.text.toString()
                    val changedFullName = binding.fullnameShowEdit.text.toString()

                    val updateInfoProfile = mapOf(
                        "username" to changedUserName,
                        "name" to changedFullName,
                        "url" to urlInEdit
                    )

                    Log.d("Data in Edit box at clicking save ","The username $changedUserName" +
                            "The Full name $changedFullName .")

                    database.child(userIDfromProfile).updateChildren(updateInfoProfile).addOnSuccessListener {

                        Toast.makeText(this,"SuccessFully Updated your information.",Toast.LENGTH_SHORT).show()

                        finish()
                    }.addOnFailureListener {

                        Log.e("Error updating Edited information","Error is ${snaphshot.value}")
                        Toast.makeText(this,"Some Error Occurred, Couldn't update your information.",Toast.LENGTH_SHORT).show()
                    }




                }


            }

        }


    }

}