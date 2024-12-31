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
import com.example.hackverse.databinding.FragmentMyHackathonsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class My_HackathonsFragment : Fragment() {
    private var _binding : FragmentMyHackathonsBinding? =null
    private val binding get() = _binding!!

    private lateinit var recyclerViewHackathonsAll : RecyclerView
    private lateinit var recyclerViewHackathons_adapterAll : Hackathon_Adapter
    private var Hackathons_listAll = arrayListOf<Hackathon_Recycler>()

   // private lateinit var recyclerViewHackathonsCreated : Hackathon_Recycler
    private lateinit var recyclerViewHackathons_adapterCreated : Hackathon_Adapter
    private var Hackathons_listCreated = arrayListOf<Hackathon_Recycler>()

  //  private lateinit var recyclerViewHackathonsRegistered : Hackathon_Recycler
    private lateinit var recyclerViewHackathons_adapterRegistered : Hackathon_Adapter
    private var Hackathons_listRegistered = arrayListOf<Hackathon_Recycler>()



    private lateinit var database : DatabaseReference

    private lateinit var CurrentuseridforMyHackathon : String

    private var selectedItemId : Int = R.id.Menu_All_Hackathons
    private var SelectItemText : String = "All"

    private var Created_Hackathon_list = mutableListOf<String>()
    private var Registered_Hackathon_list = mutableListOf<String>()

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
                        Log.d("SelectItmText","The SelectItem Text is $SelectItemText")
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

                   if (Created_Hackathon_list.isEmpty())
                   {
                       Toast.makeText(requireContext(),"No Hackathons created yet",Toast.LENGTH_SHORT).show()
                   }else{

                       for (index in Created_Hackathon_list.indices)
                       {
                           val createdHackathonId = Created_Hackathon_list[index]
                           Log.d("Created ID of Hackathons","The created IDs of hackathon is $createdHackathonId")

                           val SearchCreateDatabase = FirebaseDatabase.getInstance()

                           SearchCreateDatabase.getReference("HACKATHON").child(createdHackathonId).addListenerForSingleValueEvent(object : ValueEventListener{
                               @SuppressLint("NotifyDataSetChanged")
                               override fun onDataChange(snapshot: DataSnapshot) {
                                   if (snapshot.exists())
                                   {
                                       Log.d("InsideHackathon","Yes")
                                       val Eventinfo = snapshot.getValue(hackathons_info::class.java)

                                       val eventname = snapshot.child("eventName").value.toString()
                                       val bannerURL = snapshot.child("bannerUrl").value.toString()
                                       val host = snapshot.child("hostedBy").value.toString()
                                       val prize = snapshot.child("prize").value.toString()
                                       val bookmark = snapshot.child("bookMark").value.toString()

                                                       val status = "Created"

                                                       val information_event = Hackathon_Recycler(bannerURL,createdHackathonId,eventname,host,prize,bookmark,status)

                                       Log.d("HackathonIDData","EventName -> $eventname" +
                                               "bannerurl -> $bannerURL" +
                                               "host -> $host" +
                                               "Prize -> $prize" +
                                               "bookmark -> $bookmark")

                                       Hackathons_listAll.add(information_event)
                                       Hackathons_listCreated.add(information_event)


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

                    if (Registered_Hackathon_list.isEmpty())
                    {
                        Toast.makeText(requireContext(),"Not Registered in any Hackathons Yet.",Toast.LENGTH_SHORT).show()
                    }else{

                        for(index in Registered_Hackathon_list.indices)
                        {
                            val RegisteredHackathonID = Registered_Hackathon_list[index]
                            Log.d("Registered ID of Hackathons","The created IDs of hackathon is $RegisteredHackathonID")

                            val SearchRegisteredDatabase = FirebaseDatabase.getInstance()

                            SearchRegisteredDatabase.getReference("HACKATHON").child(RegisteredHackathonID)
                                .addListenerForSingleValueEvent(object : ValueEventListener{
                                    @SuppressLint("NotifyDataSetChanged")
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists())
                                        {
                                            val Eventinfo = snapshot.getValue(hackathons_info::class.java)

                                            val eventname = Eventinfo?.EventName.toString()
                                            val bannerURL = Eventinfo?.bannerUrl.toString()
                                            val host = Eventinfo?.HostedBy.toString()
                                            val prize = Eventinfo?.Prize.toString()
                                            val bookmark = Eventinfo?.BookMark.toString()
                                            val status ="Registered"
                                            Log.d("ValueStatus","Value of status for event is $status")
                                            val information_event = Hackathon_Recycler(bannerURL,RegisteredHackathonID,eventname,host,prize,bookmark,status)
                                            Hackathons_listAll.add(information_event)
                                            Hackathons_listRegistered.add(information_event)



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
                    OnBookmarkClickAdd = { hackathon -> BookMarkAdded(hackathon) },
                    OnBookmarkClickRemove = { hackathon -> BookMarkRemove(hackathon) },
                    OnLikeClickAdd = { hackathon -> UpvoteAdd(hackathon) },
                    OnLikeClickRemove = { hackathon -> UpvoteRemove(hackathon) },
                )
                recyclerViewHackathonsAll.adapter = recyclerViewHackathons_adapterAll

            }
            "Created" -> {
                recyclerViewHackathons_adapterCreated = Hackathon_Adapter(
                    Hackathons_listCreated ,
                    OnBookmarkClickAdd = { hackathon -> BookMarkAdded(hackathon) },
                    OnBookmarkClickRemove = { hackathon -> BookMarkRemove(hackathon) },
                    OnLikeClickAdd = { hackathon -> UpvoteAdd(hackathon) },
                    OnLikeClickRemove = { hackathon -> UpvoteRemove(hackathon) },
                )
                recyclerViewHackathonsAll.adapter = recyclerViewHackathons_adapterCreated

            }
            "Registered" -> {
                recyclerViewHackathons_adapterRegistered = Hackathon_Adapter(
                    Hackathons_listRegistered ,
                    OnBookmarkClickAdd = { hackathon -> BookMarkAdded(hackathon) },
                    OnBookmarkClickRemove = { hackathon -> BookMarkRemove(hackathon) },
                    OnLikeClickAdd = { hackathon -> UpvoteAdd(hackathon) },
                    OnLikeClickRemove = { hackathon -> UpvoteRemove(hackathon) },
                )
                recyclerViewHackathonsAll.adapter = recyclerViewHackathons_adapterRegistered

            }
        }

    }

    private fun ItemTitle(selectItemText: String): String {

        return selectItemText

    }

    private fun BookMarkRemove(hackathon: Hackathon_Recycler) {

        val HACKATHONID = hackathon.EventId.toString()

        val ref = FirebaseDatabase.getInstance().getReference("USERS")

        val update = mapOf(
            "bookmark" to ""
        )
        ref.child(CurrentuseridforMyHackathon).child("hackathons").child(HACKATHONID).updateChildren(update).addOnSuccessListener {
            Toast.makeText(requireContext(),"Successfully Removed From Bookmark",Toast.LENGTH_SHORT).show()


        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Error : Couldn't Removed From Bookmark",Toast.LENGTH_SHORT).show()
        }

    }

    private fun BookMarkAdded(hackathon: Hackathon_Recycler) {

        val HACKATHONID = hackathon.EventId.toString()

        val ref = FirebaseDatabase.getInstance().getReference("USERS")

        val update = mapOf(
            "bookmark" to "Added"
        )
        ref.child(CurrentuseridforMyHackathon).child("hackathons").child(HACKATHONID).updateChildren(update).addOnSuccessListener {
            Toast.makeText(requireContext(),"Succesfully Added To BookMark.",Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Error : Couldn't Added To BookMark.",Toast.LENGTH_SHORT).show()
        }

    }

    private fun UpvoteRemove(hackathon: Hackathon_Recycler) {

        val ref = FirebaseDatabase.getInstance().getReference("HACKATHON")

        val HACKATHONID = hackathon.EventId.toString()

        ref.child(HACKATHONID).addListenerForSingleValueEvent(object : ValueEventListener{
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

        ref.child(HACKATHONID).addListenerForSingleValueEvent(object : ValueEventListener{
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
               }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

}