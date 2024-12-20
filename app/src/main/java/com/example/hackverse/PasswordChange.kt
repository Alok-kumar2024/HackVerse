package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.hackverse.databinding.ActivityPasswordChangeBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PasswordChange : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordChangeBinding
    private lateinit var database : DatabaseReference

    private var senduseridtonewpassword : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPasswordChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.PasswordChange)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        val data = intent.getBooleanExtra("value",false)
//
//        if (data)
//        {
//            binding.TextviewChangePassword.visibility =View.VISIBLE
//            binding.currentPassword.visibility = View.VISIBLE
//
//            binding.cancel.visibility = View.VISIBLE
//           binding.confirm.visibility = View.VISIBLE
//            binding.forgotpassword.visibility = View.VISIBLE
//
//            binding.FrameContainerForForgotPassword.visibility = View.INVISIBLE
//        }

        database = FirebaseDatabase.getInstance().getReference("USERS")

        val useridfromprofile = intent.getStringExtra("UserIDFromProfile") ?: "Not Got"
        Log.d("useridprofile","The UserID I got from profile is $useridfromprofile" )

        val confirmbtn = binding.confirm


        binding.cancel.setOnClickListener {

            finish()
        }

        binding.forgotpassword.setOnClickListener{


            binding.TextviewChangePassword.visibility =View.INVISIBLE
            binding.currentPassword.visibility = View.INVISIBLE

            binding.cancel.visibility = View.INVISIBLE
            confirmbtn.visibility = View.INVISIBLE
            binding.forgotpassword.visibility = View.INVISIBLE

            binding.FrameContainerForForgotPassword.visibility = View.VISIBLE

            supportFragmentManager.beginTransaction().replace(R.id.Frame_Container_ForForgotPassword,ForgotPassword()).addToBackStack(null)
                .commit()


        }


        if(useridfromprofile.isEmpty() || useridfromprofile == "Not Got")
        {
            Toast.makeText(this,"Unable to fetch your userid",Toast.LENGTH_SHORT).show()

        }else{

            database.child(useridfromprofile).get().addOnSuccessListener { snapshot ->

                if(snapshot.exists())
                {
                    val passwordfromdatabase = snapshot.child("password").value.toString()
                    Log.d("PasswordDatabase","The Password I got from RealTime Database is $passwordfromdatabase.")

                    confirmbtn.setOnClickListener {

                        val editpassword= binding.currentPassword.editText?.text.toString()

                        if (editpassword.isEmpty())
                        {
                            Toast.makeText(this,"Current Password cannot be Empty.",Toast.LENGTH_SHORT).show()
                        }
                        else if(editpassword == passwordfromdatabase)
                            {

                                senduseridtonewpassword = useridfromprofile

                                binding.TextviewChangePassword.visibility =View.INVISIBLE
                                binding.currentPassword.visibility = View.INVISIBLE

                                binding.cancel.visibility = View.INVISIBLE
                                confirmbtn.visibility = View.INVISIBLE
                                binding.forgotpassword.visibility = View.INVISIBLE
                                binding.FrameContainerOfProfile.visibility = View.VISIBLE
                                replaceFragment(NewPassword())

                        }else{
                                Toast.makeText(this,"Wrong Password",Toast.LENGTH_SHORT).show()
                            }

                        }
                    }else {

                    Toast.makeText(this,"Some Problem occurred.",Toast.LENGTH_SHORT).show()

                    Log.e("ErrorWhyCantFetch","Error is ${snapshot.value}")
                }

            }
        }



    }

    private fun replaceFragment(fragment : Fragment) {

        val sendid = senduseridtonewpassword

        val bundle = Bundle()
        bundle.putString("SendUserIDToNewPassword",sendid)

        fragment.arguments = bundle

        supportFragmentManager.beginTransaction().replace(R.id.Frame_Container_OfProfile,fragment)
            .addToBackStack(null)
            .commit()

    }
}