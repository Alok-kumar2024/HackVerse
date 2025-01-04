package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.hackverse.databinding.ActivityViewProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewProfile : AppCompatActivity() {

    private lateinit var binding : ActivityViewProfileBinding
    private lateinit var database : DatabaseReference

    private lateinit var ViewingUserID : String
    private lateinit var ViewersID : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.ButtonBackViewDetails.setOnClickListener {
            finish()
        }


        ViewingUserID = (intent.getStringExtra("ViewingUserID") ?: "Not Got").toString()
        Log.d("ViewingUSerID","The Viewing user's Id is $ViewingUserID")

        ViewersID= (intent.getStringExtra("CurrentViewersID") ?: "Not Got").toString()
        Log.d("Viewer'sUSerID","The Viewer's user's Id is $ViewersID")

//        binding.ButtonMessageViewProfile.setOnClickListener {
//
//            val intent = Intent(this,messageActivity::class.java)
//            intent.putExtra("ViewingID",ViewingUserID)
//            intent.putExtra("ViewersID",ViewersID)
//            startActivity(intent)
//
//        }

        binding.ButtonCreatedHackathonsViewProfile.setOnClickListener {

            val intent = Intent(this,ViewCreatedHackathon::class.java)
            intent.putExtra("ViewingID",ViewingUserID)
            intent.putExtra("ViewersID",ViewersID)
            startActivity(intent)

        }


        database = FirebaseDatabase.getInstance().getReference("USERS")

        database.child(ViewingUserID).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("InsideDataChangeViewProfile","Yes")

                if (snapshot.exists())
                {
                    Log.d("Snapshot Exists in view profile","yes")

                    val profileURL = (snapshot.child("url").value ?: "None").toString()
                    val userName = (snapshot.child("username").value ?: "No username").toString()

                    Log.d("ViewingUSersData","The userID -> $ViewingUserID" +
                            "The UserName -> $userName" +
                            "The profileurl -> $profileURL")

                    binding.ShowUSERIDViewProfile.text = ViewingUserID
                    binding.ShowUSERNameViewProfile.text = userName
                    if (!isFinishing && !isDestroyed) {
                        Glide.with(this@ViewProfile)
                            .load(profileURL)
                            .error(R.drawable.default_profile_pic_vector)
                            .placeholder(R.drawable.loading_for_image_vector)
                            .into(binding.ViewProfileImageView)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



    }
}