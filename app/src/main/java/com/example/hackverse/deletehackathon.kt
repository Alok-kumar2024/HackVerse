package com.example.hackverse

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.hackverse.databinding.FragmentDeletehackathonBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class deletehackathon : Fragment() {

    private var _binding : FragmentDeletehackathonBinding? = null

    private val binding get() = _binding!!

    private lateinit var database : DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDeletehackathonBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment

        binding.cancelDelete.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val currentID = arguments?.getString("UserId").toString()
        Log.d("Delete_GetUserID", "UserId i got from detailsHackathon is $currentID")

        val hackathonID = arguments?.getString("HackathonID").toString()
        Log.d("Delete_GetHackathonID", "HackathonId i got from detailsHackathon is $hackathonID")


        database = FirebaseDatabase.getInstance().getReference("HACKATHON")


        FirebaseDatabase.getInstance().getReference("USERS").child(currentID)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())
                    {
                        val pass = (snapshot.child("password").value ?:"Not Got").toString()

                        binding.buttonDeleteEvent.setOnClickListener {
                            val enteredpass = binding.currentPasswordCheck.editText?.text.toString()
                            if(enteredpass.isEmpty())
                            {
                                Toast.makeText(requireContext(),"Password Cannot Be Empty...",Toast.LENGTH_SHORT).show()
                            }else{
                                if(pass != enteredpass)
                                {
                                    Toast.makeText(requireContext(),"Password Not Matched..",Toast.LENGTH_SHORT).show()
                                }else{

                                    val userDeletionTasks = mutableListOf<Task<Void>>()


                                    FirebaseDatabase.getInstance().getReference("USERS").get().addOnSuccessListener { snapshot
                                        Log.d("Inside Get","yes")
                                        for (usersnapshot in snapshot.children)
                                        {
                                            val userID = usersnapshot.key ?: continue
                                            Log.d("Useridbyget", userID)
                                            val userhackathonref = usersnapshot.child("hackathons").child(hackathonID)

                                            if(userhackathonref.exists())
                                            {
                                                Log.d("pathin get","yes")

                                                val task=  FirebaseDatabase.getInstance().getReference("USERS")
                                                    .child(userID).child("hackathons").child(hackathonID).removeValue()
                                                    .addOnSuccessListener {
                                                        Log.d("Debug", "Marked event as deleted for user: $userID")
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.e("Debug", "Failed to update for user: $userID", e)
                                                    }
                                                userDeletionTasks.add(task)
                                            }
                                        }

                                    }
                                    Tasks.whenAllComplete(userDeletionTasks).addOnSuccessListener {
                                        database.child(hackathonID).removeValue()
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "SuccessFully Deleted The Event..",
                                                    Toast.LENGTH_SHORT
                                                ).show()


                                                requireActivity().finish()
                                            }.addOnFailureListener { e ->
                                            Toast.makeText(requireContext(), "Failed to delete event: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }.addOnFailureListener { e ->
                                        Log.e("Debug", "Failed to get users", e)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        return binding.root
    }



}