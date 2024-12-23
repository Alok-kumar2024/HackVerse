package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import com.example.hackverse.databinding.FragmentForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.apache.commons.validator.routines.EmailValidator


class ForgotPassword : Fragment() {

    private var _binding : FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebase : FirebaseAuth
    private lateinit var database : DatabaseReference


    private fun CheckEmail(checkEmail : String):Boolean {

        val valiator = EmailValidator.getInstance()

        return valiator.isValid(checkEmail)

    }

    override fun onDestroy() {
        super.onDestroy()

        view?.background = null

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(inflater,container,false)

//        val additionalLayout = inflater.inflate(R.layout.activity_password_change,null)


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            requireActivity().finish()
        }


        firebase = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("USERS")

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
//                requireActivity().finish()
//            }

            requireActivity().finish()
        }

        binding.buttonConfirmForgotPassword.setOnClickListener {

            val passwordchangeEmail= binding.Entermail.text.toString()

            if(passwordchangeEmail.isEmpty())
            {
                Toast.makeText(requireContext(),"Email Field cannot be empty.",Toast.LENGTH_SHORT).show()
            }
            else if (!CheckEmail(passwordchangeEmail))
            {
                Toast.makeText(requireContext(),"Check your Email Format..", Toast.LENGTH_SHORT).show()
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

                                Toast.makeText(requireContext(),"Successfully Send Reset Email, Kindly Check your Email.",Toast.LENGTH_SHORT).show()
//                                Toast.makeText(requireContext(),"Make Sure to fill new password  in Below box , for it to be updated in our database.",Toast.LENGTH_SHORT).show()
//                                binding.TextOfForgotPassword.visibility = View.INVISIBLE
//                                binding.TextViewForgotPassword.visibility = View.INVISIBLE
//                                binding.Entermail.visibility = View.INVISIBLE
//                                binding.buttonCancelForgotPassword.visibility =  View.INVISIBLE
//                                binding.buttonConfirmForgotPassword.visibility = View.INVISIBLE

                                binding.TextCheckEmail.visibility = View.VISIBLE
//                                binding.NewPasswordForgot.visibility = View.VISIBLE
//                                binding.SomeTextForUser.visibility = View.VISIBLE
//                                binding.buttonConfirmChangedPassword.visibility = View.VISIBLE

                                Handler(Looper.getMainLooper()).postDelayed({

                                    val intent = Intent(requireContext(),LoginActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                    requireActivity().finish()

                                },5000)
                                Toast.makeText(requireContext(),"Kindly Re-Login after changing Password",Toast.LENGTH_LONG).show()

                            }.addOnFailureListener {
                                Toast.makeText(requireContext(),"Some Error occurred , couldn't send Reset Email",Toast.LENGTH_SHORT).show()
                            }


                        }else(

                            Toast.makeText(requireContext(),"Couldn't find the Email in Database.",Toast.LENGTH_SHORT).show()
                        )

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }

        }


        return binding.root
    }


}