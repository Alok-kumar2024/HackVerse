package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hackverse.databinding.ActivityLoginBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var navigation : NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = FirebaseDatabase.getInstance().getReference("USERS")

        firebaseAuth = FirebaseAuth.getInstance()

//        //finding navigation view...
//        navigation = findViewById(R.id.navigation_view)
//
//        //fnding first header view in navigation view of acivity main, first navigation view by 0
//        val headerfile = navigation.getHeaderView(0)

        val inflater = LayoutInflater.from(this)
        val headerfile = inflater.inflate(R.layout.drawer_layout,null)

        val HeaderUser : TextView = headerfile.findViewById(R.id.headerUser)
        val HeaderEmail : TextView = headerfile.findViewById(R.id.headerEmail)
        val HeaderUserID : TextView = headerfile.findViewById(R.id.headerUserID)

        val str1 : String = "UserID -> "
        val str2 : String = "UserName -> "
        val str3 : String = "UserEmail -> "


        val getsharedUerID = getSharedPreferences("MyUsersUserID", MODE_PRIVATE)

        val UserID = getsharedUerID.getString("CodersUserID",null).toString()

        binding.Login.setOnClickListener{


            val email = binding.email.text.toString()
            val password =binding.password.editText?.text.toString()


            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
            else {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {

                        binding.progressBar.visibility = View.VISIBLE

                        if(UserID.isEmpty()) {

                            HeaderUser.text = "N/A"
                            HeaderEmail.text = "N/A"
                            HeaderUserID.text = "Not found"

                        }

                        database.child(UserID).get().addOnSuccessListener { snapshot ->
                            if (snapshot.exists()) {
                                val Username = snapshot.child("name").value.toString()
                                val EmailId = snapshot.child("email").value.toString()

                                HeaderUser.text = "$str2$Username"
                                HeaderEmail.text = "$str3$EmailId"

                                HeaderUserID.text = "$str1$UserID"

                                Log.d("FetchDataFromRealTime","SnapShot ${snapshot.value}")
                                Log.e("FetchDataFromRealTimeError", "Error ${snapshot.value}")
                            }else{

                                HeaderUser.text = "N/A"
                                HeaderEmail.text = "N/A"
                                HeaderUserID.text = "N/A"
                                Log.e("FetchDataFromRealTimeError","Error ${snapshot.value}")

                            }
                        }

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    binding.GoToRegisterActivity.setOnClickListener{

        val loginIntent = Intent(this,RegisterActivity::class.java)
        startActivity(loginIntent)

        finish()
    }

    }
}


