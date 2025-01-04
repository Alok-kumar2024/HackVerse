package com.example.hackverse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlin.math.round

class participants_adapter (
    private val participantsLIST : ArrayList<participants_recycler> ,
    private val onClickViewProfile : (participants_recycler) -> Unit = {} ,
//    private val onClickMessageButton : (participants_recycler) -> Unit = {}
) : RecyclerView.Adapter<participants_adapter.UserHolderParticipants>(){

    inner class UserHolderParticipants(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val userIdPArticipants : TextView = itemView.findViewById(R.id.ShowUserIDParticipants)
        val userNameParticipants : TextView = itemView.findViewById(R.id.ShowUserNameParticipants)
        val profileURL : ImageView = itemView.findViewById(R.id.ParticipantsImageView)

        val viewProfile : Button = itemView.findViewById(R.id.ButtonViewProfileParticipants)
//        val message : Button = itemView.findViewById(R.id.ButtonMessageParticipants)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolderParticipants {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_for_participants,parent,false)
        return UserHolderParticipants(view)
    }

    override fun getItemCount(): Int {
        return participantsLIST.size
    }

    override fun onBindViewHolder(holder: UserHolderParticipants, position: Int) {

        val participants = participantsLIST[position]

        holder.userIdPArticipants.text = participants.userIdParticipants.toString()
        holder.userNameParticipants.text = participants.usernameParticipants.toString()

        Glide.with(holder.itemView.context)
            .load(participants.profileUrl)
            .error(R.drawable.default_profile_pic_vector)
            .placeholder(R.drawable.loading_for_image_vector)
            .into(holder.profileURL)

        holder.viewProfile.setOnClickListener {
            onClickViewProfile(participants)
        }

//        holder.message.setOnClickListener {
//            onClickMessageButton(participants)
//        }
    }

}