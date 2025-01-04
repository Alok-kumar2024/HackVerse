package com.example.hackverse

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hackverse.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewHackathonsAll : RecyclerView
    private lateinit var recyclerViewHackathons_adapterAll : Hackathon_Adapter
    private var Hackathons_listAll = arrayListOf<Hackathon_Recycler>()

    private var Hackathons_listActive = arrayListOf<Hackathon_Recycler>()
    private var Hackathons_listInactive = arrayListOf<Hackathon_Recycler>()

    private var selectedItemId : Int = R.id.Menu_All_Hackathons_Home
    private var SelectItemText : String = "All"

    private lateinit var database : DatabaseReference

    private var ALL_Hackathon_list = mutableListOf<String>()

    private var Active_Hackathon_list = mutableListOf<String>()
    private var Inactive_Hackathon_list = mutableListOf<String>()

    private lateinit var CurrentuseridforHome : String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater,container,false)

        CurrentuseridforHome = arguments?.getString("UserId") ?: "Not getting user Id"
        Log.d("Home_GetUserID","UserId i got from main header is $CurrentuseridforHome")

        binding.TagOfEnterEventIDHome.text = SelectItemText
        LoadEvent(SelectItemText)

        val dropDown : ImageButton = binding.butttonSortEventsHome
        database = FirebaseDatabase.getInstance().getReference("HACKATHON")

        dropDown.setOnClickListener {

            val popupMenu = PopupMenu(requireContext(),dropDown)
            popupMenu.menuInflater.inflate(R.menu.choose_home , popupMenu.menu)

            for (i in 0 until popupMenu.menu.size())
            {
                val menuitem = popupMenu.menu.getItem(i)
                menuitem.isChecked = menuitem.itemId == selectedItemId
            }

            popupMenu.setOnMenuItemClickListener { menuitem : MenuItem ->
                selectedItemId = menuitem.itemId
                menuitem.isChecked = true

                SelectItemText = menuitem.title.toString()

                when(menuitem.itemId)
                {
                    R.id.Menu_All_Hackathons_Home -> {
                        SelectItemText = menuitem.title.toString()
                        binding.TagOfEnterEventIDHome.text = SelectItemText
                        LoadEvent(SelectItemText)
                        Log.d("SelectItmText","The SelectItem Text is $SelectItemText")
                        true
                    }

                    R.id.Menu_Active_Hackathons_Home -> {
                        SelectItemText = menuitem.title.toString()
                        binding.TagOfEnterEventIDHome.text = SelectItemText
                        LoadEvent(SelectItemText)
                        Log.d("SelectItmText","The SelectItem Text is $SelectItemText")

                        true
                    }

                    R.id.Menu_Inactive_Hackathons_Home -> {
                        SelectItemText = menuitem.title.toString()
                        binding.TagOfEnterEventIDHome.text = SelectItemText
                        LoadEvent(SelectItemText)
                        Log.d("SelectItmText","The SelectItem Text is $SelectItemText")
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach { HomeHackathon ->
                        val HackathonID = HomeHackathon.key ?: "Not Got"
                        Log.d("ALLHackathonIDKey","The HackathonID for Created status is $HackathonID")

                        if (HackathonID != "Not Got")
                        {
                            ALL_Hackathon_list.add(HackathonID)
                            Log.d("AddToarray","Successfully added $HackathonID to All Hackathon list")
                        }
                    }

                    if (ALL_Hackathon_list.isEmpty())
                    {
                        Toast.makeText(requireContext(),"Hackathon Event is Not Hosted Yet",Toast.LENGTH_SHORT).show()
                    }else{
                        Hackathons_listAll.clear()

                        for (index in ALL_Hackathon_list.indices)
                        {
                            val HackathonIDALL = ALL_Hackathon_list[index]
                            Log.d("All ID of Hackathons","The created IDs of hackathon is $HackathonIDALL")

                            database.child(HackathonIDALL).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                   if (snapshot.exists())
                                   {
                                       Log.d("Home InsideHackathon","Yes")

                                       val eventname = snapshot.child("eventName").value.toString()
                                       val bannerURL = snapshot.child("bannerUrl").value.toString()
                                       val host = snapshot.child("hostedBy").value.toString()
                                       val prize = snapshot.child("prize").value.toString()

                                       val bookmark = snapshot.child("BookMark").child(CurrentuseridforHome).getValue(String::class.java) ?: "None"

                                       val VotedText = snapshot.child("votes").child("upvoted").child(CurrentuseridforHome).getValue(String::class.java) ?: "none"

                                       val status = snapshot.child("status").value.toString()
                                       val information_event = Hackathon_Recycler(bannerURL,HackathonIDALL,eventname,host,prize,bookmark,VotedText,status)
                                       Log.d("HackathonIDDataHome","EventName -> $eventname" +
                                               "bannerurl -> $bannerURL" +
                                               "host -> $host" +
                                               "Prize -> $prize" +
                                               "bookmark -> $bookmark" +
                                               "Voted -> $VotedText")

                                       if (!Hackathons_listAll.contains(information_event))
                                       {
                                           Hackathons_listAll.add(information_event)
                                       }

                                       refreshAdapter("All")


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

        database.orderByChild("status").equalTo("Active").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot.exists())
               {
                   snapshot.children.forEach { HomeHackathon ->

                       val ActiveHackathonID = HomeHackathon.key ?: "Not Got"
                       Log.d("ActiveHackathonIDKey","The HackathonID for Created status is $ActiveHackathonID")

                       if (ActiveHackathonID != "Not Got")
                       {
                           Active_Hackathon_list.add(ActiveHackathonID)
                           Log.d("AddToarray","Successfully added $ActiveHackathonID to Active Hackathon list")
                       }

                   }

                   if (Active_Hackathon_list.isEmpty())
                   {
                       Toast.makeText(requireContext(),"No Active Event",Toast.LENGTH_SHORT).show()
                   }else{

                       Hackathons_listActive.clear()
                       for (index in Active_Hackathon_list.indices)
                       {
                           val HackathonIDActive = Active_Hackathon_list[index]
                           Log.d("Active ID of Hackathons","The created IDs of hackathon is $HackathonIDActive")

                           database.child(HackathonIDActive).addListenerForSingleValueEvent(object : ValueEventListener{
                               override fun onDataChange(snapshot: DataSnapshot) {
                                   if (snapshot.exists())
                                   {
                                       Log.d("Home InsideHackathon","Yes")

                                       val eventname = snapshot.child("eventName").value.toString()
                                       val bannerURL = snapshot.child("bannerUrl").value.toString()
                                       val host = snapshot.child("hostedBy").value.toString()
                                       val prize = snapshot.child("prize").value.toString()

                                       val bookmark = snapshot.child("BookMark").child(CurrentuseridforHome).getValue(String::class.java) ?: "None"

                                       val VotedText = snapshot.child("votes").child("upvoted").child(CurrentuseridforHome).getValue(String::class.java) ?: "none"

                                       val status = snapshot.child("status").value.toString()
                                       val information_event = Hackathon_Recycler(bannerURL,HackathonIDActive,eventname,host,prize,bookmark,VotedText,status)
                                       Log.d("HackathonIDDataHome","EventName -> $eventname" +
                                               "bannerurl -> $bannerURL" +
                                               "host -> $host" +
                                               "Prize -> $prize" +
                                               "bookmark -> $bookmark" +
                                               "Voted -> $VotedText")

                                       if (!Hackathons_listActive.contains(information_event))
                                       {
                                           Hackathons_listActive.add(information_event)
                                       }


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

        database.orderByChild("status").equalTo("Inactive").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    snapshot.children.forEach { HomeHackathon ->

                        val InactiveHackathonID = HomeHackathon.key ?: "Not Got"
                        Log.d("InactiveHackathonIDKey","The HackathonID for Created status is $InactiveHackathonID")

                        if (InactiveHackathonID != "Not Got")
                        {
                            Inactive_Hackathon_list.add(InactiveHackathonID)
                            Log.d("AddToarray","Successfully added $InactiveHackathonID to InActive Hackathon list")
                        }

                    }

                    if (Inactive_Hackathon_list.isEmpty())
                    {
//                        Toast.makeText(requireContext(),"No Active Event",Toast.LENGTH_SHORT).show()
                        Log.d("No Inactive","No InActive Event")
                    }else{
                        Hackathons_listInactive.clear()

                        for (index in Inactive_Hackathon_list.indices)
                        {
                            val HackathonIDInactive = Inactive_Hackathon_list[index]
                            Log.d("Inactive ID of Hackathons","The created IDs of hackathon is $HackathonIDInactive")

                            database.child(HackathonIDInactive).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists())
                                    {
                                        Log.d("Home inactive InsideHackathon","Yes")

                                        val eventname = snapshot.child("eventName").value.toString()
                                        val bannerURL = snapshot.child("bannerUrl").value.toString()
                                        val host = snapshot.child("hostedBy").value.toString()
                                        val prize = snapshot.child("prize").value.toString()

                                        val bookmark = snapshot.child("BookMark").child(CurrentuseridforHome).getValue(String::class.java) ?: "None"

                                        val VotedText = snapshot.child("votes").child("upvoted").child(CurrentuseridforHome).getValue(String::class.java) ?: "none"

                                        val status = snapshot.child("status").value.toString()
                                        val information_event = Hackathon_Recycler(bannerURL,HackathonIDInactive,eventname,host,prize,bookmark,VotedText,status)
                                        Log.d("HackathonIDDataHome","EventName -> $eventname" +
                                                "bannerurl -> $bannerURL" +
                                                "host -> $host" +
                                                "Prize -> $prize" +
                                                "bookmark -> $bookmark" +
                                                "Voted -> $VotedText")

                                        if (!Hackathons_listInactive.contains(information_event))
                                        {
                                            Hackathons_listInactive.add(information_event)
                                        }


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

    private fun GoToDetailsActivity(hackathon: Hackathon_Recycler) {

        val intent = Intent(requireContext(),DetailsHackathon::class.java)
        intent.putExtra("HackathonEventID",hackathon.EventId)
        intent.putExtra("CurrentUserID",CurrentuseridforHome)
        startActivity(intent)

    }

    private fun LoadEvent(status : String){
        recyclerViewHackathonsAll = binding.RecyclerViewEventHomeALL
        recyclerViewHackathonsAll.layoutManager = LinearLayoutManager(context)
//        recyclerViewHackathonsAll.adapter = recyclerViewHackathons_adapterCreated

        when(status)
        {
            "All" -> {
                recyclerViewHackathons_adapterAll = Hackathon_Adapter(
                    Hackathons_listAll ,
                    OnBookmarkClickAdd = { hackathon -> BookMarkAdded(hackathon) },
                    OnBookmarkClickRemove = { hackathon -> BookMarkRemove(hackathon) },
                    OnLikeClickAdd = { hackathon -> UpvoteAdd(hackathon) },
                    OnLikeClickRemove = { hackathon -> UpvoteRemove(hackathon) },
                    OnDetailsClick = { hackathon -> GoToDetailsActivity(hackathon)}
                )
                recyclerViewHackathonsAll.adapter = recyclerViewHackathons_adapterAll

            }
            "Active" -> {
                recyclerViewHackathons_adapterAll = Hackathon_Adapter(
                    Hackathons_listActive ,
                    OnBookmarkClickAdd = { hackathon -> BookMarkAdded(hackathon) },
                    OnBookmarkClickRemove = { hackathon -> BookMarkRemove(hackathon) },
                    OnLikeClickAdd = { hackathon -> UpvoteAdd(hackathon) },
                    OnLikeClickRemove = { hackathon -> UpvoteRemove(hackathon) },
                    OnDetailsClick = { hackathon -> GoToDetailsActivity(hackathon)}
                )
                recyclerViewHackathonsAll.adapter = recyclerViewHackathons_adapterAll

            }
            "Inactive" -> {
                recyclerViewHackathons_adapterAll = Hackathon_Adapter(
                    Hackathons_listInactive ,
                    OnBookmarkClickAdd = { hackathon -> BookMarkAdded(hackathon) },
                    OnBookmarkClickRemove = { hackathon -> BookMarkRemove(hackathon) },
                    OnLikeClickAdd = { hackathon -> UpvoteAdd(hackathon) },
                    OnLikeClickRemove = { hackathon -> UpvoteRemove(hackathon) },
                    OnDetailsClick = { hackathon -> GoToDetailsActivity(hackathon)}
                )
                recyclerViewHackathonsAll.adapter = recyclerViewHackathons_adapterAll

            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshAdapter(status: String) {
        when (status) {
            "All" -> recyclerViewHackathons_adapterAll.notifyDataSetChanged()
            "Active" -> recyclerViewHackathons_adapterAll.notifyDataSetChanged()
            "Inactive" -> recyclerViewHackathons_adapterAll.notifyDataSetChanged()
        }
    }

    private fun BookMarkRemove(hackathon: Hackathon_Recycler) {

        val HACKATHONID = hackathon.EventId.toString()

        val ref = FirebaseDatabase.getInstance().getReference("USERS")
        val HackathonRef = FirebaseDatabase.getInstance().getReference("HACKATHON")

        val update = mapOf(
            "bookmark" to "Removed"
        )
        ref.child(CurrentuseridforHome).child("hackathons").child(HACKATHONID).updateChildren(update).addOnSuccessListener {
            Toast.makeText(requireContext(),"Successfully Removed From Bookmark",Toast.LENGTH_SHORT).show()


        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Error : Couldn't Removed From Bookmark",Toast.LENGTH_SHORT).show()
        }

        val updateBookmark = mapOf(
            "BookMark/$CurrentuseridforHome" to ""
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
        ref.child(CurrentuseridforHome).child("hackathons").child(HACKATHONID).updateChildren(update).addOnSuccessListener {
            Toast.makeText(requireContext(),"Succesfully Added To BookMark.",Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Error : Couldn't Added To BookMark.",Toast.LENGTH_SHORT).show()
        }
        val updateBookmark = mapOf(
            "BookMark/$CurrentuseridforHome" to "Added"
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
                        Toast.makeText(requireContext(),"Successfully Removed Upvoted.",Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(),"Error Couldn't Remove Upvote",Toast.LENGTH_SHORT).show()
                    }

                    val update_voted = mapOf(
                        "upvoted/$CurrentuseridforHome" to "no"
                    )

                    ref.child(HACKATHONID).child("votes").updateChildren(update_voted).addOnSuccessListener {

                        hackathon.Voted = "no"
                        recyclerViewHackathons_adapterAll.notifyDataSetChanged()
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
                        Toast.makeText(requireContext(),"Successfully Upvoted.",Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(),"Error Couldn't Upvote",Toast.LENGTH_SHORT).show()
                    }

                    val update_voted = mapOf(
                        "upvoted/$CurrentuseridforHome" to "yes"
                    )

                    ref.child(HACKATHONID).child("votes").updateChildren(update_voted).addOnSuccessListener {

//                        hackathon.Voted = "yes"
//                        recyclerViewHackathons_adapterAll.notifyDataSetChanged()


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