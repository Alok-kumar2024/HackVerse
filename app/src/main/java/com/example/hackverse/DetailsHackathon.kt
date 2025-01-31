package com.example.hackverse

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hackverse.databinding.ActivityDetailsHackathonBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.apache.commons.collections.set.MapBackedSet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailsHackathon : AppCompatActivity() {
    private lateinit var binding : ActivityDetailsHackathonBinding

    private lateinit var database : DatabaseReference

    private lateinit var recyclerviewcomment : RecyclerView
    private lateinit var recyclerviewcomment_adapter : Comments_Adapter
    private var Comment_List = arrayListOf<Comment_recycler>()

    private var CommentKey_List = mutableListOf<String>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailsHackathonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val HackathonEventId = intent.getStringExtra("HackathonEventID") ?: "Not Got"
        Log.d("HackathonEventIDFromAdapter","The Event Id Got FromFunction is $HackathonEventId")

        val CurrentUserID = intent.getStringExtra("CurrentUserID") ?: "Not Got"

        Log.d("CurrentUserIDInDetailsActivity","The Current user is $CurrentUserID")

        database = FirebaseDatabase.getInstance().getReference("HACKATHON")

        binding.BookMarkRemoveDetailsHackathon.visibility = View.GONE
        binding.ButtonAfterLikeDetailsHackathon.visibility = View.GONE

        binding.buttonBackDetailsHackathon.setOnClickListener {
            finish()
        }

//        FirebaseDatabase.getInstance().getReference("USERS").child(CurrentUserID).child("hackathons").child(HackathonEventId)
//            .addListenerForSingleValueEvent(object : ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if(snapshot.exists())
//                    {
//                        val eventstatus = snapshot.child("EventStatus").value.toString()
//
//                        if(eventstatus=="Created")
//                        {
//                            binding.buttonSetting.setOnClickListener { view->
//
//                                val popup = PopupMenu(this@DetailsHackathon,view)
//                                popup.menuInflater.inflate(R.menu.details_hackathon_menu,popup.menu)
//                                popup.setOnMenuItemClickListener { item ->
//
//                                    when(item.itemId)
//                                    {
//                                        R.id.Edit_DetailsHackathon -> {
//                                            Toast.makeText(this@DetailsHackathon,"Clicked Edit",Toast.LENGTH_SHORT).show()
//                                            true
//                                        }
//
//                                        R.id.Participants_DetailsHackathon -> {
//
//                                        }
//                                    }
//
//
//                                }
//                            }
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//            })


        database.child(HackathonEventId).child("time").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    val startDate = snapshot.child("activeDate").value.toString()
                    val startTime = snapshot.child("activeTime").value.toString()
                    val endDate = snapshot.child("closedDate").value.toString()
                    val endTime = snapshot.child("closeTime").value.toString()

                    if (endDate.isNotEmpty() && endTime.isNotEmpty()) {
                        val combinedDateTime = "$endDate $endTime"

                        val dateFormat = SimpleDateFormat("yyyy-M-d HH:mm", Locale.getDefault())
                        try {
                            val closeDate = dateFormat.parse(combinedDateTime)
                            val closeTimestamp = closeDate?.time ?: 0L

                            // Compare with current time
                            if (System.currentTimeMillis() > closeTimestamp) {
                                // Time has passed, update Firebase or UI
                                val update = mapOf(
                                    "status" to "Inactive"
                                )
                                database.child(HackathonEventId).updateChildren(update)

                                binding.buttonRegisterDetailsHackathon.isEnabled = false
                                Toast.makeText(this@DetailsHackathon,"Registration Already Ended",Toast.LENGTH_SHORT).show()
                            }
                        }catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }else if (startTime.isNotEmpty() && startDate.isNotEmpty())
                    {
                        val combinedDateTimeStart = "$startDate $startTime"
                        val dateFormat = SimpleDateFormat("yyyy-M-d HH:mm", Locale.getDefault())
                        try {
                            val startDateTime = dateFormat.parse(combinedDateTimeStart)
                            val startTimestamp = startDateTime?.time ?: 0L
                            if (System.currentTimeMillis() > startTimestamp) {
                                // Time has passed, update Firebase or UI
                                val updates = mapOf(
                                    "status" to "Active"
                                )
                                database.child(HackathonEventId).updateChildren(updates).addOnSuccessListener {
                                    Log.d("Changed To Inactive","Successfully done")
                                }.addOnFailureListener { Log.d("Couldn't Change To Inactive","Coludn't do") }
                                binding.buttonRegisterDetailsHackathon.isEnabled = true
                            }
                        }catch (e : Exception){e.printStackTrace()}


                    }else if (startTime.isNotEmpty() && startDate.isNotEmpty()) {
                    val combinedDateTimeStart = "$startDate $startTime"
                    val dateFormat = SimpleDateFormat("yyyy-M-d HH:mm", Locale.getDefault())
                    try {
                        val startDateTime = dateFormat.parse(combinedDateTimeStart)
                        val startTimestamp = startDateTime?.time ?: 0L
                        if (System.currentTimeMillis() < startTimestamp) {
                            // Time has passed, update Firebase or UI
                            val updates = mapOf(
                                "status" to "None"
                            )
                            database.child(HackathonEventId).updateChildren(updates)
                                .addOnSuccessListener {
                                    Log.d("Changed To None", "Successfully done")
                                }.addOnFailureListener {
                                Log.d(
                                    "Couldn't Change To None.",
                                    "Coludn't do"
                                )
                            }
                            binding.buttonRegisterDetailsHackathon.isEnabled = false
                            Toast.makeText(this@DetailsHackathon,"Registration Not Started Yet.",Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.buttonRegisterDetailsHackathon.setOnClickListener {
            val userDatabase = FirebaseDatabase.getInstance()

            userDatabase.getReference("USERS").child(CurrentUserID).child("hackathons")
                .child(HackathonEventId).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                       if (snapshot.exists())
                       {
                           val EventStatus = snapshot.child("EventStatus").value ?: "No Value"

                           if (EventStatus == "Created")
                           {
                               Toast.makeText(this@DetailsHackathon,"You Can't Join Your Own Event",Toast.LENGTH_SHORT).show()


                           }else if(EventStatus == "Registered")
                           {
                               Toast.makeText(this@DetailsHackathon,"Already Registered.",Toast.LENGTH_SHORT).show()
                           }else{
                               val setStatus = mapOf(
                                   "EventStatus" to "Registered"
                               )
                               userDatabase.getReference("USERS").child(CurrentUserID)
                                   .child("hackathons").child(HackathonEventId).updateChildren(setStatus).addOnSuccessListener {
                                       Toast.makeText(this@DetailsHackathon,"SuccessFully Registered",Toast.LENGTH_SHORT).show()
                                   }.addOnFailureListener {
                                       Toast.makeText(this@DetailsHackathon,"Error : Couldn't Register",Toast.LENGTH_SHORT).show()
                                   }

                               @SuppressLint("SimpleDateFormat")
                               fun getCurrentDateTime(): String {
                                   val currentDate = Date() // Get the current date and time
                                   val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // Define your date format
                                   return formatter.format(currentDate) // Format the date into a string
                               }
                               val CurrentTime = getCurrentDateTime()


                               val update = mapOf(
                                   "participationTime" to CurrentTime
                               )

                               database.child(HackathonEventId).child("participants").child(CurrentUserID).updateChildren(update)
                                   .addOnSuccessListener {
                                       Log.d("Participants","Successfully added to firebase")
                                   }.addOnFailureListener {
                                       Log.d("Participants","Error, Couldn't add")
                                   }
                           }
                       }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseErrorregister", "Error: ${error.message}")
                    }

                })

        }
        database.child(HackathonEventId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    val creator = snapshot.child("createdBy").value.toString()

                    if (creator == CurrentUserID)
                    {
                        binding.buttonSetting.visibility = View.VISIBLE
                        binding.buttonSetting.setOnClickListener { view->

                            val popup = PopupMenu(this@DetailsHackathon,view)
                            popup.menuInflater.inflate(R.menu.details_hackathon_menu,popup.menu)
                            popup.setOnMenuItemClickListener { item ->

                                when(item.itemId)
                                {
                                    R.id.Edit_DetailsHackathon -> {
                                        val intent = Intent(this@DetailsHackathon,Edit_Hackathon::class.java)
                                        intent.putExtra("HackathonID",HackathonEventId)
                                        intent.putExtra("currentuserid",CurrentUserID)
                                        startActivity(intent)
                                        true
                                    }

                                    R.id.Participants_DetailsHackathon -> {

                                        val intent = Intent(this@DetailsHackathon,ParticipantsList::class.java)
                                        intent.putExtra("HackathonID",HackathonEventId)
                                        intent.putExtra("currentuserid",CurrentUserID)
                                        startActivity(intent)

                                        true

                                    }

//                                    R.id.Delete_DetailsHackathons -> {
//                                        val frag = deletehackathon()
//
//                                        val bundle = Bundle()
//                                        bundle.putString("UserId",CurrentUserID)
//                                        bundle.putString("HackathonID",HackathonEventId)
//                                        frag.arguments = bundle
//
//                                        val Frame = binding.FrameContainerDeleteHackathon
//
//                                        supportFragmentManager.beginTransaction().replace(R.id.Frame_container_DeleteHackathon,frag)
//                                            .addToBackStack(null).commit()
//
//                                        Frame.visibility = View.VISIBLE
//
//                                        true
//                                    }
                                    else->false
                                }


                            }
                            popup.show()
                        }
//                        binding.ButtonParticipantsDetailsHackathon.visibility = View.VISIBLE
//                        binding.ButtonParticipantsDetailsHackathon.setOnClickListener {
//                            val intent = Intent(this@DetailsHackathon,ParticipantsList::class.java)
//                            intent.putExtra("HackathonID",HackathonEventId)
//                            intent.putExtra("currentuserid",CurrentUserID)
//                            startActivity(intent)
//                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseErrorregister", "Error: ${error.message}")
            }

        })


        if (HackathonEventId == "Not Got")
        {
            Toast.makeText(this,"Couldn't Find EventID",Toast.LENGTH_SHORT).show()
        }else {

            database.child(HackathonEventId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.EventIDShowDetailsHackathon.text = HackathonEventId
                        val bannerurl = snapshot.child("bannerUrl").value ?: "No URL"
                        val eventname = snapshot.child("eventName").value ?: "No EventName"
                        val host = snapshot.child("hostedBy").value ?: "No Host"
                        val prize = snapshot.child("prize").value ?: "No Prize"
                        val startDate = snapshot.child("time").child("activeDate").value.toString()
                        val startTime = snapshot.child("time").child("activeTime").value.toString()
                        val endDate = snapshot.child("time").child("closedDate").value.toString()
                        val endTime = snapshot.child("time").child("closeTime").value.toString()
                        val description = snapshot.child("description").value ?: "No Description"

//                        val BookMark = snapshot.child("BookMark").value as? Map<*, *>
//                        val Numberbookmark = BookMark?.size ?: 0

                        val Vote = snapshot.child("votes").child("upvotes").value


                        Log.d("DetailsOfHackathon","EventID -> $HackathonEventId " +
                                "BannerURL -> $bannerurl " +
                                "EventName -> $eventname " +
                                "Host -> $host " +
                                "prize -> $prize " +
                                "startDate -> $startDate " +
                                "startTime -> $startTime " +
                                "endDate -> $endDate " +
                                "endTime -> $endTime " +
//                                "Number Of Bookmark -> $Numberbookmark " +
                                "Number Of Votes -> $Vote " +
                                "description -> $description .")

                        binding.EventNameShowDetailsHackathon.text = eventname.toString()
                        binding.EventHostNameDetailHackathon.text = host.toString()
                        binding.EventPrizeShowDetailsHackathon.text = prize.toString()
                        binding.ShowStartDateTimeDetailsHackathon.text = listOf(startDate,startTime).joinToString(" - ")
                        binding.ShowEndDateTimeDetailsHackathon.text = listOf(endDate,endTime).joinToString(" - ")
                        binding.ShowDescriptionDetailsHackathon.text = description.toString()
//                        binding.NumberOfBookMarkedDetailsHackathon.text = Numberbookmark.toString()
                        binding.NumberOfVotesDetailsHackathon.text = Vote.toString()

                        if (!isFinishing && !isDestroyed) {
                            Glide.with(this@DetailsHackathon)
                                .load(bannerurl)
                                .error(R.drawable.default_image_of_profile)
                                .placeholder(R.drawable.loading_for_image_vector)
                                .into(binding.BannerImageViewDetailsHackathon)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError1", "Error: ${error.message}")
                }

            })

            database.child(HackathonEventId).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        val Bookmarkstatus = snapshot.child("BookMark").child(CurrentUserID).getValue(String::class.java) ?: "None"
                        val VoteStatus = snapshot.child("votes").child("upvoted").child(CurrentUserID).getValue(String::class.java) ?: "None"

                        if (Bookmarkstatus == "Added")
                        {
                            binding.BookMarkAddDetailsHackathon.visibility = View.GONE
                            binding.BookMarkRemoveDetailsHackathon.visibility = View.VISIBLE

                            binding.BookMarkRemoveDetailsHackathon.setOnClickListener {

                                val ref = FirebaseDatabase.getInstance().getReference("USERS")
                                val HackathonRef = FirebaseDatabase.getInstance().getReference("HACKATHON")

                                val update = mapOf(
                                    "bookmark" to "Removed"
                                )
                                ref.child(CurrentUserID).child("hackathons").child(HackathonEventId).updateChildren(update).addOnSuccessListener {
                                    Toast.makeText(this@DetailsHackathon,"Successfully Removed From Bookmark",Toast.LENGTH_SHORT).show()


                                }.addOnFailureListener {
                                    Toast.makeText(this@DetailsHackathon,"Error : Couldn't Removed From Bookmark",Toast.LENGTH_SHORT).show()
                                }

                                val updateBookmark = mapOf(
                                    "BookMark/$CurrentUserID" to ""
                                )

                                HackathonRef.child(HackathonEventId).updateChildren(updateBookmark).addOnSuccessListener {
                                    Log.d("BookMarkRemoved","Successfully Removed in HACKATHON")
                                }.addOnFailureListener {
                                    Log.d("BookMarkNotRemoved","Couldn't  Remove in HACKATHON")
                                }

                            }
                        }else if( Bookmarkstatus =="" || Bookmarkstatus == "None")
                        {
                            binding.BookMarkAddDetailsHackathon.visibility = View.VISIBLE
                            binding.BookMarkRemoveDetailsHackathon.visibility = View.GONE

                            binding.BookMarkAddDetailsHackathon.setOnClickListener {

                                val ref = FirebaseDatabase.getInstance().getReference("USERS")
                                val HackathonRef = FirebaseDatabase.getInstance().getReference("HACKATHON")

                                val update = mapOf(
                                    "bookmark" to "Added"
                                )
                                ref.child(CurrentUserID).child("hackathons").child(HackathonEventId).updateChildren(update).addOnSuccessListener {
                                    Toast.makeText(this@DetailsHackathon,"Succesfully Added To BookMark.",Toast.LENGTH_SHORT).show()

                                }.addOnFailureListener {
                                    Toast.makeText(this@DetailsHackathon,"Error : Couldn't Added To BookMark.",Toast.LENGTH_SHORT).show()
                                }
                                val updateBookmark = mapOf(
                                    "BookMark/$CurrentUserID" to "Added"
                                )

                                HackathonRef.child(HackathonEventId).updateChildren(updateBookmark).addOnSuccessListener {
                                    Log.d("BookMarkAdded","Successfully added in HACKATHON")
                                }.addOnFailureListener {
                                    Log.d("BookMarkNotAdded","Couldn't  add in HACKATHON")
                                }
                            }
                        }else{
                            binding.BookMarkAddDetailsHackathon.visibility = View.VISIBLE
                            binding.BookMarkRemoveDetailsHackathon.visibility = View.GONE
                        }

                        if (VoteStatus == "yes")
                        {
                            binding.ButtonBeforeLikeDetailsHackathon.visibility = View.GONE
                            binding.ButtonAfterLikeDetailsHackathon.visibility = View.VISIBLE

                            binding.ButtonAfterLikeDetailsHackathon.setOnClickListener {
                                val ref = FirebaseDatabase.getInstance().getReference("HACKATHON")

                                ref.child(HackathonEventId)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        @SuppressLint("NotifyDataSetChanged")
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                val upvote =
                                                    snapshot.child("votes").child("upvotes")
                                                        .getValue(Int::class.java) ?: 0

                                                val update = mapOf(
                                                    "upvotes" to upvote - 1
                                                )

                                                ref.child(HackathonEventId).child("votes")
                                                    .updateChildren(update).addOnSuccessListener {
                                                    Toast.makeText(
                                                        this@DetailsHackathon,
                                                        "Successfully Removed Upvoted.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }.addOnFailureListener {
                                                    Toast.makeText(
                                                        this@DetailsHackathon,
                                                        "Error Couldn't Remove Upvote",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                                val update_voted = mapOf(
                                                    "upvoted/$CurrentUserID" to "no"
                                                )

                                                ref.child(HackathonEventId).child("votes")
                                                    .updateChildren(update_voted)
                                                    .addOnSuccessListener {

                                                        Log.d(
                                                            "votedText",
                                                            "Successfully added 'no' in HACKATHON"
                                                        )
                                                    }.addOnFailureListener {
                                                    Log.d(
                                                        "votedText",
                                                        "Couldn't added in HACKATHON"
                                                    )
                                                }
                                            }

                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Log.e("FirebaseError2", "Error: ${error.message}")
                                        }

                                    })
                            }
                        }else if(VoteStatus == "no" || VoteStatus == "None")
                        {
                            binding.ButtonBeforeLikeDetailsHackathon.visibility = View.VISIBLE
                            binding.ButtonAfterLikeDetailsHackathon.visibility = View.GONE

                            binding.ButtonBeforeLikeDetailsHackathon.setOnClickListener {

                                val ref = FirebaseDatabase.getInstance().getReference("HACKATHON")


                                ref.child(HackathonEventId).addListenerForSingleValueEvent(object : ValueEventListener{
                                    @SuppressLint("NotifyDataSetChanged")
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists())
                                        {
                                            val upvote = snapshot.child("votes").child("upvotes").getValue(Int::class.java) ?: 0

                                            val update = mapOf(
                                                "upvotes" to  upvote+1
                                            )

                                            ref.child(HackathonEventId).child("votes").updateChildren(update).addOnSuccessListener {
                                                Toast.makeText(this@DetailsHackathon,"Successfully Upvoted.",Toast.LENGTH_SHORT).show()
                                            }.addOnFailureListener {
                                                Toast.makeText(this@DetailsHackathon,"Error Couldn't Upvote",Toast.LENGTH_SHORT).show()
                                            }

                                            val update_voted = mapOf(
                                                "upvoted/$CurrentUserID" to "yes"
                                            )

                                            ref.child(HackathonEventId).child("votes").updateChildren(update_voted).addOnSuccessListener {


                                                Log.d("votedText","Successfully added 'yes' in HACKATHON")
                                            }.addOnFailureListener {
                                                Log.d("votedText","Couldn't added in HACKATHON")
                                            }
                                        }

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e("FirebaseError3", "Error: ${error.message}")
                                    }

                                })
                            }
                        }else{
                            binding.ButtonBeforeLikeDetailsHackathon.visibility = View.VISIBLE
                            binding.ButtonAfterLikeDetailsHackathon.visibility = View.GONE
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError4", "Error: ${error.message}")
                }

            })



        }

        database.child(HackathonEventId).child("comment").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    snapshot.children.forEach { CommentUserID ->
                        val commentkey = CommentUserID.key.toString()
                        Log.d("Comentkey","The comment key is $commentkey")

                        if (commentkey.isNotEmpty())
                        {
                            CommentKey_List.add(commentkey)
                            Log.d("AddToCommentList","yes")
                        }
                    }

                    if (CommentKey_List.isEmpty())
                    {
                        Log.d("Comment","Empty , No comments")
                    }else{
                        val comment_TempList = mutableListOf<Comment_recycler>()

                        var check = 0

                        for(index in CommentKey_List.indices)
                        {

                            val CommentKEY = CommentKey_List[index]
                            Log.d("commentKeyFromList","The comment key from list is $CommentKEY")

                            database.child(HackathonEventId).child("comment").child(CommentKEY).addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists())
                                    {
                                        val commentuserID = (snapshot.child("userIDcomment").value ?: "No UserId").toString()
                                        val commentText = (snapshot.child("commenttext").value ?: "No CommentText" ).toString()
                                        val timestamp = (snapshot.child("times").value ?: 0).toString().toLong()
                                        Log.d("CommentData","UserId -> $commentuserID" +
                                                "Comment Text -> $commentText" +
                                                "TimeStamp")

                                        FirebaseDatabase.getInstance().getReference("USERS").child(commentuserID)
                                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists())
                                                {
                                                    val username = snapshot.child("username").value.toString()
                                                    val profileurl = snapshot.child("url").value.toString()

                                                    Log.d("CommentDatawithUserName","UserId -> $commentuserID" +
                                                            "Comment Text -> $commentText" +
                                                            "UserNamme -> $username" +
                                                            "profileURL -> $profileurl")

                                                    val commentData = Comment_recycler(profileurl,commentuserID,username,commentText,timestamp)
                                                    if(!comment_TempList.contains(commentData))
                                                    {
                                                        comment_TempList.add(commentData)
//                                                        recyclerviewcomment_adapter.notifyDataSetChanged()
//                                                        Log.d("AddedTo recyclerList of comment","Yes")
                                                    }
                                                    check ++

                                                    if (check == CommentKey_List.size)
                                                    {
                                                        comment_TempList.sortByDescending { it.TimeStamp }
                                                        Comment_List.clear()
                                                        Comment_List.addAll(comment_TempList)
                                                        recyclerviewcomment_adapter.notifyDataSetChanged()
                                                        Log.d("AddedTo recyclerList of comment","Yes")

                                                    }
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                Log.e("FirebaseError5", "Error: ${error.message}")
                                            }

                                        })
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("FirebaseError6", "Error: ${error.message}")
                                }

                            })

                        }


                        LoadCommentRecyclerView(Comment_List)

                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError7", "Error: ${error.message}")
            }

        })



        binding.ImageButtonSendCommentDetailsHackathon.setOnClickListener {


            val EnterComment = binding.EditTextEnterCommentDetailsHackathon

            val commentKEY = "$CurrentUserID%${System.currentTimeMillis()}"
            Log.d("CustomCommentKey","The custom Key for comment is $commentKEY")

            if (EnterComment.text.toString().isEmpty())
            {
                Toast.makeText(this, "Comment cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (HackathonEventId.isEmpty()) {
                Log.e("HackathonEventIdError", "HackathonEventId is null or empty")
                return@setOnClickListener
            }
            if (commentKEY.isEmpty()) {
                Log.e("CommentKeyError", "commentKEY is null or empty")
                return@setOnClickListener
            }




            val updateData = Comments(CurrentUserID,EnterComment.text.toString(),System.currentTimeMillis().toString().toLong())
            val updateDatabase = FirebaseDatabase.getInstance().getReference("HACKATHON")

            updateDatabase.child(HackathonEventId).child("comment").child(commentKEY).setValue(updateData).addOnSuccessListener {
                Toast.makeText(this@DetailsHackathon, "Successfully Sent Comment", Toast.LENGTH_SHORT).show()
                EnterComment.text.clear()
                recyclerviewcomment_adapter.notifyDataSetChanged()
            }.addOnFailureListener {
                Toast.makeText(this, "Error: Couldn't Send Comment", Toast.LENGTH_SHORT).show()
            }
        }



    }

    private fun LoadCommentRecyclerView(commentList: ArrayList<Comment_recycler>) {

        recyclerviewcomment = binding.RecyclerViewShowCommentsDetailsHackathon
        recyclerviewcomment.layoutManager = LinearLayoutManager(this)

        recyclerviewcomment_adapter = Comments_Adapter(commentList)
        recyclerviewcomment.adapter = recyclerviewcomment_adapter

    }
}