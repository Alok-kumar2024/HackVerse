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
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hackverse.Hackathon_Adapter.UserHackathonViewHolder
import com.example.hackverse.databinding.FragmentMyHackathonsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener


class My_HackathonsFragment : Fragment() {
    private var _binding : FragmentMyHackathonsBinding? =null
    private val binding get() = _binding!!

    private lateinit var recyclerViewHackathonsAll : RecyclerView
    private lateinit var recyclerViewHackathons_adapterAll : Hackathon_Adapter
    private var Hackathons_listAll = arrayListOf<Hackathon_Recycler>()

    private var Hackathons_listSearchAll = arrayListOf<Hackathon_Recycler>()
//    private var Hackathons_listSearchCreated = arrayListOf<Hackathon_Recycler>()
//    private var Hackathons_listSearchRegistered = arrayListOf<Hackathon_Recycler>()

   // private lateinit var recyclerViewHackathonsCreated : Hackathon_Recycler
//    private lateinit var recyclerViewHackathons_adapterCreated : Hackathon_Adapter
    private var Hackathons_listCreated = arrayListOf<Hackathon_Recycler>()

  //  private lateinit var recyclerViewHackathonsRegistered : Hackathon_Recycler
//    private lateinit var recyclerViewHackathons_adapterRegistered : Hackathon_Adapter
    private var Hackathons_listRegistered = arrayListOf<Hackathon_Recycler>()



    private lateinit var database : DatabaseReference

    private lateinit var CurrentuseridforMyHackathon : String

    private var selectedItemId : Int = R.id.Menu_All_Hackathons
    private var SelectItemText : String = "All"

    private var Created_Hackathon_list = mutableListOf<String>()
    private var Registered_Hackathon_list = mutableListOf<String>()

    private var EventIDS = mutableListOf<String>()
    private lateinit var Status : String

//    var createdDataFetched = false
//    var registeredDataFetched = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyHackathonsBinding.inflate(inflater,container,false)

        CurrentuseridforMyHackathon = arguments?.getString("UserId").toString()
        Log.d("Friend_GetUserID","UserId i got from main header is $CurrentuseridforMyHackathon")


        binding.buttonCreateEvent.setOnClickListener {
            val intent = Intent(requireActivity(),CreateHackathon::class.java)
            intent.putExtra("CurrentUserID",CurrentuseridforMyHackathon)
            startActivity(intent)
        }

        LoadEvent(SelectItemText)

        database = FirebaseDatabase.getInstance().getReference("HACKATHON")

        val DatabaseUsers = FirebaseDatabase.getInstance().getReference("USERS")


        binding.TagOfEnterEventID.text = SelectItemText
        val dropdown : ImageButton = binding.butttonSortEvents


        dropdown.setOnClickListener {

            val popupMenu = PopupMenu(requireContext(),dropdown)
            popupMenu.menuInflater.inflate(R.menu.choose_option , popupMenu.menu)

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
                    R.id.Menu_All_Hackathons -> {
                        SelectItemText = menuitem.title.toString()
                        binding.TagOfEnterEventID.text = SelectItemText
//                        ItemTitle(SelectItemText)
                        LoadEvent(SelectItemText)


                        Log.d("SelectItmText", "The SelectItem Text is $SelectItemText")

                        true
                    }

                    R.id.Menu_Created_Hackathons -> {
                        SelectItemText = menuitem.title.toString()
                        binding.TagOfEnterEventID.text = SelectItemText
//                        ItemTitle(SelectItemText)
                        LoadEvent(SelectItemText)
                        Log.d("SelectItmText","The SelectItem Text is $SelectItemText")


                        true
                    }

                    R.id.Menu_Registered_Hackathons -> {
                        SelectItemText = menuitem.title.toString()
                        binding.TagOfEnterEventID.text = SelectItemText
//                        ItemTitle(SelectItemText)
                        LoadEvent(SelectItemText)
                        Log.d("SelectItmText","The SelectItem Text is $SelectItemText")



                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        val Title_Menu =  ItemTitle(SelectItemText)
        Log.d("OutSelectItmText","The SelectItem Text is $Title_Menu")



        DatabaseUsers.child(CurrentuseridforMyHackathon).child("hackathons").orderByChild("EventStatus")
            .equalTo("Created")
            .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot.exists())
               {
                   snapshot.children.forEach{ hackathonsnapshot ->

                       val hackathonId = hackathonsnapshot.key?: "Not Got"
                       Log.d("CreatedHackathonIDKey","The HackathonID for Created status is $hackathonId")

                       if (hackathonId != "Not Got")
                       {
                           Created_Hackathon_list.add(hackathonId)
                           Log.d("AddToarray","Successfully added $hackathonId to Added Hackathon list")
                       }
                   }
                   Hackathons_listAll.clear()
                   Hackathons_listCreated.clear()

                   if (Created_Hackathon_list.isEmpty())
                   {
                       Toast.makeText(requireContext(),"No Hackathons created yet",Toast.LENGTH_SHORT).show()
                   }else{

                       Hackathons_listAll.clear()
                       Hackathons_listCreated.clear()

                       for (index in Created_Hackathon_list.indices)
                       {
                           val createdHackathonId = Created_Hackathon_list[index]
                           Log.d("Created ID of Hackathons","The created IDs of hackathon is $createdHackathonId")

                           val SearchCreateDatabase = FirebaseDatabase.getInstance()

                           SearchCreateDatabase.getReference("HACKATHON").child(createdHackathonId).addValueEventListener(object : ValueEventListener{
                               @SuppressLint("NotifyDataSetChanged")
                               override fun onDataChange(snapshot: DataSnapshot) {
                                   if (snapshot.exists())
                                   {
                                       Log.d("InsideHackathonCreated","Yes")

                                       val eventname = snapshot.child("eventName").value.toString()
                                       val bannerURL = snapshot.child("bannerUrl").value.toString()
                                       val host = snapshot.child("hostedBy").value.toString()
                                       val prize = snapshot.child("prize").value.toString()

                                       val bookmark = snapshot.child("BookMark").child(CurrentuseridforMyHackathon).getValue(String::class.java) ?: "None"

                                       val VotedText = snapshot.child("votes").child("upvoted").child(CurrentuseridforMyHackathon).getValue(String::class.java) ?: "none"

                                       val status = snapshot.child("status").value.toString()
                                       val information_event = Hackathon_Recycler(bannerURL,createdHackathonId,eventname,host,prize,bookmark,VotedText,status)
                                       Log.d("HackathonIDData","EventName -> $eventname" +
                                               "bannerurl -> $bannerURL" +
                                               "host -> $host" +
                                               "Prize -> $prize" +
                                               "bookmark -> $bookmark" +
                                               "Voted -> $VotedText")

                                       if (!Hackathons_listAll.contains(information_event))
                                       {
                                           Hackathons_listAll.add(information_event)
                                       }
                                       if (!Hackathons_listCreated.contains(information_event)) {
                                           Hackathons_listCreated.add(
                                               information_event
                                           )
                                       }
                                       refreshAdapter("All")

                                   }else{
                                       Toast.makeText(requireContext(),"Could not find the Event.",Toast.LENGTH_SHORT).show()
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

        DatabaseUsers.child(CurrentuseridforMyHackathon).child("hackathons").orderByChild("EventStatus")
            .equalTo("Registered").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    snapshot.children.forEach { hackathonSnaphot ->
                        val HackathonID = hackathonSnaphot.key ?: "Not Got"
                        Log.d("RegistredHackathonIDKey","The HackathonID for Created status is $HackathonID")

                        if (HackathonID != "Not Got")
                        {
                            Registered_Hackathon_list.add(HackathonID)
                            Log.d("AddToarray","Successfully added $HackathonID to Added friend list")
                        }
                    }
                    Hackathons_listRegistered.clear()

                    if (Registered_Hackathon_list.isEmpty())
                    {
                        Toast.makeText(requireContext(),"Not Registered in any Hackathons Yet.",Toast.LENGTH_SHORT).show()
                    }else{

//                        Hackathons_listAll.clear()
//                        Hackathons_listRegistered.clear()

                        for(index in Registered_Hackathon_list.indices)
                        {
                            val RegisteredHackathonID = Registered_Hackathon_list[index]
                            Log.d("Registered ID of Hackathons","The created IDs of hackathon is $RegisteredHackathonID")

                            val SearchRegisteredDatabase = FirebaseDatabase.getInstance()

                            SearchRegisteredDatabase.getReference("HACKATHON").child(RegisteredHackathonID)
                                .addValueEventListener(object : ValueEventListener{
                                    @SuppressLint("NotifyDataSetChanged")
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists())
                                        {
                                            val eventname = snapshot.child("eventName").value.toString()
                                            val bannerURL = snapshot.child("bannerUrl").value.toString()
                                            val host = snapshot.child("hostedBy").value.toString()
                                            val prize = snapshot.child("prize").value.toString()

                                            val bookmark = snapshot.child("BookMark").child(CurrentuseridforMyHackathon).getValue(String::class.java) ?: "None"

                                            val VotedText = snapshot.child("votes").child("upvoted").child(CurrentuseridforMyHackathon).getValue(String::class.java) ?: "none"

                                            val status = snapshot.child("status").value.toString()

                                            Log.d("ValueStatus","Value of status for event is $status")
                                            val information_event = Hackathon_Recycler(bannerURL,RegisteredHackathonID,eventname,host,prize,bookmark,VotedText,status)
                                            Log.d("HackathonIDData","EventName -> $eventname" +
                                                    "bannerurl -> $bannerURL" +
                                                    "host -> $host" +
                                                    "Prize -> $prize" +
                                                    "bookmark -> $bookmark" +
                                                    "Voted -> $VotedText")



                                            if (!Hackathons_listAll.contains(information_event))
                                            {
                                                Hackathons_listAll.add(information_event)
                                            }
                                            if (!Hackathons_listRegistered.contains(information_event)) {

                                                Hackathons_listRegistered.add(information_event)
                                            }

                                            refreshAdapter("All")




                                        }else{
                                            Toast.makeText(requireContext(),"Could not find the Event.",Toast.LENGTH_SHORT).show()
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


//        binding.buttonSearchHackathon.setOnClickListener {
//
//            val EnteredID = binding.EditViewEnterEventID.text.toString()
//
//            val DataBase = FirebaseDatabase.getInstance()
//
//            DataBase.getReference("USERS").child(CurrentuseridforMyHackathon)
//                .child("hackathons")
//                .orderByChild("EventStatus").equalTo(SelectItemText)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        if (snapshot.exists()) {
//                            snapshot.children.forEach { hackathon ->
//                                val IDs = (hackathon.key ?: "Not Got").toString()
//                                Log.d(
//                                    "SearchEventID",
//                                    "The Event ID searched is : IDS"
//                                )
//
//                                if (IDs != "Not Got") {
//                                    EventIDS.add(IDs)
//                                    Log.d("IDs Added", "Added In list..")
//                                }
//
//                            }
//
//                            if (EventIDS.isEmpty()) {
//                                Toast.makeText(
//                                    requireContext(),
//                                    "No Event With This Event ID is recorded here.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            } else {
//
//                                val FoundID = EventIDS.filter { it.contains(EnteredID) }
//                                for (index in FoundID.indices) {
//
//                                    DataBase.getReference("HACKATHON")
//                                        .child(FoundID[index])
//                                        .addListenerForSingleValueEvent(object :
//                                            ValueEventListener {
//                                            override fun onDataChange(snapshot: DataSnapshot) {
//                                                if (snapshot.exists()) {
//                                                    val eventname =
//                                                        snapshot.child("eventName").value.toString()
//                                                    val bannerURL =
//                                                        snapshot.child("bannerUrl").value.toString()
//                                                    val host =
//                                                        snapshot.child("hostedBy").value.toString()
//                                                    val prize =
//                                                        snapshot.child("prize").value.toString()
//
//                                                    val bookmark =
//                                                        snapshot.child("BookMark")
//                                                            .child(
//                                                                CurrentuseridforMyHackathon
//                                                            )
//                                                            .getValue(String::class.java)
//                                                            ?: "None"
//
//                                                    val VotedText =
//                                                        snapshot.child("votes")
//                                                            .child("upvoted").child(
//                                                                CurrentuseridforMyHackathon
//                                                            )
//                                                            .getValue(String::class.java)
//                                                            ?: "none"
//
//                                                    val status =
//                                                        snapshot.child("status").value.toString()
//
//                                                    Log.d(
//                                                        "ValueStatus",
//                                                        "Value of status for event is $status"
//                                                    )
//                                                    val information_event =
//                                                        Hackathon_Recycler(
//                                                            bannerURL,
//                                                            FoundID[index],
//                                                            eventname,
//                                                            host,
//                                                            prize,
//                                                            bookmark,
//                                                            VotedText,
//                                                            status
//                                                        )
//                                                    Log.d(
//                                                        "HackathonIDData",
//                                                        "EventName -> $eventname" +
//                                                                "bannerurl -> $bannerURL" +
//                                                                "host -> $host" +
//                                                                "Prize -> $prize" +
//                                                                "bookmark -> $bookmark" +
//                                                                "Voted -> $VotedText"
//                                                    )
//
//                                                    if (!Hackathons_listSearchAll.contains(
//                                                            information_event
//                                                        )
//                                                    ) {
//                                                        Hackathons_listSearchAll.add(
//                                                            information_event
//                                                        )
//                                                    }
//
//                                                }
//                                            }
//
//                                            override fun onCancelled(error: DatabaseError) {
//                                                TODO("Not yet implemented")
//                                            }
//                                        })
//                                }
//
//                                recyclerViewHackathonsAll = binding.RecyclerViewEventCreatedOrJoined
//                                recyclerViewHackathonsAll.layoutManager = LinearLayoutManager(context)
//
//                                recyclerViewHackathons_adapterAll = Hackathon_Adapter(
//                                    Hackathons_listSearchAll ,
//                                    OnBookmarkClickAdd = { hackathon -> BookMarkAdded(hackathon) } ,
//                                    OnBookmarkClickRemove = { hackathon -> BookMarkRemove(hackathon) } ,
//                                    OnLikeClickAdd = { hackathon -> UpvoteAdd(hackathon) } ,
//                                    OnLikeClickRemove = { hackathon -> UpvoteRemove(hackathon) } ,
//                                    OnDetailsClick = { hackathon -> GoToDetailsActivity(hackathon)}
//                                )
//                                recyclerViewHackathonsAll.adapter = recyclerViewHackathons_adapterAll
//
//
//                            }
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        TODO("Not yet implemented")
//                    }
//                })
//        }



        return binding.root
    }


    private fun LoadEvent(status : String){
        recyclerViewHackathonsAll = binding.RecyclerViewEventCreatedOrJoined
        recyclerViewHackathonsAll.layoutManager = LinearLayoutManager(context)
//        recyclerViewHackathonsAll.adapter = recyclerViewHackathons_adapterCreated

        when(status)
        {
            "All" -> {
                recyclerViewHackathons_adapterAll = Hackathon_Adapter(
                    Hackathons_listAll ,
                    OnBookmarkClickAdd = { hackathon -> BookMarkAdded(hackathon) } ,
                    OnBookmarkClickRemove = { hackathon -> BookMarkRemove(hackathon) } ,
                    OnLikeClickAdd = { hackathon -> UpvoteAdd(hackathon) } ,
                    OnLikeClickRemove = { hackathon -> UpvoteRemove(hackathon) } ,
                    OnDetailsClick = { hackathon -> GoToDetailsActivity(hackathon)}
                )
                recyclerViewHackathonsAll.adapter = recyclerViewHackathons_adapterAll

            }
            "Created" -> {
                recyclerViewHackathons_adapterAll = Hackathon_Adapter(
                    Hackathons_listCreated ,
                    OnBookmarkClickAdd = { hackathon -> BookMarkAdded(hackathon) },
                    OnBookmarkClickRemove = { hackathon -> BookMarkRemove(hackathon) },
                    OnLikeClickAdd = { hackathon -> UpvoteAdd(hackathon) },
                    OnLikeClickRemove = { hackathon -> UpvoteRemove(hackathon) },
                    OnDetailsClick = { hackathon -> GoToDetailsActivity(hackathon)}
                )
                recyclerViewHackathonsAll.adapter = recyclerViewHackathons_adapterAll

            }
            "Registered" -> {
                recyclerViewHackathons_adapterAll = Hackathon_Adapter(
                    Hackathons_listRegistered ,
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

    private fun GoToDetailsActivity(hackathon: Hackathon_Recycler) {

        val intent = Intent(requireContext(),DetailsHackathon::class.java)
        intent.putExtra("HackathonEventID",hackathon.EventId)
        intent.putExtra("CurrentUserID",CurrentuseridforMyHackathon)
        startActivity(intent)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshAdapter(status: String) {
        when (status) {
            "All" -> recyclerViewHackathons_adapterAll.notifyDataSetChanged()
            "Created" -> recyclerViewHackathons_adapterAll.notifyDataSetChanged()
            "Registered" -> recyclerViewHackathons_adapterAll.notifyDataSetChanged()
        }
    }

    private fun ItemTitle(selectItemText: String): String {

        return selectItemText

    }

//    private fun SearchEvent(user : String , status : String ,searchedID :String)
//    {
//
//        val DataBase = FirebaseDatabase.getInstance()
//
//        DataBase.getReference("USERS").child(user).child("hackathons")
//            .orderByChild("EventStatus").equalTo(status).addListenerForSingleValueEvent( object : ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if(snapshot.exists())
//                    {
//                        snapshot.children.forEach { hackathon ->
//                             val IDs= (hackathon.key ?:"Not Got").toString()
//                            Log.d("SearchEventID","The Event ID searched is : IDS")
//
//                            if (IDs != "Not Got")
//                            {
//                                EventIDS.add(IDs)
//                                Log.d("IDs Added","Added In list..")
//                            }
//
//                        }
//
//                        if (EventIDS.isEmpty())
//                        {
//                            Toast.makeText(requireContext(),"No Event With This Event ID is recorded here.",Toast.LENGTH_SHORT).show()
//                        }else{
//
//                            val FoundID = EventIDS.filter { it.contains(searchedID) }
//                            for (index in FoundID.indices) {
//
//                                DataBase.getReference("HACKATHON").child(FoundID[index])
//                                    .addListenerForSingleValueEvent(object : ValueEventListener{
//                                        override fun onDataChange(snapshot: DataSnapshot) {
//                                            if (snapshot.exists())
//                                            {
//                                                val eventname = snapshot.child("eventName").value.toString()
//                                                val bannerURL = snapshot.child("bannerUrl").value.toString()
//                                                val host = snapshot.child("hostedBy").value.toString()
//                                                val prize = snapshot.child("prize").value.toString()
//
//                                                val bookmark = snapshot.child("BookMark").child(CurrentuseridforMyHackathon).getValue(String::class.java) ?: "None"
//
//                                                val VotedText = snapshot.child("votes").child("upvoted").child(CurrentuseridforMyHackathon).getValue(String::class.java) ?: "none"
//
//                                                val status = snapshot.child("status").value.toString()
//
//                                                Log.d("ValueStatus","Value of status for event is $status")
//                                                val information_event = Hackathon_Recycler(bannerURL,FoundID[index],eventname,host,prize,bookmark,VotedText,status)
//                                                Log.d("HackathonIDData","EventName -> $eventname" +
//                                                        "bannerurl -> $bannerURL" +
//                                                        "host -> $host" +
//                                                        "Prize -> $prize" +
//                                                        "bookmark -> $bookmark" +
//                                                        "Voted -> $VotedText")
//
//                                                if(!Hackathons_listSearch.contains(information_event))
//                                                {
//                                                    Hackathons_listSearch.add(information_event)
//                                                }
//
//                                            }
//                                        }
//
//                                        override fun onCancelled(error: DatabaseError) {
//                                            TODO("Not yet implemented")
//                                        }
//                                    })
//                            }
//
//
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//            })
//
//
//
//
//    }

    private fun BookMarkRemove(hackathon: Hackathon_Recycler) {

        val HACKATHONID = hackathon.EventId.toString()

        val ref = FirebaseDatabase.getInstance().getReference("USERS")
        val HackathonRef = FirebaseDatabase.getInstance().getReference("HACKATHON")

        val update = mapOf(
            "bookmark" to "Removed"
        )
        ref.child(CurrentuseridforMyHackathon).child("hackathons").child(HACKATHONID).updateChildren(update).addOnSuccessListener {
            Toast.makeText(requireContext(),"Successfully Removed From Bookmark",Toast.LENGTH_SHORT).show()


        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Error : Couldn't Removed From Bookmark",Toast.LENGTH_SHORT).show()
        }

        val updateBookmark = mapOf(
            "BookMark/$CurrentuseridforMyHackathon" to ""
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
        ref.child(CurrentuseridforMyHackathon).child("hackathons").child(HACKATHONID).updateChildren(update).addOnSuccessListener {
            Toast.makeText(requireContext(),"Succesfully Added To BookMark.",Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Error : Couldn't Added To BookMark.",Toast.LENGTH_SHORT).show()
        }
        val updateBookmark = mapOf(
            "BookMark/$CurrentuseridforMyHackathon" to "Added"
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
                        "upvoted/$CurrentuseridforMyHackathon" to "no"
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
                       "upvoted/$CurrentuseridforMyHackathon" to "yes"
                   )

                   ref.child(HACKATHONID).child("votes").updateChildren(update_voted).addOnSuccessListener {

                       hackathon.Voted = "yes"
                        recyclerViewHackathons_adapterAll.notifyDataSetChanged()

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