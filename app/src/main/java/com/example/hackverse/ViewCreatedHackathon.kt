package com.example.hackverse

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hackverse.databinding.ActivityViewCreatedHackathonBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewCreatedHackathon : AppCompatActivity() {
    private lateinit var binding : ActivityViewCreatedHackathonBinding
    private lateinit var database : DatabaseReference

    private lateinit var recyclerViewHackathonsCreated : RecyclerView
    private lateinit var recyclerViewHackathons_adapterCreated : Hackathon_Adapter
    private var Hackathons_listCreated = arrayListOf<Hackathon_Recycler>()

    private lateinit var CreatedHackathonUsersID : String

    private lateinit var ViewingUsersID : String

    private var CreatedHackathonID_LIST = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewCreatedHackathonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.ButtonBackToViewProfile.setOnClickListener {
            finish()
        }

        CreatedHackathonUsersID = (intent.getStringExtra("ViewingID") ?: "Not Got").toString()
        Log.d("CreatedHackathonUsersID","The CreatedHackathonUsersID is $CreatedHackathonUsersID")

        ViewingUsersID = (intent.getStringExtra("ViewersID") ?: "Not Got" ).toString()
        Log.d("ViewingUsersID","The ViewingUsersID is $ViewingUsersID")

        database = FirebaseDatabase.getInstance().getReference("USERS")

        database.child(CreatedHackathonUsersID).child("hackathons").orderByChild("EventStatus")
            .equalTo("Created").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("InsideDataChangeCreatedHackathon","Yes")

                    if (snapshot.exists())
                    {
                        Log.d("InsidesnapshotexistsCreatedHackathon","Yes")

                        snapshot.children.forEach { createdID ->
                            val CreatedID = createdID.key ?: "Not Got"
                            Log.d("CreatedHackathonID","The Id is $CreatedID")

                            if (CreatedID != "Not Got")
                            {
                                if (!CreatedHackathonID_LIST.contains(CreatedID))
                                {
                                    CreatedHackathonID_LIST.add(CreatedID)
                                    Log.d("AddedToList","Yes")
                                }
                            }
                        }

                        if (CreatedHackathonID_LIST.isEmpty())
                        {
                            Toast.makeText(this@ViewCreatedHackathon,"Havent't Created Any Hackathons Yet",Toast.LENGTH_SHORT).show()
                        }else{

                            for(index in CreatedHackathonID_LIST.indices)
                            {
                                val createdHackathonId = CreatedHackathonID_LIST[index]
                                Log.d("createdHackathonIdFromList","ID from list is $createdHackathonId")

                                val SearchCreateDatabase = FirebaseDatabase.getInstance()

                                SearchCreateDatabase.getReference("HACKATHON").child(createdHackathonId)
                                    .addListenerForSingleValueEvent(object : ValueEventListener{
                                    @SuppressLint("NotifyDataSetChanged")
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists())
                                        {
                                            val eventname = snapshot.child("eventName").value.toString()
                                            val bannerURL = snapshot.child("bannerUrl").value.toString()
                                            val host = snapshot.child("hostedBy").value.toString()
                                            val prize = snapshot.child("prize").value.toString()

                                            val bookmark = snapshot.child("BookMark").child(ViewingUsersID).getValue(String::class.java) ?: "None"

                                            val VotedText = snapshot.child("votes").child("upvoted").child(ViewingUsersID).getValue(String::class.java) ?: "none"

                                            val status = snapshot.child("status").value.toString()
                                            val information_event = Hackathon_Recycler(bannerURL,createdHackathonId,eventname,host,prize,bookmark,VotedText,status)
                                            Log.d("HackathonIDData","EventName -> $eventname" +
                                                    "bannerurl -> $bannerURL" +
                                                    "host -> $host" +
                                                    "Prize -> $prize" +
                                                    "bookmark -> $bookmark" +
                                                    "Voted -> $VotedText")

                                            if (!Hackathons_listCreated.contains(information_event))
                                            {
                                                Hackathons_listCreated.add(information_event)
                                                recyclerViewHackathons_adapterCreated.notifyDataSetChanged()
                                            }

                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                            }

                            LoadCreatedHackathonList(Hackathons_listCreated)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


    }

    private fun LoadCreatedHackathonList(hackathonsListcreated: ArrayList<Hackathon_Recycler>) {

        recyclerViewHackathonsCreated = binding.RecyclerViewOfCreatedHackathonActivity
        recyclerViewHackathonsCreated.layoutManager = LinearLayoutManager(this)

        recyclerViewHackathons_adapterCreated = Hackathon_Adapter(hackathonsListcreated ,
            OnBookmarkClickAdd = { hackathon -> BookMarkAdded(hackathon) },
            OnBookmarkClickRemove = { hackathon -> BookMarkRemove(hackathon) },
            OnLikeClickAdd = { hackathon -> UpvoteAdd(hackathon) },
            OnLikeClickRemove = { hackathon -> UpvoteRemove(hackathon) },
            OnDetailsClick = { hackathon -> GoToDetailsActivity(hackathon)}
            )
        recyclerViewHackathonsCreated.adapter = recyclerViewHackathons_adapterCreated

    }
    private fun GoToDetailsActivity(hackathon: Hackathon_Recycler) {

        val intent = Intent(this,DetailsHackathon::class.java)
        intent.putExtra("HackathonEventID",hackathon.EventId)
        intent.putExtra("CurrentUserID",ViewingUsersID)
        startActivity(intent)

    }

    private fun BookMarkRemove(hackathon: Hackathon_Recycler) {

        val HACKATHONID = hackathon.EventId.toString()

        val ref = FirebaseDatabase.getInstance().getReference("USERS")
        val HackathonRef = FirebaseDatabase.getInstance().getReference("HACKATHON")

        val update = mapOf(
            "bookmark" to "Removed"
        )
        ref.child(ViewingUsersID).child("hackathons").child(HACKATHONID).updateChildren(update).addOnSuccessListener {
            Toast.makeText(this,"Successfully Removed From Bookmark",Toast.LENGTH_SHORT).show()


        }.addOnFailureListener {
            Toast.makeText(this,"Error : Couldn't Removed From Bookmark",Toast.LENGTH_SHORT).show()
        }

        val updateBookmark = mapOf(
            "BookMark/$ViewingUsersID" to ""
        )

        HackathonRef.child(HACKATHONID).updateChildren(updateBookmark).addOnSuccessListener {
            Log.d("BookMarkRemoved","Successfully Removed in HACKATHON")
        }.addOnFailureListener {
            Log.d("BookMarkNotRemoved","Couldn't  Remove in HACKATHON")
        }

    }

    private fun BookMarkAdded(hackathon: Hackathon_Recycler) {

        val HACKATHONID = hackathon.EventId.toString()

        val ref = FirebaseDatabase.getInstance().getReference("USERS")
        val HackathonRef = FirebaseDatabase.getInstance().getReference("HACKATHON")

        val update = mapOf(
            "bookmark" to "Added"
        )
        ref.child(ViewingUsersID).child("hackathons").child(HACKATHONID).updateChildren(update).addOnSuccessListener {
            Toast.makeText(this,"Succesfully Added To BookMark.",Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {
            Toast.makeText(this,"Error : Couldn't Added To BookMark.",Toast.LENGTH_SHORT).show()
        }
        val updateBookmark = mapOf(
            "BookMark/$ViewingUsersID" to "Added"
        )

        HackathonRef.child(HACKATHONID).updateChildren(updateBookmark).addOnSuccessListener {
            Log.d("BookMarkAdded","Successfully added in HACKATHON")
        }.addOnFailureListener {
            Log.d("BookMarkNotAdded","Couldn't  add in HACKATHON")
        }

    }

    private fun UpvoteRemove(hackathon: Hackathon_Recycler) {

        val ref = FirebaseDatabase.getInstance().getReference("HACKATHON")

        val HACKATHONID = hackathon.EventId.toString()

        ref.child(HACKATHONID).addListenerForSingleValueEvent(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    val upvote = snapshot.child("votes").child("upvotes").getValue(Int::class.java) ?: 0

                    val update = mapOf(
                        "upvotes" to  upvote-1
                    )

                    ref.child(HACKATHONID).child("votes").updateChildren(update).addOnSuccessListener {
                        Toast.makeText(this@ViewCreatedHackathon,"Successfully Removed Upvoted.",Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this@ViewCreatedHackathon,"Error Couldn't Remove Upvote",Toast.LENGTH_SHORT).show()
                    }

                    val update_voted = mapOf(
                        "upvoted/$ViewingUsersID" to "no"
                    )

                    ref.child(HACKATHONID).child("votes").updateChildren(update_voted).addOnSuccessListener {

                        hackathon.Voted = "no"
                        recyclerViewHackathons_adapterCreated.notifyDataSetChanged()
                        Log.d("votedText","Successfully added 'no' in HACKATHON")
                    }.addOnFailureListener {
                        Log.d("votedText","Couldn't added in HACKATHON")
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun UpvoteAdd(hackathon: Hackathon_Recycler ) {
        val ref = FirebaseDatabase.getInstance().getReference("HACKATHON")

        val HACKATHONID = hackathon.EventId.toString()

        ref.child(HACKATHONID).addListenerForSingleValueEvent(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    val upvote = snapshot.child("votes").child("upvotes").getValue(Int::class.java) ?: 0

                    val update = mapOf(
                        "upvotes" to  upvote+1
                    )

                    ref.child(HACKATHONID).child("votes").updateChildren(update).addOnSuccessListener {
                        Toast.makeText(this@ViewCreatedHackathon,"Successfully Upvoted.",Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this@ViewCreatedHackathon,"Error Couldn't Upvote",Toast.LENGTH_SHORT).show()
                    }

                    val update_voted = mapOf(
                        "upvoted/$ViewingUsersID" to "yes"
                    )

                    ref.child(HACKATHONID).child("votes").updateChildren(update_voted).addOnSuccessListener {

                        hackathon.Voted = "yes"
                        recyclerViewHackathons_adapterCreated.notifyDataSetChanged()

                        Log.d("votedText","Successfully added 'yes' in HACKATHON")
                    }.addOnFailureListener {
                        Log.d("votedText","Couldn't added in HACKATHON")
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}