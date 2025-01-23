package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.hackverse.databinding.FragmentImageProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ImageProfileFragment : Fragment() {

    private var _binding : FragmentImageProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var database : DatabaseReference

    private var URLForImage = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentImageProfileBinding.inflate(inflater,container,false)

        val editprofileactivity = requireActivity().findViewById<ConstraintLayout>(R.id.Constraint_Layout_Edit_Data)

        val userid = editprofileactivity.findViewById<TextView>(R.id.UserId)
        val useridshowedit = editprofileactivity.findViewById<TextView>(R.id.UserID_show_edit)
        val username = editprofileactivity.findViewById<TextView>(R.id.Username)
        val usernameshowedit = editprofileactivity.findViewById<EditText>(R.id.Username_show_edit)
        val fullname = editprofileactivity.findViewById<TextView>(R.id.fullname)
        val fullnameshowedit = editprofileactivity.findViewById<EditText>(R.id.fullname_show_edit)
        val emailid = editprofileactivity.findViewById<TextView>(R.id.emailid)
        val emailidshowedit = editprofileactivity.findViewById<TextView>(R.id.email_show_edit)
        val profilesavechanges = editprofileactivity.findViewById<Button>(R.id.profile_savechanges)
        val dontsave = editprofileactivity.findViewById<Button>(R.id.DontSave)




        database = FirebaseDatabase.getInstance().getReference("USERS")

//        val getUserIDFromEditprofile = arguments?.getString("UserIDFromEditProfile") ?: "Not Fetched"
        val getsharedata = requireActivity().getSharedPreferences("USERIDFROMEDITPROFILE", MODE_PRIVATE)
        val getUserIDFromEditprofile = getsharedata.getString("UserIDFromEditProfile",null) ?: "Not Fetched"
        Log.d("UserIDForImage","The userid got from edit profile for update profile image is $getUserIDFromEditprofile .")

        val URLedit = binding.EnterURLForImage

        binding.buttonCancelProfileImage.setOnClickListener {

            userid.visibility = View.VISIBLE
            useridshowedit.visibility = View.VISIBLE
            username.visibility = View.VISIBLE
            usernameshowedit.visibility = View.VISIBLE
            fullname.visibility = View.VISIBLE
            fullnameshowedit.visibility = View.VISIBLE
            emailid.visibility = View.VISIBLE
            emailidshowedit.visibility = View.VISIBLE
            profilesavechanges.visibility = View.VISIBLE
            dontsave.visibility = View.VISIBLE

            binding.ImageFragment.visibility = View.GONE

            requireActivity().supportFragmentManager.popBackStack()

        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){

            userid.visibility = View.VISIBLE
            useridshowedit.visibility = View.VISIBLE
            username.visibility = View.VISIBLE
            usernameshowedit.visibility = View.VISIBLE
            fullname.visibility = View.VISIBLE
            fullnameshowedit.visibility = View.VISIBLE
            emailid.visibility = View.VISIBLE
            emailidshowedit.visibility = View.VISIBLE
            profilesavechanges.visibility = View.VISIBLE
            dontsave.visibility = View.VISIBLE

            binding.ImageFragment.visibility = View.GONE

            requireActivity().supportFragmentManager.popBackStack()
        }

        database.child(getUserIDFromEditprofile).addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    val urlFromDatabase = snapshot.child("url").value.toString()

                    Log.d("URLFromBaseImage","The url from realtime database is $urlFromDatabase")

                    URLedit.setText(urlFromDatabase)
                }
                else{
                    Toast.makeText(requireContext(),"Unable to Fetch data.",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"Somne Error occurred.",Toast.LENGTH_SHORT).show()
            }

        })




        binding.buttonConfirmProfileImage.setOnClickListener {

             URLForImage = binding.EnterURLForImage.text.toString()

            Log.d("URLinImageFrag","The url is $URLForImage ")

//            val updateURl = mapOf(
//                "url" to URLForImage
//            )

            Log.d("URL i put","The URL user put is $URLForImage")

            if (!isValidURL(URLForImage))
            {
                Toast.makeText(requireContext(),"Incorrect Format of URL",Toast.LENGTH_LONG).show()
            }else{

//                database.child(getUserIDFromEditprofile).updateChildren(updateURl).addOnSuccessListener {
//
//
//                    Toast.makeText(requireContext(),"Successfully Updat")
//                    requireActivity().supportFragmentManager.popBackStack()
//                }
//                val intent = Intent(requireActivity(),EditProfile::class.java)
//                intent.putExtra("FromImageFragment",true)
//                intent.putExtra("URL_From_ImageFragment",URLForImage)
//                startActivity(intent)
//                requireActivity().finish()

//                val resultBundle = Bundle().apply {
//                    putBoolean("FromImageFragment", true)
//                    putString("URL_From_ImageFragment", URLForImage)
//                }
//                requireActivity().supportFragmentManager.setFragmentResult("ImageFragmentResult", resultBundle)
//                requireActivity().supportFragmentManager.popBackStack()
                userid.visibility = View.VISIBLE
                useridshowedit.visibility = View.VISIBLE
                username.visibility = View.VISIBLE
                usernameshowedit.visibility = View.VISIBLE
                fullname.visibility = View.VISIBLE
                fullnameshowedit.visibility = View.VISIBLE
                emailid.visibility = View.VISIBLE
                emailidshowedit.visibility = View.VISIBLE
                profilesavechanges.visibility = View.VISIBLE
                dontsave.visibility = View.VISIBLE

                binding.ImageFragment.visibility = View.GONE

                val uploadURLToRealTimeDatabase = mapOf(
                    "url" to URLForImage
                )
                database.child(getUserIDFromEditprofile).updateChildren(uploadURLToRealTimeDatabase).addOnSuccessListener {

                    Toast.makeText(requireContext(),"SuccessFully Updated your Profile Image.",Toast.LENGTH_LONG).show()

                    requireActivity().finish()



                }.addOnFailureListener {

                    Toast.makeText(requireContext(),"Some Error Occurred, Couldn't update your information.",Toast.LENGTH_SHORT).show()
                }

            //    requireActivity().supportFragmentManager.popBackStack()


            }
        }

        return binding.root
    }


    private fun isValidURL(url :String) : Boolean {

        return  Patterns.WEB_URL.matcher(url).matches()
    }

}