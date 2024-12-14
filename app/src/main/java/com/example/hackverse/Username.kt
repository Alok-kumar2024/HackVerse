package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hackverse.databinding.ActivityUsernameBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Username : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var binding : ActivityUsernameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityUsernameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        database =  FirebaseDatabase.getInstance().reference

        val confirm = binding.UserConfirm

        val name = Coders_Data.name.toString()
        val email = Coders_Data.email.toString()
        val password = Coders_Data.password.toString()



        confirm.setOnClickListener {

            val username = binding.coderUserName.text.toString()

            Log.d("UserDataFromRegisterActivity", "UserName: $username, Name: $name , Email: $email , Password: $password")

            //Creating custom keys
            fun generateKey():String{

                val storeName = username



                val suffix = (1000 .. 9999).random()

                val symbol = listOf('@','%','!','&')

                val randomsymbol = symbol.random()

                return "$storeName${randomsymbol}$suffix"

            }

            val UserID = generateKey()
            val user = coders(username,name,email,password)

            if(UserID.isNotEmpty() && name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty())
            {

                if(username.isEmpty()) {


                    Toast.makeText(this,"Username Field cannot be Empty",Toast.LENGTH_SHORT).show()

                    return@setOnClickListener

                }   else{


                    database.child("USERS").child(UserID).setValue(user)

                    val sharetoactivity = getSharedPreferences("MyUsersUserID", MODE_PRIVATE)
                    val editor = sharetoactivity.edit()
                    editor.putString("CodersUserID", UserID).apply()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()


                }
            } else{
                Toast.makeText(this,"User Data Incomplete...",Toast.LENGTH_SHORT).show()
            }


        }

    }


    }
