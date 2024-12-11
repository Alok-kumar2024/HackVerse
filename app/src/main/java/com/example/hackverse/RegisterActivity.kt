package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.example.hackverse.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

//        binding.password.setOnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus && binding.password.text.isNullOrEmpty()) {
//                binding.password.error = "password cannot be empty"
//            } else {
//                binding.password.error = null
//            }
//        }
//
//        binding.confirmPassword.setOnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus && binding.confirmPassword.editText?.text.isNullOrEmpty()) {
//                binding.confirmPassword.error = "Confirm Password cannot be empty"
//            } else {
//                binding.confirmPassword.error = null
//            }
//        }

        binding.Register.setOnClickListener{

            val email = binding.email.text.toString()
            val password = binding.password.editText?.text.toString()
            val confirm_password = binding.confirmPassword.editText?.text.toString()

            if(email.isEmpty() || password.isEmpty() || confirm_password.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
            else{
                if (password == confirm_password) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {

                            if (it.isSuccessful) {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }
                } else {
                    Toast.makeText(this, "Password does not match.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.LoginRedirectText.setOnClickListener{

            val loginIntent = Intent(this,LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
    }
}