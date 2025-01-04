package com.example.hackverse

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hackverse.databinding.ActivityFriendRequestBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendRequestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFriendRequestBinding
    private lateinit var database : DatabaseReference

    private lateinit var recyclerViewIncoming : RecyclerView
    private lateinit var recyclerViewIncoming_Adapter : Friends_Adaptar
    private var Friend_Incoming_List = arrayListOf<Friends_Recycler>()

    private lateinit var CurrentUSerID : String

    private var Incoming_List = mutableListOf<String>()
    private var OutGoing_List = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityFriendRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.BackToFriendFragmentFromFriendList.setOnClickListener {
            finish()
        }

        CurrentUSerID = intent.getStringExtra("CurrentUSERID") ?: "Not Got"
        Log.d("UserIdFromFriendFragment","The current user id from friend fragment to friend request activity is $CurrentUSerID ")

        database = FirebaseDatabase.getInstance().getReference("USERS")

        database.child(CurrentUSerID).child("friends").orderByChild("status")
            .equalTo("incoming").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        snapshot.children.forEach { incomingfriend ->
                            val incomingUserID = incomingfriend.key ?:"Not Got"
                            Log.d("Incoming friendsID","The UserID of incoming friends request is $incomingUserID")

                            if (incomingUserID != "Not Got") {
                                if (!Incoming_List.contains(incomingUserID)) {
                                    Incoming_List.add(incomingUserID)
                                    Log.d("AddedToIncomingList","Yes , added $incomingUserID")
                                }
                            }
                        }
                        if (Incoming_List.isEmpty())
                        {
                            Toast.makeText(this@FriendRequestActivity,"No Incoming Friend Request",Toast.LENGTH_SHORT).show()
                        }else{

                            for (index in Incoming_List.indices)
                            {
                                val incomingFriendID = Incoming_List[index]
                                Log.d("IncomingFriendId","The ID is $incomingFriendID")

                                database.child(incomingFriendID).addListenerForSingleValueEvent(object : ValueEventListener{
                                    @SuppressLint("NotifyDataSetChanged")
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists())
                                        {
                                            val urlIncoming = snapshot.child("url").value.toString()
                                            val usernameIncoming = snapshot.child("username").value.toString()

                                            Log.d("DataIncomingFriends","The url is $urlIncoming " +
                                                    "The Username is $usernameIncoming " +
                                                    "The UserID is $incomingFriendID")

                                            val data_incomingFriend = Friends_Recycler(
                                                urlIncoming,
                                                incomingFriendID,
                                                usernameIncoming,
                                                "incoming"
                                            )

                                            if (!Friend_Incoming_List.contains(data_incomingFriend))
                                            {
                                                Friend_Incoming_List.add(data_incomingFriend)
                                                recyclerViewIncoming_Adapter.notifyItemInserted(Friend_Incoming_List.size-1)
                                            }

                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e("FirebaseError", error.message, error.toException())
                                    }

                                })
                            }
                            LoadRecyclerView(Friend_Incoming_List)
                        }


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", error.message, error.toException())
                }

            })

        database.child(CurrentUSerID).child("friends").orderByChild("status").equalTo("outcoming")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        snapshot.children.forEach { outcomingfriend ->
                            val outgoingId = outcomingfriend.key ?: "Not Got"
                            Log.d("outcoming friendsID","The UserID of outcoming friends request is $outcomingfriend")

                            if (outgoingId != "Not Got")
                            {
                                if (!OutGoing_List.contains(outgoingId)){
                                    OutGoing_List.add(outgoingId)
                                }
                            }
                        }

                        if (OutGoing_List.isEmpty())
                        {
                            Toast.makeText(this@FriendRequestActivity,"No OutGoing Friend Request",Toast.LENGTH_SHORT).show()
                        }else{

                            for (index in OutGoing_List.indices)
                            {
                                val outgoingFriendID = OutGoing_List[index]
                                Log.d("OutgoingFriendId","The ID is $outgoingFriendID")

                                database.child(outgoingFriendID).addListenerForSingleValueEvent(object : ValueEventListener{
                                    @SuppressLint("NotifyDataSetChanged")
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists())
                                        {
                                            val urloutgoing = snapshot.child("url").value.toString()
                                            val usernameoutgoing = snapshot.child("username").value.toString()

                                            Log.d("DataOutgoingFriends","The url is $urloutgoing " +
                                                    "The Username is $usernameoutgoing " +
                                                    "The UserID is $outgoingFriendID")

                                            val data_outgoingFriend = Friends_Recycler(
                                                urloutgoing,
                                                outgoingFriendID,
                                                usernameoutgoing,
                                                "outcoming"
                                            )

                                            if (!Friend_Incoming_List.contains(data_outgoingFriend))
                                            {
                                                Friend_Incoming_List.add(data_outgoingFriend)
                                                recyclerViewIncoming_Adapter.notifyItemInserted(Friend_Incoming_List.size-1)
                                            }

                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e("FirebaseError", error.message, error.toException())
                                    }

                                })

                            }
                            LoadRecyclerView(Friend_Incoming_List)

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


    }

    private fun LoadRecyclerView(friendIncomingList: ArrayList<Friends_Recycler>) {

        recyclerViewIncoming = binding.RecyclerViewOfIncoming
        recyclerViewIncoming.layoutManager = LinearLayoutManager(this)

        recyclerViewIncoming_Adapter = Friends_Adaptar(friendIncomingList ,
            onCancelClick = {friend -> CancelFriendReq(friend)} ,
            onAcceptClick = {friend -> AcceptFriendReq(friend)} ,
            onRejectClick = {friend -> RejectFriendReq(friend)}
            )

        recyclerViewIncoming.adapter = recyclerViewIncoming_Adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun CancelFriendReq(friends: Friends_Recycler) {
//        check = true

//        DataRemoveRealTimeDatabase(currentUserIDFromFriend,friends.userID_search!!)
//        DataRemoveRealTimeDatabase(friends.userID_search, currentUserIDFromFriend)

        val position = Friend_Incoming_List.indexOf(friends)

        if (position>=0) {

            Friend_Incoming_List.removeAt(position)
            recyclerViewIncoming_Adapter.notifyDataSetChanged()

            DataRemoveRealTimeDatabase(CurrentUSerID,friends.userID_search!!)
            DataRemoveRealTimeDatabase(friends.userID_search, CurrentUSerID)

            Toast.makeText(this,"Successfully Cancelled Friend Request",Toast.LENGTH_SHORT).show()
        }else {

            Toast.makeText(
               this,
                "Error : Couldn't Cancele Friend Request",
                Toast.LENGTH_SHORT
            ).show()
        }

//        Handler(Looper.getMainLooper()).postDelayed({
//            check= false
//        }, 1000)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun RejectFriendReq(friends: Friends_Recycler) {

        val mapofStatus = mapOf(
            "status" to ""
        )
//            DataRemoveRealTimeDatabase(currentUserIDFromFriend,friends.userID_search!!)
//            DataRemoveRealTimeDatabase(friends.userID_search, currentUserIDFromFriend)

        val position = Friend_Incoming_List.indexOf(friends)

        if (position>= 0) {

            Friend_Incoming_List.removeAt(position)
            recyclerViewIncoming_Adapter.notifyDataSetChanged()

            DataRemoveRealTimeDatabase(CurrentUSerID,friends.userID_search!!)
            DataRemoveRealTimeDatabase(friends.userID_search, CurrentUSerID)

            Toast.makeText(this,"Successfully Reject Friend Request",Toast.LENGTH_SHORT).show()

        }else {

            Toast.makeText(
                this,
                "Error : Couldn't Reject Friend Request",
                Toast.LENGTH_SHORT
            ).show()
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun AcceptFriendReq(friends: Friends_Recycler) {

        val mapofStatus = mapOf(
            "status" to "added"
        )
        val updateCurrentUser = FirebaseDatabase.getInstance()
            .getReference("USERS")
            .child(CurrentUSerID)
            .child("friends")
            .child(friends.userID_search!!)
            .updateChildren(mapofStatus)

        val updateFriendUser = FirebaseDatabase.getInstance()
            .getReference("USERS")
            .child(friends.userID_search)
            .child("friends")
            .child(CurrentUSerID)
            .updateChildren(mapofStatus)


//        val position = Friend_Incoming_List.indexOf(friends)
//
//        FirebaseDatabase.getInstance().getReference("USERS").child(CurrentUSerID).child("friends").child(friends.userID_search!!)
//            .updateChildren(mapofStatus).addOnCompleteListener { Accept ->
//                if (Accept.isSuccessful)
//                {
//
//                    if (position>=0) {
//                        Friend_Incoming_List.removeAt(position)
//                        recyclerViewIncoming_Adapter.notifyItemRemoved(position)
//
//                        Toast.makeText(
//                            this,
//                            "Successfuly Accepted FriendRequest.",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }else{
//                        Toast.makeText(this, "Could not accept friend request.", Toast.LENGTH_SHORT).show()
//                    }
//                }else{
//                    Toast.makeText(this,"Some error occurred , so couldn't perform the command",Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        FirebaseDatabase.getInstance().getReference("USERS").child(friends.userID_search).child("friends").child(CurrentUSerID)
//            .updateChildren(mapofStatus).addOnCompleteListener { Accept ->
//                if (Accept.isSuccessful)
//                {
//                    Log.d("Accept status -> Added","Successfully changed status to added for friend")
//                }else{
//                    Log.e("Accept status -> Added","couldn't changed status to added for friend")
//                }
//            }
        updateCurrentUser.continueWithTask { task1 ->
            if (task1.isSuccessful) updateFriendUser
            else throw task1.exception ?: Exception("Couldn't Accept Friend request.")
        }.addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val position = Friend_Incoming_List.indexOf(friends)
                if (position >= 0) {
                    Friend_Incoming_List.removeAt(position)
                    recyclerViewIncoming_Adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Friend request accepted.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    // Firebase updates failed
                    val errorMessage = task.exception?.message ?: "Unknown error occurred."
                    Toast.makeText(this, "Could not accept friend request: $errorMessage",Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun DataRemoveRealTimeDatabase(UserID: String, FriendID: String) {

        val RemoveDatabase = FirebaseDatabase.getInstance().getReference("USERS").child(UserID).child("friends").child(FriendID)

        RemoveDatabase.removeValue().addOnCompleteListener { task ->

            if (task.isSuccessful)
            {
                Log.d("RemoveFirebase", "Friend $FriendID removed successfully.")

            }else{
                Log.d("FailedRemoveFirebase","Failed To Remove $FriendID from firebase",task.exception)
            }
        }

    }
}