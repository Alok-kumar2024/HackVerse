package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hackverse.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.apache.commons.validator.routines.EmailValidator

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityForgotPasswordBinding

    private lateinit var firebase : FirebaseAuth
    private lateinit var database : DatabaseReference

    private fun CheckEmail(checkEmail : String):Boolean {

        val valiator = EmailValidator.getInstance()

        return valiator.isValid(checkEmail)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        firebase = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("USERS")

        binding.TextCheckEmail.visibility = View.INVISIBLE

        binding.buttonCancelForgotPassword.setOnClickListener {

//            additionalLayout.findViewById<TextView>(R.id.TextviewChangePassword).visibility =View.VISIBLE
//            additionalLayout.findViewById<EditText>(R.id.currentPassword).visibility =View.VISIBLE
//            additionalLayout.findViewById<Button>(R.id.cancel).visibility =View.VISIBLE
//            additionalLayout.findViewById<Button>(R.id.confirm).visibility =View.VISIBLE
//            additionalLayout.findViewById<TextView>(R.id.forgotpassword).visibility =View.VISIBLE
//            additionalLayout.findViewById<FrameLayout>(R.id.Frame_Container_ForForgotPassword).visibility =View.INVISIBLE

//
//           val intent = Intent(activity,PasswordChange::class.java)
//            intent.putExtra("value",true)
//            startActivity(intent)
//
//            if(isAdded) {
//
//            parentFragmentManager.beginTransaction()
//                .remove(this)
//                .commit()
            finish()
        }

        binding.buttonConfirmForgotPassword.setOnClickListener {

            val passwordchangeEmail= binding.Entermail.text.toString()

            if(passwordchangeEmail.isEmpty())
            {
                Toast.makeText(this,"Email Field cannot be empty.", Toast.LENGTH_SHORT).show()
            }
            else if (!CheckEmail(passwordchangeEmail))
            {
                Toast.makeText(this,"Check your Email Format..", Toast.LENGTH_SHORT).show()
            }else {

                val querydata = database.orderByChild("email").equalTo(passwordchangeEmail)
                Log.d(
                    "EmailSendForChecking",
                    "The Email , send for checking is $passwordchangeEmail"
                )

                querydata.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            Log.d(
                                "DataExists_CheckForForgotPassword",
                                "Data exists: ${snapshot.value}"
                            )


                            for (userSnapshot in snapshot.children) {
                                val user = userSnapshot.getValue(coders::class.java)

                                var userID = userSnapshot.key.toString()
                                var username = user?.name ?: "N/A"
                                var emailId = user?.email ?: "N/A"

                                Log.d(
                                    "UsersData",
                                    "User ID: $userID, Username: $username, Email: $emailId"
                                )
                            }

                            firebase.sendPasswordResetEmail(passwordchangeEmail).addOnSuccessListener {

                                Toast.makeText(this@ForgotPasswordActivity,"Successfully Send Reset Email, Kindly Check your Email.",
                                    Toast.LENGTH_SHORT).show()
//                                Toast.makeText(requireContext(),"Make Sure to fill new password  in Below box , for it to be updated in our database.",Toast.LENGTH_SHORT).show()
//                                binding.TextOfForgotPassword.visibility = View.INVISIBLE
//                                binding.TextViewForgotPassword.visibility = View.INVISIBLE
//                                binding.Entermail.visibility = View.INVISIBLE
//                                binding.buttonCancelForgotPassword.visibility =  View.INVISIBLE
//                                binding.buttonConfirmForgotPassword.visibility = View.INVISIBLE

                                binding.TextCheckEmail.visibility = View.VISIBLE

                                val getLoggedInDetails = getSharedPreferences("LoggedIN", MODE_PRIVATE)
                                val editor = getLoggedInDetails.edit()

                                editor.clear()
                                editor.apply()

//                                binding.NewPasswordForgot.visibility = View.VISIBLE
//                                binding.SomeTextForUser.visibility = View.VISIBLE
//                                binding.buttonConfirmChangedPassword.visibility = View.VISIBLE

                                Handler(Looper.getMainLooper()).postDelayed({

                                    val intent = Intent(this@ForgotPasswordActivity,LoginActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                    finish()

                                },5000)
                                Toast.makeText(this@ForgotPasswordActivity,"Kindly Re-Login after changing Password",
                                    Toast.LENGTH_LONG).show()

                            }.addOnFailureListener {
                                Toast.makeText(this@ForgotPasswordActivity,"Some Error occurred , couldn't send Reset Email",
                                    Toast.LENGTH_SHORT).show()
                            }


                        }else(

                                Toast.makeText(this@ForgotPasswordActivity,"Couldn't find the Email in Database.",
                                    Toast.LENGTH_SHORT).show()
                                )

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }

        }






    }
}