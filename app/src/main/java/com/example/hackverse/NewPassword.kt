package com.example.hackverse

// This is a Fragment....


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.hackverse.databinding.FragmentNewPasswordBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class NewPassword : Fragment() {

    private lateinit var firebase : FirebaseAuth
    private lateinit var database : DatabaseReference

    private var  _binding : FragmentNewPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentNewPasswordBinding.inflate(inflater,container,false)


        firebase = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("USERS")

        binding.buttonConfimPassword.setOnClickListener {

            val newpass = binding.NewPassword.editText?.text.toString()
            val confirNewPass = binding.confirmNewPassword.editText?.text.toString()

            val user = firebase.currentUser

            if(user == null)
            {
                Toast.makeText(requireContext(),"Could not find your Login information",Toast.LENGTH_SHORT).show()
            }
            else
            {
                if((newpass.length <6)) {
                    Toast.makeText(
                        requireContext(),
                        "Password must be at of 6 characters",
                        Toast.LENGTH_SHORT
                    ).show()
                }else {
                    if (newpass == confirNewPass) {
                     user.updatePassword(newpass).addOnSuccessListener { snapshot ->

                         val passwordMap = mapOf(

                             "password" to newpass

                         )
                         val userIDinNewpassword = arguments?.getString("SendUserIDToNewPassword").toString()
                         Log.d("Userid_FromChangePassword","The Userid from change password activity is $userIDinNewpassword")

                         database.child(userIDinNewpassword).updateChildren(passwordMap)

                         Toast.makeText(requireContext(),"Sccuessfully Updated your Password.",Toast.LENGTH_SHORT).show()

                         requireActivity().finish()

                     }.addOnFailureListener {

                         Toast.makeText(requireContext(),"Some problems occurred.",Toast.LENGTH_SHORT).show()
                     }

                    }else{
                        Toast.makeText(requireContext(),"password not matched. Check and corrent it.",Toast.LENGTH_SHORT).show()
                    }
                }



            }
        }


        return binding.root
    }


}