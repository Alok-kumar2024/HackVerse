package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.hackverse.databinding.FragmentLogOutBinding
import com.google.firebase.auth.FirebaseAuth


class LogOutFragment : Fragment() {

    private lateinit var firebase : FirebaseAuth

    private var _binding : FragmentLogOutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentLogOutBinding.inflate(inflater,container,false)

        firebase = FirebaseAuth.getInstance()

        binding.cancelLogout.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.buttonLogout.setOnClickListener {

        firebase.signOut()
            Toast.makeText(requireContext(),"Successfully Logged out.", Toast.LENGTH_SHORT).show()

            val getLoggedInDetails = requireActivity().getSharedPreferences("LoggedIN", MODE_PRIVATE)
            val editor = getLoggedInDetails.edit()

            editor.clear()
            editor.apply()


            val intent = Intent(activity,LoginActivity::class.java)
            startActivity(intent)

            if(isAdded) {
                requireActivity().finish()
            }



        }

        return binding.root
    }

}