package com.example.hackverse

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class Friends_Adaptar (private val FriendsList : ArrayList<Friends_Recycler>) : RecyclerView.Adapter<Friends_Adaptar.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val profileImage : ImageView = itemView.findViewById(R.id.ProfileImageRecycler)
        val userIdRecycler : TextView = itemView.findViewById(R.id.UserIdRecyclerShow)
        val  userNameRecycyler : TextView =  itemView.findViewById(R.id.UserNameRecyclerShow)

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

        Glide.with(holder.itemView.context)
            .load(user.profileImg)
            .placeholder(R.drawable.loading_for_image_vector)
            .error(R.drawable.profile_menu)
            .into(holder.profileImage)

    }
}