package com.example.hackverse

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hackverse.databinding.FragmentFriendRequestsBinding
import com.example.hackverse.databinding.FragmentFriendsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendRequests : Fragment() {

    private var _binding : FragmentFriendRequestsBinding? =null
    private val binding get() = _binding!!

    private lateinit var database : DatabaseReference
    private lateinit var RemoveDatabase : DatabaseReference

    private lateinit var recyclerviewFriendsIncoming : RecyclerView
    private lateinit var recyclerviewFriends_Adapter_Incoming : Friends_Adaptar
    private lateinit var recyclerviewFriendsOutcoming : RecyclerView
    private lateinit var recyclerviewFriends_Adapter_Outcoming : Friends_Adaptar

    private var Friends_Incoming_Array  = arrayListOf<Friends_Recycler>()
    private var Friends_Outcoming_Array  = arrayListOf<Friends_Recycler>()

    private lateinit var currentUserIDFromFriend : String

    private var incoming_friend_list = mutableListOf<String>()
    private var outcoming_friend_list = mutableListOf<String>()

    private var check = false

    @SuppressLint("NotifyDataSetChanged")
    private fun CancelFriendReq(friends: Friends_Recycler) {
        check = true

//        DataRemoveRealTimeDatabase(currentUserIDFromFriend,friends.userID_search!!)
//        DataRemoveRealTimeDatabase(friends.userID_search, currentUserIDFromFriend)

        val position = Friends_Outcoming_Array.indexOf(friends)

        if (position>=0) {

            Friends_Outcoming_Array.removeAt(position)
            recyclerviewFriends_Adapter_Outcoming.notifyDataSetChanged()

            DataRemoveRealTimeDatabase(currentUserIDFromFriend,friends.userID_search!!)
            DataRemoveRealTimeDatabase(friends.userID_search, currentUserIDFromFriend)

            Toast.makeText(requireContext(),"Successfully Cancelled Friend Request",Toast.LENGTH_SHORT).show()
        }else {

            Toast.makeText(
                requireContext(),
                "Error : Couldn't Cancele Friend Request",
                Toast.LENGTH_SHORT
            ).show()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            check= false
        }, 1000)

    }



    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentFriendRequestsBinding.inflate(inflater,container,false)


        val backBtn =  binding.BackToFriendFragmentFromFriendList

           backBtn.setOnClickListener {

                requireActivity().supportFragmentManager.popBackStack()
            }

         currentUserIDFromFriend = arguments?.getString("UserIDFromFriendFrag").toString()
        Log.d("UserIdFromFriendFragment","The current user id from friend fragment to friend request fragment is $currentUserIDFromFriend")

        database = FirebaseDatabase.getInstance().getReference("USERS")

        database.child(currentUserIDFromFriend).child("friends")
            .orderByChild("status").equalTo("incoming")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    Friends_Incoming_Array.clear()

                    snapshot.children.forEach{ incomingFriend ->

                        val incomingFriendID = incomingFriend.key ?: return@forEach
                        Log.d("Incoming friendsID","The UserID of incoming friends request is $incomingFriendID")

                        if (!incoming_friend_list.contains(incomingFriendID))
                        {
                            incoming_friend_list.add(incomingFriendID)
                            Log.d("IncomingList","Successfully added incoming $incomingFriendID")
                        }
                    }

                    if (incoming_friend_list.isEmpty())
                    {
                        Log.d("EmptyIncomingList","The incoming_friend_list is Empty")
                        Toast.makeText(requireContext(),"No incoming Friend request",Toast.LENGTH_SHORT).show()

                    }else{

                        for (index in incoming_friend_list.indices)
                        {
                            val incomingFriendIdFromStore = incoming_friend_list[index]
                            Log.d("Separate IncomingFriendsID","The value of incoming friendIDs from List array is $incomingFriendIdFromStore")

                            database.child(incomingFriendIdFromStore).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        val urlIncomingFriend =
                                            snapshot.child("url").value.toString()
                                        val usernameIncomingFriend =
                                            snapshot.child("username").value.toString()

                                        Log.d("DataIncomingFriends","The url is $urlIncomingFriend " +
                                                "The Username is $usernameIncomingFriend " +
                                                "The UserID is $incomingFriendIdFromStore")

                                        val data_incomingFriend = Friends_Recycler(
                                            urlIncomingFriend,
                                            incomingFriendIdFromStore,
                                            usernameIncomingFriend,
                                            "incoming"
                                        )

                                        Friends_Incoming_Array.add(data_incomingFriend)

                                        if (Friends_Incoming_Array.size == incoming_friend_list.size)
                                        {
                                            incomingRecyclerView(Friends_Incoming_Array)
                                        }else{
//                                            Toast.makeText(requireContext(),"Some Error occcured , While fetching data.",Toast.LENGTH_SHORT).show()
                                            Log.e("SizeMismatchIncoming","The size of Friends_Incoming_Array is ${Friends_Incoming_Array.size} ," +
                                                    "While size of incoming_friend_list is ${incoming_friend_list.size}")
                                        }
                                    }else{
                                        Log.d("IncomingId Does not exists","The Id $incomingFriendIdFromStore does not exists in database")
                                    }

                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                        }
//                        if (Friends_Incoming_Array.size == incoming_friend_list.size)
//                        {
//                            incomingRecyclerView(Friends_Incoming_Array)
//                        }else{
//                            Toast.makeText(requireContext(),"Some Error occcured , While fetching data.",Toast.LENGTH_SHORT).show()
//                            Log.e("SizeMismatchIncoming","The size of Friends_Incoming_Array is ${Friends_Incoming_Array.size} ," +
//                                    "While size of incoming_friend_list is ${incoming_friend_list.size}")
//                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        database.child(currentUserIDFromFriend).child("friends")
            .orderByChild("status").equalTo("outcoming")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (check)
                    {
                        Log.d("FirebaseListener", "Update skipped due to ongoing cancel operation")
                        return
                    }
                    Friends_Outcoming_Array.clear()

                    snapshot.children.forEach { outcomingFriend ->

                        val outcomingFriendID = outcomingFriend.key ?: return@forEach
                        Log.d("Incoming friendsID","The UserID of incoming friends request is $outcomingFriendID")

                        if (!outcoming_friend_list.contains(outcomingFriendID))
                        {
                            outcoming_friend_list.add(outcomingFriendID)
                            Log.d("IncomingList","Successfully added incoming $outcomingFriend")
                        }

                    }

                    if(outcoming_friend_list.isEmpty())
                    {
                        Log.d("EmptyOutcomingList","The Outcoming_friend_list is Empty")
                        Toast.makeText(requireContext(),"No Outcoming Friend request",Toast.LENGTH_SHORT).show()
                    }else{

                        for (index in outcoming_friend_list.indices)
                        {
                            val outcomingFriendIdFromStore = outcoming_friend_list[index]
                            Log.d("Separate outcomingFriendsID","The value of outcoming friendIDs from List array is $outcomingFriendIdFromStore")

                            database.child(outcomingFriendIdFromStore).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    if (snapshot.exists())
                                    {
                                        val urlOutcomingFriend =
                                            snapshot.child("url").value.toString()
                                        val usernameOutcomingFriend =
                                            snapshot.child("username").value.toString()

                                        Log.d("DataOutcomingFriends","The url is $urlOutcomingFriend " +
                                                "The Username is $usernameOutcomingFriend " +
                                                "The UserID is $outcomingFriendIdFromStore")

                                        val data_outcomingFriend = Friends_Recycler(
                                            urlOutcomingFriend,
                                            outcomingFriendIdFromStore,
                                            usernameOutcomingFriend,
                                            "outcoming"
                                        )
                                        Friends_Outcoming_Array.add(data_outcomingFriend)

                                        if (Friends_Outcoming_Array.size == outcoming_friend_list.size)
                                        {
                                            outcomingRecyclerView(Friends_Outcoming_Array)
                                        }else{
//                                            Toast.makeText(requireContext(),"Some Error occcured , While fetching data.",Toast.LENGTH_SHORT).show()
                                            Log.e("SizeMismatchOutcoming","The size of Friends_Outcoming_Array is ${Friends_Outcoming_Array.size} ," +
                                                    "While size of outcoming_friend_list is ${outcoming_friend_list.size}")
                                        }

                                    }else{
                                        Log.d("OutcomingId Does not exists","The Id $outcomingFriendIdFromStore does not exists in database")
                                    }

                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                        }


//                        if (Friends_Outcoming_Array.size == outcoming_friend_list.size)
//                        {
//                            outcomingRecyclerView(Friends_Outcoming_Array)
//                        }else{
//                            Toast.makeText(requireContext(),"Some Error occcured , While fetching data.",Toast.LENGTH_SHORT).show()
//                            Log.e("SizeMismatchOutcoming","The size of Friends_Outcoming_Array is ${Friends_Outcoming_Array.size} ," +
//                                    "While size of outcoming_friend_list is ${outcoming_friend_list.size}")
//                        }
                    }



                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })



        return binding.root
    }

    private fun outcomingRecyclerView(friendsOutcomingArray: ArrayList<Friends_Recycler>) {

        if (isAdded) {

            binding.RecyclerViewOfOutcoming.visibility = View.VISIBLE
            recyclerviewFriendsOutcoming = binding.RecyclerViewOfOutcoming
            recyclerviewFriendsOutcoming.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

            recyclerviewFriends_Adapter_Outcoming = Friends_Adaptar(friendsOutcomingArray,
                onCancelClick = { friend -> CancelFriendReq(friend) }
            )

            recyclerviewFriendsOutcoming.adapter = recyclerviewFriends_Adapter_Outcoming
        }else
        {
            Log.e("Fragment Error outcoming", "Fragment outcoming is not attached to an activity.")
        }

    }


//    private fun CancelFriendReq(friends: Friends_Recycler) {
//
////        DataRemoveRealTimeDatabase(currentUserIDFromFriend,friends.userID_search!!)
////        DataRemoveRealTimeDatabase(friends.userID_search, currentUserIDFromFriend)
//
//        val position = Friends_Outcoming_Array.indexOf(friends)
//
//        if (position>=0) {
//
//            Friends_Outcoming_Array.removeAt(position)
//            recyclerviewFriends_Adapter_Outcoming.notifyItemRemoved(position)
//
//            DataRemoveRealTimeDatabase(currentUserIDFromFriend,friends.userID_search!!)
//            DataRemoveRealTimeDatabase(friends.userID_search, currentUserIDFromFriend)
//
//            Toast.makeText(requireContext(),"Successfully Cancelled Friend Request",Toast.LENGTH_SHORT).show()
//        }else {
//
//            Toast.makeText(
//                requireContext(),
//                "Error : Couldn't Cancele Friend Request",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//
//    }

    private fun incomingRecyclerView(friendsIncomingArray: ArrayList<Friends_Recycler>) {

        if (isAdded) {
            binding.RecyclerViewOfIncoming.visibility = View.VISIBLE
            recyclerviewFriendsIncoming = binding.RecyclerViewOfIncoming
            recyclerviewFriendsIncoming.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

            recyclerviewFriends_Adapter_Incoming = Friends_Adaptar(friendsIncomingArray,
                onAcceptClick = { friend -> AcceptFriendReq(friend) },
                onRejectClick = { friend -> RejectFriendReq(friend) }
            )

            recyclerviewFriendsIncoming.adapter = recyclerviewFriends_Adapter_Incoming
        }else
        {
            Log.e("Fragment Error incoming", "Fragment incoming is not attached to an activity.")
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun RejectFriendReq(friends: Friends_Recycler) {

            val mapofStatus = mapOf(
                "status" to ""
            )
//            DataRemoveRealTimeDatabase(currentUserIDFromFriend,friends.userID_search!!)
//            DataRemoveRealTimeDatabase(friends.userID_search, currentUserIDFromFriend)

        val position = Friends_Incoming_Array.indexOf(friends)

        if (position>= 0) {

            Friends_Incoming_Array.removeAt(position)
            recyclerviewFriends_Adapter_Incoming.notifyDataSetChanged()

            DataRemoveRealTimeDatabase(currentUserIDFromFriend,friends.userID_search!!)
            DataRemoveRealTimeDatabase(friends.userID_search, currentUserIDFromFriend)

            Toast.makeText(requireContext(),"Successfully Reject Friend Request",Toast.LENGTH_SHORT).show()

            }else {

            Toast.makeText(
                requireContext(),
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
        FirebaseDatabase.getInstance().getReference("USERS").child(currentUserIDFromFriend).child("friends").child(friends.userID_search!!)
            .updateChildren(mapofStatus).addOnCompleteListener { Accept ->
                if (Accept.isSuccessful)
                {

                    val position = Friends_Incoming_Array.indexOf(friends)

                    if (position>=0) {
                        Friends_Incoming_Array.removeAt(position)
                        recyclerviewFriends_Adapter_Incoming.notifyDataSetChanged()
                        Toast.makeText(
                            requireContext(),
                            "Successfuly Accepted FriendRequest.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        Toast.makeText(requireContext(), "Could not accept friend request.", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(),"Some error occurred , so couldn't perform the command",Toast.LENGTH_SHORT).show()
                }
            }

        FirebaseDatabase.getInstance().getReference("USERS").child(friends.userID_search).child("friends").child(currentUserIDFromFriend)
            .updateChildren(mapofStatus).addOnCompleteListener { Accept ->
                if (Accept.isSuccessful)
                {
                    Log.d("Accept status -> Added","Successfully changed status to added for friend")
                }else{
                    Log.e("Accept status -> Added","couldn't changed status to added for friend")
                }
            }


    }

    private fun DataRemoveRealTimeDatabase(UserID: String, FriendID: String) {

        RemoveDatabase = FirebaseDatabase.getInstance().getReference("USERS").child(UserID).child("friends").child(FriendID)

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