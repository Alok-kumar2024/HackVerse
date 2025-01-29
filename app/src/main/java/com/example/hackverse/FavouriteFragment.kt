package com.example.hackverse

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hackverse.databinding.FragmentFavouriteBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var CurrentuseridforBookMark: String

    private lateinit var recyclerViewHackathonsBookMark: RecyclerView
    private lateinit var recyclerViewHackathons_adapterBookMark: Hackathon_Adapter
    private var Hackathons_listBookMark = arrayListOf<Hackathon_Recycler>()

    private lateinit var database: DatabaseReference

    private var BookMarkAdded_Hackathon_list = mutableListOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)

        CurrentuseridforBookMark = arguments?.getString("UserId").toString()
        Log.d("BookMark_GetUserID", "UserId i got from main header is $CurrentuseridforBookMark")


//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner)
//        {
////            val intent = Intent(requireActivity(),HomeFragment::class.java)
////            intent.putExtra("",CurrentuseridforBookMark)
//            val fragment :Fragment = HomeFragment()
//            val bundle = Bundle()
//            bundle.putString("UserId",CurrentuseridforBookMark)
//            fragment.arguments = bundle
//
//
//            parentFragmentManager.beginTransaction().replace(R.id.Fragment_Container_Main,fragment)
//                .commit()
//        }


        database = FirebaseDatabase.getInstance().getReference("USERS")

        database.child(CurrentuseridforBookMark).child("hackathons").orderByChild("bookmark")
            .equalTo("Added").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.children.forEach { bookmark ->
                            val hackathonID = bookmark.key ?: "Not Got"
                            Log.d(
                                "BookMarkHackathonID",
                                "The HackathonID for Bookmark Added is $hackathonID"
                            )


                            if (hackathonID != "Not Got") {
                                BookMarkAdded_Hackathon_list.add(hackathonID)
                                Log.d(
                                    "BookmarkAddToarray",
                                    "Successfully added $hackathonID to Added BookmarkAdded Hackathon list"
                                )
                            }
                        }

                        if (BookMarkAdded_Hackathon_list.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "No Hackathons Added To bookmarks yet.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {

                            for (index in BookMarkAdded_Hackathon_list.indices) {
                                val BookMarkedHackathonID = BookMarkAdded_Hackathon_list[index]
                                Log.d(
                                    "BookMarked ID of Hackathons",
                                    "The BookMarked IDs of hackathon is $BookMarkedHackathonID"
                                )

                                val HackathonDatabase =
                                    FirebaseDatabase.getInstance().getReference("HACKATHON")

                                HackathonDatabase.child(BookMarkedHackathonID)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                Log.d("InsideHackathon", "Yes")

                                                val eventname =
                                                    snapshot.child("eventName").value.toString()
                                                val bannerURL =
                                                    snapshot.child("bannerUrl").value.toString()
                                                val host =
                                                    snapshot.child("hostedBy").value.toString()
                                                val prize = snapshot.child("prize").value.toString()

                                                val bookmark = snapshot.child("BookMark").child(CurrentuseridforBookMark).getValue(String::class.java) ?: "None"

                                                val VotedText = snapshot.child("votes").child("upvoted").child(CurrentuseridforBookMark).getValue(String::class.java) ?: "none"

//                                                database.child(CurrentuseridforBookMark)
//                                                    .child("hackathons")
//                                                    .child(BookMarkedHackathonID)
//                                                    .addListenerForSingleValueEvent(object :
//                                                        ValueEventListener {
//                                                        override fun onDataChange(snapshot: DataSnapshot) {
//                                                            if (snapshot.exists()) {

                                                val status = snapshot.child("status").value.toString()


                                                                val information_event =
                                                                    Hackathon_Recycler(
                                                                        bannerURL,
                                                                        BookMarkedHackathonID,
                                                                        eventname,
                                                                        host,
                                                                        prize,
                                                                        bookmark,
                                                                        VotedText,
                                                                        status
                                                                    )

                                                                Log.d(
                                                                    "HackathonIDData",
                                                                    "EventName -> $eventname" +
                                                                            "bannerurl -> $bannerURL" +
                                                                            "host -> $host" +
                                                                            "Prize -> $prize" +
                                                                            "bookmark -> $bookmark" +
                                                                            "voted text -> $VotedText"
                                                                )

                                                                Hackathons_listBookMark.add(
                                                                    information_event
                                                                )

                                                                if (isAdded) {
                                                                    if (Hackathons_listBookMark.size == BookMarkAdded_Hackathon_list.size) {

                                                                            LoadBookMark(
                                                                                Hackathons_listBookMark
                                                                            )

                                                                    }
                                                                }

//                                                            }
//                                                        }
//
//                                                        override fun onCancelled(error: DatabaseError) {
//                                                            TODO("Not yet implemented")
//                                                        }
//
//                                                    })
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })
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

    private fun LoadBookMark(hackathonsListbookmark: ArrayList<Hackathon_Recycler>) {
        recyclerViewHackathonsBookMark = binding.RecyclerViewForBookMark
        recyclerViewHackathonsBookMark.layoutManager = LinearLayoutManager(requireContext())

        recyclerViewHackathons_adapterBookMark = Hackathon_Adapter(
            hackathonsListbookmark,
            OnBookmarkClickAdd = { hackathon -> BookMarkAdded(hackathon) },
            OnBookmarkClickRemove = { hackathon -> BookMarkRemove(hackathon) },
            OnLikeClickAdd = { hackathon -> UpvoteAdd(hackathon) },
            OnLikeClickRemove = { hackathon -> UpvoteRemove(hackathon) },
            OnDetailsClick = {hackathon -> GoToDetailsActivity(hackathon) }
        )
        recyclerViewHackathonsBookMark.adapter = recyclerViewHackathons_adapterBookMark

    }

    private fun GoToDetailsActivity(hackathon: Hackathon_Recycler) {

        val intent = Intent(requireContext(),DetailsHackathon::class.java)
        intent.putExtra("HackathonEventID",hackathon.EventId)
        intent.putExtra("CurrentUserID",CurrentuseridforBookMark)
        startActivity(intent)

    }

    private fun BookMarkRemove(hackathon: Hackathon_Recycler) {

        val HACKATHONID = hackathon.EventId.toString()

        val ref = FirebaseDatabase.getInstance().getReference("USERS")
        val HackathonRef = FirebaseDatabase.getInstance().getReference("HACKATHON")

        val update = mapOf(
            "bookmark" to "Removed"
        )
        ref.child(CurrentuseridforBookMark).child("hackathons").child(HACKATHONID)
            .updateChildren(update).addOnSuccessListener {
            Toast.makeText(
                requireContext(),
                "Successfully Removed From Bookmark",
                Toast.LENGTH_SHORT
            ).show()


        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                "Error : Couldn't Removed From Bookmark",
                Toast.LENGTH_SHORT
            ).show()
        }

        val updateBookmark = mapOf(
            "BookMark/$CurrentuseridforBookMark" to ""
        )

        HackathonRef.child(HACKATHONID).updateChildren(updateBookmark).addOnSuccessListener {
            Log.d("BookMarkRemoved", "Successfully Removed in HACKATHON")
        }.addOnFailureListener {
            Log.d("BookMarkNotRemoved", "Couldn't  Remove in HACKATHON")
        }

    }

    private fun BookMarkAdded(hackathon: Hackathon_Recycler) {

        val HACKATHONID = hackathon.EventId.toString()

        val ref = FirebaseDatabase.getInstance().getReference("USERS")
        val HackathonRef = FirebaseDatabase.getInstance().getReference("HACKATHON")

        val update = mapOf(
            "bookmark" to "Added"
        )
        ref.child(CurrentuseridforBookMark).child("hackathons").child(HACKATHONID)
            .updateChildren(update).addOnSuccessListener {
            Toast.makeText(requireContext(), "Succesfully Added To BookMark.", Toast.LENGTH_SHORT)
                .show()

        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                "Error : Couldn't Added To BookMark.",
                Toast.LENGTH_SHORT
            ).show()
        }
        val updateBookmark = mapOf(
            "BookMark/$CurrentuseridforBookMark" to "Added"
        )

        HackathonRef.child(HACKATHONID).updateChildren(updateBookmark).addOnSuccessListener {
            Log.d("BookMarkAdded", "Successfully added in HACKATHON")
        }.addOnFailureListener {
            Log.d("BookMarkNotAdded", "Couldn't  add in HACKATHON")
        }

    }

    private fun UpvoteRemove(hackathon: Hackathon_Recycler) {

        val ref = FirebaseDatabase.getInstance().getReference("HACKATHON")

        val HACKATHONID = hackathon.EventId.toString()

        ref.child(HACKATHONID).addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val upvote =
                        snapshot.child("votes").child("upvotes").getValue(Int::class.java) ?: 0

                    val update = mapOf(
                        "upvotes" to upvote - 1
                    )

                    ref.child(HACKATHONID).child("votes").updateChildren(update)
                        .addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Successfully Removed Upvoted.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Error Couldn't Remove Upvote",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    val update_voted = mapOf(
                        "upvoted/$CurrentuseridforBookMark" to "no"
                    )

                    ref.child(HACKATHONID).child("votes").updateChildren(update_voted)
                        .addOnSuccessListener {

                            hackathon.Voted = "no"
                            recyclerViewHackathons_adapterBookMark.notifyDataSetChanged()
                            Log.d("votedText", "Successfully added 'no' in HACKATHON")
                        }.addOnFailureListener {
                        Log.d("votedText", "Couldn't added in HACKATHON")
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun UpvoteAdd(hackathon: Hackathon_Recycler) {
        val ref = FirebaseDatabase.getInstance().getReference("HACKATHON")

        val HACKATHONID = hackathon.EventId.toString()

        ref.child(HACKATHONID).addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val upvote =
                        snapshot.child("votes").child("upvotes").getValue(Int::class.java) ?: 0

                    val update = mapOf(
                        "upvotes" to upvote + 1
                    )

                    ref.child(HACKATHONID).child("votes").updateChildren(update)
                        .addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Successfully Upvoted.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Error Couldn't Upvote",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    val update_voted = mapOf(
                        "upvoted/$CurrentuseridforBookMark" to "yes"
                    )

                    ref.child(HACKATHONID).child("votes").updateChildren(update_voted)
                        .addOnSuccessListener {

                            hackathon.Voted = "yes"
                            recyclerViewHackathons_adapterBookMark.notifyDataSetChanged()

                            Log.d("votedText", "Successfully added 'yes' in HACKATHON")
                        }.addOnFailureListener {
                        Log.d("votedText", "Couldn't added in HACKATHON")
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }
}