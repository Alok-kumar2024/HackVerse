package com.example.hackverse

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private lateinit var recyclerviewFriends : RecyclerView
    private var Friends_Add_Array  = arrayListOf<Friends_Recycler>()

    private lateinit var recyclerviewFriendsSearch : RecyclerView
    private var Friends_Search_Array  = arrayListOf<Friends_Recycler>()

    private lateinit var getuseridforfriends : String
    private var userFriendAdded : Map<String,Boolean> ? =null
    private lateinit var addedUserID : List<String>
    private lateinit var addedChecks : List<Boolean>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentFriendsBinding.inflate(inflater,container,false)

        database = FirebaseDatabase.getInstance().getReference("USERS")


        getuseridforfriends = arguments?.getString("UserId").toString()
        Log.d("Friend_GetUserID","UserId i got from main header is $getuseridforfriends")

//        val Friends_Add_Array = arrayListOf<Friends_Recycler>()

        database.child(getuseridforfriends).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists())
                {
                    val users = snapshot.getValue(coders::class.java)
                    var flag = false

                     userFriendAdded = users?.friends?.added
                    if (userFriendAdded == null)
                    {
                        Toast.makeText(requireContext(),"No Friends Added Yet",Toast.LENGTH_SHORT).show()
                    }else{

                        addedUserID = userFriendAdded?.keys?.toList()!!

                        addedChecks = userFriendAdded?.values?.toList()!!

                        for(index in addedChecks.indices) {

                            if (addedChecks[index])
                            {

                                flag= true

                                recyclerviewFriends = binding.RecyclerViewForShowingUsersAdded
                                recyclerviewFriends.layoutManager = LinearLayoutManager(requireContext())

                                database.child(addedUserID[index]).addValueEventListener(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                       if (snapshot.exists())
                                       {
                                           val urlFriend = snapshot.child("url").value.toString()
                                           val useridFriend = snapshot.key.toString()
                                           val usernameFriend = snapshot.child("username").value.toString()

                                           Log.d("FriendsData for users","The data is for $addedUserID[index]" +
                                                   "UserID -> $useridFriend" +
                                                   "UserName -> $usernameFriend" +
                                                   "ProfilePicURL -> $urlFriend")

                                           if (urlFriend.isNotEmpty())
                                           {
                                              val data = Friends_Recycler(urlFriend,useridFriend,usernameFriend)

                                               Friends_Add_Array.add(data)
                                           }
                                       }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })



                            }


                        }
//                            Log.d("ValueOfFlag","The Flag is $flag")
//                        if (flag) {
//                            binding.TextNoFriends.visibility = View.VISIBLE
//                        }
                        recyclerviewFriends.visibility =View.VISIBLE
                        recyclerviewFriends.adapter = Friends_Adaptar(Friends_Add_Array)



                    }
                    Log.d("ValueOfFlag","The Flag is $flag")
                    if (flag) {
                        binding.TextNoFriends.visibility = View.GONE
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

        binding.SearchIDButton.setOnClickListener {

//            recyclerviewFriends.visibility =View.GONE

            val EnteredUserID = binding.SearchID.text.toString()
            Log.d("EnteredUSERID","The USERID Entered is $EnteredUserID")

//            database.child(EnteredUserID).addValueEventListener(object : ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//
//                    if (snapshot.exists())
//                    {
//                        val urlFromEnteredUserID = snapshot.child("url").value.toString()
//                        val userNameFromEnteredUserID = snapshot.child("username").value.toString()
//
//                        recyclerviewFriendsSearch = binding.RecyclerViewForShowingUsersSearch
//                        recyclerviewFriendsSearch.layoutManager = LinearLayoutManager(requireContext())
//
//                        val dataSearch = Friends_Recycler(urlFromEnteredUserID,EnteredUserID,userNameFromEnteredUserID)
//
//                        Friends_Search_Array.add(dataSearch)
//
//                        recyclerviewFriendsSearch.adapter = Friends_Adaptar(Friends_Search_Array)
//
//
//
//                    }else{
//                        Toast.makeText(requireContext(),"No user of UserId $EnteredUserID exists.",Toast.LENGTH_SHORT).show()
//                        binding.TextNoUserExists.visibility = View.VISIBLE
//                    }
//
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//
//            })

            if(EnteredUserID.isEmpty())
            {
                Toast.makeText(requireContext(),"This Field Cannot be Empty",Toast.LENGTH_SHORT).show()
            }else {

                val Search = database.orderByKey().equalTo(EnteredUserID)
                    .addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {
//
                            if (snapshot.exists()) {
                                lateinit var urlFromEnteredUserID: String
                                lateinit var userNameFromEnteredUserID: String

                                for (userSnapshot in snapshot.children) {
                                    val dataWhenEntered = userSnapshot.getValue(coders::class.java)

                                    urlFromEnteredUserID = dataWhenEntered?.url.toString()
                                    userNameFromEnteredUserID = dataWhenEntered?.username.toString()
                                }

                                recyclerviewFriendsSearch =
                                    binding.RecyclerViewForShowingUsersSearch
                                recyclerviewFriendsSearch.layoutManager =
                                    LinearLayoutManager(requireContext())

                                val dataSearch = Friends_Recycler(
                                    urlFromEnteredUserID,
                                    EnteredUserID,
                                    userNameFromEnteredUserID
                                )

                                Friends_Search_Array.add(dataSearch)

                                binding.TextNoUserExists.visibility = View.GONE
                                binding.TextNoFriends.visibility = View.GONE
                                recyclerviewFriendsSearch.visibility = View.VISIBLE

                                recyclerviewFriendsSearch.adapter =
                                    Friends_Adaptar(Friends_Search_Array)


                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "No user of UserId $EnteredUserID exists.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                binding.TextNoFriends.visibility = View.GONE
//                                recyclerviewFriendsSearch.visibility = View.GONE

                                binding.TextNoUserExists.visibility = View.VISIBLE
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

}