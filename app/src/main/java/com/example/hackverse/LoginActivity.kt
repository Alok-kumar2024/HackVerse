package com.example.hackverse

import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import org.apache.commons.validator.routines.EmailValidator
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var navigation : NavigationView

    private fun CheckEmail(checkEmail : String) : Boolean {

        val validator = EmailValidator.getInstance()

        return validator.isValid(checkEmail)
    }

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

//        lateinit var UserID : String
//        lateinit var Username : String
//        lateinit var EmailId : String

//        binding.TextViewForgotPasswordLogin.setOnClickListener {
//            val fragment = ForgotPassword()
//
//            supportFragmentManager.beginTransaction().replace(R.id.FrameLayoutForgotPassword,fragment).addToBackStack(null).commit()
//        }


        database = FirebaseDatabase.getInstance().reference.child("USERS")

        firebaseAuth = FirebaseAuth.getInstance()

//        //finding navigation view...
//        navigation = findViewById(R.id.navigation_view)
//
//        //fnding first header view in navigation view of acivity main, first navigation view by 0
//        val headerfile = navigation.getHeaderView(0)

//        val inflater = LayoutInflater.from(this)
//        val headerfile = inflater.inflate(R.layout.drawer_layout,null)

//        val headerUser : TextView = headerfile.findViewById(R.id.headerUser)
//        val headerEmail : TextView = headerfile.findViewById(R.id.headerEmail)
//        val headerUserID : TextView = headerfile.findViewById(R.id.headerUserID)
//
//        val str1 : String = "UserID -> "
//        val str2 : String = "UserName -> "
//        val str3 : String = "UserEmail -> "


        binding.TextViewForgotPasswordLogin.setOnClickListener {

            val intent = Intent(this,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }



        binding.Login.setOnClickListener{


            binding.progressBar.visibility = View.VISIBLE

            val email = binding.email.text.toString().lowercase().trim()
            val password = binding.password.editText?.text.toString().trim()

            Log.d("LoginActivity", "Email entered: $email")

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this,"Check your Email Format..",Toast.LENGTH_SHORT).show()
            }

            else {
                val queryData = database.orderByChild("email").equalTo(email)

                Log.d("LoginActivity", "Query: database.orderByChild('email').equalTo('$email')")

                queryData.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Log.d("Firebase", "Data exists: ${snapshot.value}")

                            var userID: String = ""
                            var username: String = ""
                            var emailId: String = ""
                            var passwordmatch: String = ""

                            for (userSnapshot in snapshot.children) {
                                val user = userSnapshot.getValue(coders::class.java)

                                userID = userSnapshot.key.toString()
                                username = user?.name ?: "N/A"
                                emailId = user?.email ?: "N/A"
                                passwordmatch = user?.password ?: "N?A"
                            }

                            Log.d("LoginActivity", "User ID: $userID, Username: $username, Email: $emailId")

//                            val headerUser: TextView = headerfile.findViewById(R.id.headerUser)
//                            val headerEmail: TextView = headerfile.findViewById(R.id.headerEmail)
//                            val headerUserID: TextView = headerfile.findViewById(R.id.headerUserID)

                            if(password != passwordmatch)
                            {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(this@LoginActivity,"Password Not Matched...",Toast.LENGTH_SHORT).show()
                                binding.Login.isEnabled = true

                            }

                            val startTime = System.currentTimeMillis()
                            binding.Login.isEnabled = false

                            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                                binding.Login.isEnabled = true

                                val endTime = System.currentTimeMillis()
                                Log.d("Performance", "Authentication took: ${endTime - startTime}ms")
                                if (it.isSuccessful) {
//                                    binding.progressBar.visibility = View.VISIBLE

//                                    headerUser.text = "UserName -> $username"
//                                    headerEmail.text = "UserEmail -> $emailId"
//                                    headerUserID.text = "UserID -> $userID"
                                    val mapofforgotpassword = mapOf(
                                        "password" to password
                                    )

                                    database.child(userID).updateChildren(mapofforgotpassword)

                                    val sharedLoggedIn = getSharedPreferences("LoggedIN",
                                        MODE_PRIVATE)
                                    
                                    val editorLoggedIn = sharedLoggedIn.edit()
                                    editorLoggedIn.putBoolean("isLoggedIn",true)
                                    editorLoggedIn.putString("ShareLoginToMain","SourceLogin")
                                    editorLoggedIn.apply()



                                    val shareToMainByLogin =
                                        getSharedPreferences("ShareLogin", MODE_PRIVATE)
                                    val editor = shareToMainByLogin.edit()
//                                    editor.putString("CheckLogin","SourceLogin")
//                                    editor.putBoolean("isLoggedIn",true)
                                    editor.putString("LoginUserID", userID).apply()
                                    editor.putString("LoginUserName", username).apply()
                                    editor.putString("LoginEmailID", emailId).apply()

//                                    val sharetoactivityMain = getSharedPreferences("Source", MODE_PRIVATE)
//                                    val editorRegister = sharetoactivityMain.edit()
//                                    editorRegister.putString("Check","SourceLogin").apply()


                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                                    intent.putExtra("ShareLoginToMain", "SourceLogin")


                                    startActivity(intent)
                                    Toast.makeText(this@LoginActivity,"Welcome $username !!",Toast.LENGTH_SHORT).show()
                                    finish()
                                } else {
                                    binding.progressBar.visibility = View.INVISIBLE
                                    Toast.makeText(this@LoginActivity, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            binding.progressBar.visibility = View.GONE
                            Log.d("Firebase", "No user found for email: $email")
                            Toast.makeText(this@LoginActivity, "User of Email $email Not found", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "Query cancelled", error.toException())
                        Toast.makeText(applicationContext, "Query cancelled: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })

            }





        }

    binding.GoToRegisterActivity.setOnClickListener{

        val loginIntent = Intent(this,RegisterActivity::class.java)
        startActivity(loginIntent)

        finish()
    }

    }
}


