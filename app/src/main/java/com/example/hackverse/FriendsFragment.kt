package com.example.hackverse

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.bumptech.glide.Glide
import com.example.hackverse.databinding.FragmentFriendsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FriendsFragment : Fragment() {



    private var _binding : FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database : DatabaseReference
    private lateinit var RemoveDatabase : DatabaseReference

    private lateinit var recyclerviewFriends : RecyclerView
    private lateinit var recyclerviewFriends_Adapter : Friends_Adaptar
    private var Friends_Add_Array  = arrayListOf<Friends_Recycler>()

    private lateinit var recyclerviewFriendsSearch : RecyclerView
    private lateinit var recyclerviewFriendsSearch_Adapter : Friends_Adaptar
    private var Friends_Search_Array  = arrayListOf<Friends_Recycler>()

    private lateinit var getuseridforfriends : String
    private lateinit var EnteredUserID : String

    private var Added_friend_list = mutableListOf<String>()



    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentFriendsBinding.inflate(inflater,container,false)


        getuseridforfriends = arguments?.getString("UserId").toString()
        Log.d("Friend_GetUserID","UserId i got from main header is $getuseridforfriends")

        binding.Notification.setOnClickListener {

//            binding.TextYourFriend.visibility =View.GONE
//            binding.TextNoFriends.visibility = View.GONE


//            binding.Notification.visibility = View.GONE


//            binding.SearchIDButton.visibility = View.GONE
//            binding.TextNoUserExists.visibility = View.GONE
//            binding.SearchID.visibility = View.GONE
//
//            (binding.RecyclerViewForShowingUsersSearch.adapter as? Friends_Adaptar)?.notifyDataSetChanged()
//            binding.RecyclerViewForShowingUsersSearch.visibility = View.GONE
//
//            (binding.RecyclerViewForShowingUsersSearch.adapter as? Friends_Adaptar)?.notifyDataSetChanged()
//            binding.RecyclerViewForShowingUsersSearch.visibility = View.GONE

            binding.FrameLayoutOfNotification.visibility = View.VISIBLE

//            binding.Notification.isEnabled = false

            val fragment = FriendRequests()

            val bundle = Bundle()
            bundle.putString("UserIDFromFriendFrag",getuseridforfriends)
            fragment.arguments = bundle

            parentFragmentManager.beginTransaction().replace(R.id.FrameLayoutOfNotification,fragment)
                .addToBackStack(null)
                .commit()

        }


         database = FirebaseDatabase.getInstance().getReference("USERS").child(getuseridforfriends).child("friends")


        database.orderByChild("status").equalTo("added").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

//                val Added_friend_list = mutableListOf<String>()

                snapshot.children.forEach { friendSnaphot ->

                    val friendID = friendSnaphot.key ?: "Not Found FriendID"
                    Log.d("FriendIDKey","The FriendID for ADDED status is $friendID")

                    if (friendID != "Not Found FriendID")
                    {
                        Added_friend_list.add(friendID)
                        Log.d("AddToarray","Successfully added $friendID to Added friend list")
                    }

                }

                if (Added_friend_list.isEmpty())
                {
                    Toast.makeText(requireContext(),"No Friends Added Yet.",Toast.LENGTH_SHORT).show()
                }else{
                    binding.TextNoFriends.visibility = View.GONE

                    for (index in Added_friend_list.indices){

                        val AddedfriendIDs = Added_friend_list[index]
                        Log.d("ValueFrom Array","Value AddedFriendIDs -> $AddedfriendIDs" +
                                "value Added_Friend_list[index] -> $Added_friend_list[index]")
                        val AddedSearch = FirebaseDatabase.getInstance()
//                        var Check : String = "Start"
//                        Log.d("Check at start","The value of check at start is Check $Check")

                        val Check_Added = mutableListOf<String>()
                        AddedSearch.getReference("USERS").child(AddedfriendIDs).child("friends").orderByChild("status").equalTo("Added")
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    snapshot.children.forEach { CheckSnapshot ->
                                        val Check_ID = CheckSnapshot.key.toString()
                                        Log.d("Chek_ID","Check_ID of AddedFriendsID is $Check_ID")
                                        if (Check_ID.isNotEmpty()) {
                                            Check_Added.add(Check_ID)
                                        }
                                    }

//                                    recyclerviewFriends = binding.RecyclerViewForShowingUsersAdded
//                                    recyclerviewFriends.layoutManager = LinearLayoutManager(requireContext())

                                    AddedSearch.getReference("USERS").child(AddedfriendIDs).addListenerForSingleValueEvent(object : ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val urlFriend = snapshot.child("url").value.toString()
                                            val usernameFriend = snapshot.child("username").value.toString()

                                            val data_addedFriend = Friends_Recycler(urlFriend,AddedfriendIDs,usernameFriend,"added")

                                            Friends_Add_Array.add(data_addedFriend)

                                            if (isAdded) {
                                                if (Friends_Add_Array.size == Added_friend_list.size) {
                                                    setRecyclerView(Friends_Add_Array)
                                                }
                                            }

                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })


                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })


                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



        binding.SearchIDButton.setOnClickListener {



            EnteredUserID = binding.SearchID.text.toString()
            Log.d("EnteredUSERID","The USERID Entered is $EnteredUserID")

            lateinit var searchInDatabase : DatabaseReference

            var statusSearchCurrentuser = "None"
//            lateinit var statusSearchFriend : String

            if (EnteredUserID.isEmpty())
            {
                Toast.makeText(requireContext(),"This field cannot be Empty , if you wish to search someone",Toast.LENGTH_SHORT).show()
            }else if (EnteredUserID == getuseridforfriends) {
                Toast.makeText(requireContext(),"You can't search yourSelf.",Toast.LENGTH_SHORT).show()

            }else{
                binding.TextNoFriends.visibility = View.GONE
                searchInDatabase = FirebaseDatabase.getInstance().getReference("USERS")

                searchInDatabase.orderByKey().equalTo(EnteredUserID).addListenerForSingleValueEvent(object : ValueEventListener{
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists())
                        {
                            binding.TextNoUserExists.visibility = View.GONE
                            lateinit var urlSearch : String
                            lateinit var usernameSearch : String

                            for (userSnapshot in snapshot.children) {

                                val DataOfEnteredUserID = userSnapshot.getValue(coders::class.java)

                                 urlSearch = DataOfEnteredUserID?.url.toString()
                                 usernameSearch = DataOfEnteredUserID?.username.toString()
                            }
                            Log.d("Data from Search","The url is $urlSearch" +
                                    "The username is $usernameSearch")


                          // Now about the value of status , i will check it in the current user , that the search id , has whih
                        // satus in current user database, if i like i would double check that status in the entered userid

                            Log.d("GetuserIDforfriends","Checking for userid in searching one $getuseridforfriends")
                            searchInDatabase.child(getuseridforfriends).child("friends").child(EnteredUserID)
                                .addValueEventListener(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.d("Debug", "Listener triggered")
                                        if (snapshot.exists()) {
                                            Log.d("Debuginside", "Snapshot exists: ${snapshot.value}")

                                            statusSearchCurrentuser = (snapshot.child("status").value ?: "None").toString()
                                            Log.d("Valueinside of status from function","The value of status for current user is $statusSearchCurrentuser")

                                        }else{

                                            statusSearchCurrentuser = "None"
                                            Log.d("ElseValueinside of status from function","The value of status for current user is $statusSearchCurrentuser")



                                        }
                                        Friends_Search_Array.clear()
                                        val isAlreadyAdded = Friends_Search_Array.any { it.userID_search == EnteredUserID }

                                        if (!isAlreadyAdded) {
                                            val StoreData = Friends_Recycler(
                                                urlSearch,
                                                EnteredUserID,
                                                usernameSearch,
                                                statusSearchCurrentuser
                                            )
                                            Friends_Search_Array.add(StoreData)
                                        }
                                        recyclerviewFriendsSearch_Adapter.notifyDataSetChanged()
                                        binding.RecyclerViewForShowingUsersSearch.visibility = View.VISIBLE

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })

                            if (isAdded) {

                                (binding.RecyclerViewForShowingUsersAdded.adapter as? Friends_Adaptar)?.notifyDataSetChanged()
                                binding.RecyclerViewForShowingUsersAdded.visibility = View.GONE

                                recyclerviewFriendsSearch =
                                    binding.RecyclerViewForShowingUsersSearch
                                recyclerviewFriendsSearch.layoutManager =
                                    LinearLayoutManager(requireContext())

                                recyclerviewFriendsSearch_Adapter = Friends_Adaptar(
                                    Friends_Search_Array,
                                    onAcceptClick = { friends -> AcceptFriend(friends) },
                                    onRejectClick = { friends -> RejectFriend(friends) },
                                    onAddClick = { friends -> AddFriend(friends) },
                                    onRemoveClick = { friends -> removeFriend(friends) },
                                    onCancelClick = { friends -> CancelFriend(friends) },
                                )
                                recyclerviewFriendsSearch.adapter =
                                    recyclerviewFriendsSearch_Adapter
                            }




                        }else{
                            (binding.RecyclerViewForShowingUsersSearch.adapter as? Friends_Adaptar)?.notifyDataSetChanged()
                            binding.RecyclerViewForShowingUsersSearch.visibility = View.GONE

                            binding.TextNoUserExists.visibility = View.VISIBLE
                            Toast.makeText(requireContext(),"No Such User Exists.",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })


            }

        }


        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setRecyclerView(friendsAddArray: ArrayList<Friends_Recycler>) {

        if (!isAdded || context == null) {
            Log.w("FriendsFragment", "Fragment is not attached, skipping RecyclerView setup.")
            return
        }


        (binding.RecyclerViewForShowingUsersSearch.adapter as? Friends_Adaptar)?.notifyDataSetChanged()
        binding.RecyclerViewForShowingUsersSearch.visibility = View.GONE

        binding.RecyclerViewForShowingUsersAdded.visibility = View.VISIBLE
        recyclerviewFriends = binding.RecyclerViewForShowingUsersAdded
        recyclerviewFriends.layoutManager = LinearLayoutManager(requireContext())

        recyclerviewFriends_Adapter = Friends_Adaptar(friendsAddArray,
            onRemoveClick = { friend ->
                removeFriend(friend)
            }
        )

        recyclerviewFriends.adapter = recyclerviewFriends_Adapter


    }

    private fun CancelFriend(friends: Friends_Recycler) {

        DataRemoveRealTimeDatabase(getuseridforfriends,friends.userID_search!!)
        DataRemoveRealTimeDatabase(friends.userID_search, getuseridforfriends)

        Toast.makeText(requireContext(),"Successfully Cancelled Friend Request",Toast.LENGTH_SHORT).show()

    }

    private fun AddFriend(friends: Friends_Recycler) {

       val AddDatabase = FirebaseDatabase.getInstance().getReference("USERS")
        val mapOFFriendData = mutableMapOf(
            "urlOfFriend " to "" ,
            "userNameOfFriend" to "" ,
            "status" to ""
        )
        val mapOFCurrentUserData = mutableMapOf(
            "urlOfFriend" to "" ,
            "userNameOfFriend" to "" ,
            "status" to ""
        )

        AddDatabase.child(EnteredUserID).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists())
                {
                    mapOFFriendData["urlOfFriend"] = snapshot.child("url").value.toString()
                    mapOFFriendData["userNameOfFriend"] = snapshot.child("username").value.toString()
                    mapOFFriendData["status"] = "outcoming"


                    AddDatabase.child(getuseridforfriends).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (snapshot.exists())
                            {
                                mapOFCurrentUserData["urlOfFriend"] = snapshot.child("url").value.toString()
                                mapOFCurrentUserData["userNameOfFriend"] = snapshot.child("username").value.toString()
                                mapOFCurrentUserData["status"] = "incoming"

                                AddDatabase.child(getuseridforfriends).child("friends").child(EnteredUserID).setValue(mapOFFriendData)
                                AddDatabase.child(EnteredUserID).child("friends").child(getuseridforfriends).setValue(mapOFCurrentUserData).addOnCompleteListener {
                                    if(it.isSuccessful)
                                    {
                                        Toast.makeText(requireContext(),"Successfully send friend request.",Toast.LENGTH_SHORT).show()
                                    }
                                }

                            }else{
                                Log.e("AcceptFriend","couldn't map current users data")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                }else{
                    Log.e("AcceptFriend","couldn't map friends data")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



    }

    @SuppressLint("NotifyDataSetChanged")
    private fun RejectFriend(friends: Friends_Recycler) {
        val mapofStatus = mapOf(
            "status" to ""
        )
        DataRemoveRealTimeDatabase(getuseridforfriends,friends.userID_search!!)
        DataRemoveRealTimeDatabase(friends.userID_search, getuseridforfriends)

        Toast.makeText(requireContext(),"Successfully Reject Friend Request",Toast.LENGTH_SHORT).show()

    }

    private fun AcceptFriend(friends: Friends_Recycler) {

        val mapofStatus = mapOf(
            "status" to "added"
        )
        FirebaseDatabase.getInstance().getReference("USERS").child(getuseridforfriends).child("friends").child(EnteredUserID)
            .updateChildren(mapofStatus).addOnCompleteListener { Accept ->
                if (Accept.isSuccessful)
                {
                    Toast.makeText(requireContext(),"Successfuly Accepted FriendRequest.",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(),"Some error occurred , so couldn't perform the command",Toast.LENGTH_SHORT).show()
                }
            }

        FirebaseDatabase.getInstance().getReference("USERS").child(EnteredUserID).child("friends").child(getuseridforfriends)
            .updateChildren(mapofStatus).addOnCompleteListener { Accept ->
                if (Accept.isSuccessful)
                {
                    Log.d("Accept status -> Added","Successfully changed status to added for friend")
                }else{
                    Log.e("Accept status -> Added","couldn't changed status to added for friend")
                }
            }

    }



    @SuppressLint("NotifyDataSetChanged")
    private fun removeFriend(friends: Friends_Recycler) {

//        DataRemoveRealTimeDatabase(getuseridforfriends,friends.userID_search!!)
//        DataRemoveRealTimeDatabase(friends.userID_search, getuseridforfriends)


        val position = Friends_Add_Array.indexOf(friends)

        if (position>=0) {

            Friends_Add_Array.removeAt(position)
            recyclerviewFriends_Adapter.notifyDataSetChanged()

            DataRemoveRealTimeDatabase(getuseridforfriends,friends.userID_search!!)
            DataRemoveRealTimeDatabase(friends.userID_search, getuseridforfriends)

            Toast.makeText(requireContext(), "Successfully Removed Friend.", Toast.LENGTH_SHORT)
                .show()
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



