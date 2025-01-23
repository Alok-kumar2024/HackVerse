package com.example.hackverse

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority

class Friends_Adaptar (
    private val FriendsList : ArrayList<Friends_Recycler> ,

    private val onAcceptClick : (Friends_Recycler) -> Unit = {} , // when Accept button is clicked
    private val onRejectClick : (Friends_Recycler) -> Unit = {} , // when Reject button is clicked
    private val onRemoveClick : (Friends_Recycler) -> Unit = {} , // when Remove buttonis clicked
    private val onAddClick : (Friends_Recycler) -> Unit = {} , // when Add button is clicked
    private val onCancelClick : (Friends_Recycler) -> Unit = {} ,
    private val onClickViewProfile : (Friends_Recycler) -> Unit = {}



) : RecyclerView.Adapter<Friends_Adaptar.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val profileImage : ImageView = itemView.findViewById(R.id.ProfileImageRecycler)
        val userIdRecycler : TextView = itemView.findViewById(R.id.UserIdRecyclerShow)
        val  userNameRecycyler : TextView =  itemView.findViewById(R.id.UserNameRecyclerShow)
        val statusConditon : TextView = itemView.findViewById(R.id.ShowStatusCondition)

        val addedbutton = itemView.findViewById<Button>(R.id.buttonAddedFriend)
        val removebutton = itemView.findViewById<Button>(R.id.buttonRemoveFriend)
        val rejectbutton = itemView.findViewById<Button>(R.id.buttonRejectFriend)
        val acceptbutton = itemView.findViewById<Button>(R.id.buttonAccecptFriend)
        val cancelbutton = itemView.findViewById<Button>(R.id.buttonCancelFriend)
        val addbutton = itemView.findViewById<Button>(R.id.buttonAddFriend)

        val viewProfileBtn = itemView.findViewById<Button>(R.id.ButtonViewProfileFriends)

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_for_friends,parent,false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {

        return FriendsList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val user = FriendsList[position]
        holder.userIdRecycler.text = user.userID_search ?: " No ID"
        holder.userNameRecycyler.text = user.userName_search ?: " No Name"
        holder.statusConditon.text = user.status ?: "None"

        Glide.with(holder.itemView.context)
            .load(user.profileImg)
            .placeholder(R.drawable.loading_for_image_vector)
            .error(R.drawable.profile_menu)
            .into(holder.profileImage)

        holder.addbutton.visibility = View.GONE
        holder.addedbutton.visibility = View.GONE
        holder.acceptbutton.visibility = View.GONE
        holder.rejectbutton.visibility = View.GONE
        holder.removebutton.visibility = View.GONE
        holder.cancelbutton.visibility = View.GONE
        holder.viewProfileBtn.visibility = View.GONE

        when(user.status){
            "incoming" -> {
                holder.acceptbutton.visibility = View.VISIBLE
                holder.rejectbutton.visibility = View.VISIBLE

                holder.acceptbutton.setOnClickListener { onAcceptClick(user) }
                holder.rejectbutton.setOnClickListener { onRejectClick(user) }


            }

            "outcoming" -> {

                holder.cancelbutton.visibility = View.VISIBLE

                holder.cancelbutton.setOnClickListener { onCancelClick(user) }


            }

            "added" -> {

                holder.addedbutton.visibility = View.VISIBLE
                holder.removebutton.visibility = View.VISIBLE
                holder.viewProfileBtn.visibility = View.VISIBLE

                holder.viewProfileBtn.setOnClickListener {
                    onClickViewProfile(user)
                }

                holder.removebutton.setOnClickListener { onRemoveClick(user) }

            }
            else -> {

                holder.addbutton.visibility = View.VISIBLE

                holder.addbutton.setOnClickListener { onAddClick(user) }
            }
        }


    }
    fun removeFriend(friend: Friends_Recycler)
    { val position = FriendsList.indexOf(friend)
        if (position >= 0)
        { FriendsList.removeAt(position)
            notifyItemRemoved(position) }
    }
}