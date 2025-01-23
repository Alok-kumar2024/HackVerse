package com.example.hackverse

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony.Mms.Part
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hackverse.databinding.ActivityParticipantsListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ParticipantsList : AppCompatActivity() {
    private lateinit var binding : ActivityParticipantsListBinding
    private lateinit var database : DatabaseReference

    private lateinit var currentuserid : String
    private lateinit var recyclerview_participants : RecyclerView
    private lateinit var recyclerview_participants_adapter : participants_adapter
    private var Participants_List_Show = arrayListOf<participants_recycler>()

    private var participants_add_userID = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityParticipantsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val HackathonID = intent.getStringExtra("HackathonID") ?: "Not Got"
        Log.d("HackathonEventIDFromDetails","The Event Id Got FromFunction is $HackathonID")

        currentuserid = (intent.getStringExtra("currentuserid") ?: "Not Got").toString()
        Log.d("CurrentUserViewingDetails","The user Id Got FromFunction is $currentuserid")


        database = FirebaseDatabase.getInstance().getReference("HACKATHON")

        binding.BackToDetailsHackathon.setOnClickListener {
            finish()
        }

        database.child(HackathonID).child("participants").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Checkoverride","inside override datachange")
                if (snapshot.exists())
                {
                    Log.d("checksnapshot exists","yes , inside")

                    snapshot.children.forEach { participanted ->
                        val participantsUSERID = participanted.key.toString()
                        Log.d("PArticipants USERID ","User id of participants is $participantsUSERID")

                        if (participantsUSERID.isNotEmpty())
                        {
                            if (!participants_add_userID.contains(participantsUSERID))
                            {
                                participants_add_userID.add(participantsUSERID)
                                Log.d("Added To participants added list","Yes , added $participantsUSERID")
                            }
                        }
                    }

                    if (participants_add_userID.isEmpty())
                    {
                        Toast.makeText(this@ParticipantsList,"No Participants Yet.",Toast.LENGTH_SHORT).show()
                    }else{

                        for (index in participants_add_userID.indices)
                        {
                            val ParticipantsUserID = participants_add_userID[index]
                            Log.d("articipantsUserId from list","The user id from list is $ParticipantsUserID")

                            val participantdatabase = FirebaseDatabase.getInstance()

                            participantdatabase.getReference("USERS").child(ParticipantsUserID).addListenerForSingleValueEvent(object : ValueEventListener{
                                @SuppressLint("NotifyDataSetChanged")
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    Log.d("CheckoverrideInUSer","inside override datachange")

                                    if (snapshot.exists())
                                    {
                                        Log.d("checksnapshot existsforUser","yes , inside")

                                        val participantsUserName = (snapshot.child("username").value ?: "No UserName").toString()
                                        val participantsprofileURL =(snapshot.child("url").value ?: "No url").toString()
                                        val participantsemail = (snapshot.child("email").value ?: "No Email").toString()

                                        Log.d("PArticipantedUSerSDetails","The userID -> $ParticipantsUserID" +
                                                "The UserName -> $participantsUserName" +
                                                "The Profile Url -> $participantsprofileURL")

                                        val participants_data = participants_recycler(ParticipantsUserID,participantsUserName,participantsprofileURL,participantsemail)

                                        if (!Participants_List_Show.contains(participants_data))
                                        {
                                            Participants_List_Show.add(participants_data)
                                            Log.d("Data Added To recyclerview","Yes Added")
                                            recyclerview_participants_adapter.notifyDataSetChanged()
                                        }
                                    }


                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("FirebaseError", "Error: ${error.message}")
                                }

                            })
                        }
                        LoadParticipantsRecycler(Participants_List_Show)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error: ${error.message}")
            }

        })

    }

    private fun LoadParticipantsRecycler(participantsListShow: ArrayList<participants_recycler>) {

        recyclerview_participants = binding.RecyclerViewOfParticipantsListActivity
        recyclerview_participants.layoutManager = LinearLayoutManager(this)

        recyclerview_participants_adapter = participants_adapter(participantsListShow ,
            onClickViewProfile = {participant -> ViewProfileOfParticipant(participant)} ,
//            onClickMessageButton = {participant ->MessageFun(participant)}
        )

        recyclerview_participants.adapter = recyclerview_participants_adapter

    }

//    private fun MessageFun(participant: participants_recycler) {
//        val intent = Intent(this,messageActivity::class.java)
//        intent.putExtra("ViewingID",participant.userIdParticipants)
//        intent.putExtra("ViewersID",currentuserid)
//
//    }

    private fun ViewProfileOfParticipant(participant: participants_recycler) {
        val intent = Intent(this,ViewProfile::class.java)
        intent.putExtra("ViewingUserID",participant.userIdParticipants)
        intent.putExtra("CurrentViewersID",currentuserid)
        startActivity(intent)

    }
}