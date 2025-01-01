package com.example.hackverse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class Hackathon_Adapter(
    private val HackathonInfo : ArrayList<Hackathon_Recycler> ,

    private val OnBookmarkClickAdd : (Hackathon_Recycler) -> Unit = {} ,
    private val OnBookmarkClickRemove : (Hackathon_Recycler) -> Unit = {} ,
    private val OnLikeClickAdd : (Hackathon_Recycler) -> Unit = {} ,
    private val OnLikeClickRemove : (Hackathon_Recycler) -> Unit = {}

) : RecyclerView.Adapter<Hackathon_Adapter.UserHackathonViewHolder>() {

    inner class UserHackathonViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val bannerImage : ImageView = itemView.findViewById(R.id.BannerImageView)
        val EventIdShow : TextView = itemView.findViewById(R.id.EventIDShow)
        val EventNameShow : TextView = itemView.findViewById(R.id.EventNameShow)
        val EventHostName : TextView = itemView.findViewById(R.id.EventHostName)
        val EventPrizeShow : TextView = itemView.findViewById(R.id.EventPrizeShow)

        val BookmarkAddButton : ImageButton = itemView.findViewById(R.id.BookMarkAddButton)
        val BookmarkRemoveButton : ImageButton = itemView.findViewById(R.id.BookMarkRemoveButton)
        val LikeAddButton : ImageButton = itemView.findViewById(R.id.LikeBeforeClick)
        val LikeRemoveButton : ImageButton = itemView.findViewById(R.id.LikeAfterClick)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHackathonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_for_hackathon,parent,false)
        return UserHackathonViewHolder(view)
    }

    override fun getItemCount(): Int {

        return HackathonInfo.size
    }

    override fun onBindViewHolder(holder: UserHackathonViewHolder, position: Int) {

        val hackathon = HackathonInfo[position]

        holder.EventIdShow.text = hackathon.EventId ?: "No ID"
        holder.EventNameShow.text = hackathon.EventName ?: "No Name"
        holder.EventHostName.text = hackathon.HostedBy ?: "No Host"
        holder.EventPrizeShow.text = hackathon.Prize ?: "No Prize"

        Glide.with(holder.itemView.context)
            .load(hackathon.bannerUrl)
            .placeholder(R.drawable.loading_for_image_vector)
            .into(holder.bannerImage)

//        holder.BookmarkAddButton.visibility = View.GONE
        holder.BookmarkRemoveButton.visibility = View.GONE
        holder.LikeRemoveButton.visibility = View.GONE


        when(hackathon.BookMark)
        {
            "Added" -> {

                holder.BookmarkAddButton.visibility = View.GONE
                holder.BookmarkRemoveButton.visibility = View.VISIBLE

                holder.BookmarkRemoveButton.setOnClickListener {
                    OnBookmarkClickRemove(hackathon)
                }

            }
            "Removed" -> {

                holder.BookmarkAddButton.setOnClickListener {
                    OnBookmarkClickAdd(hackathon)
                    holder.BookmarkAddButton.visibility = View.GONE
                    holder.BookmarkRemoveButton.visibility = View.VISIBLE

                }

            }
            else -> {
                holder.BookmarkAddButton.visibility = View.VISIBLE
                holder.BookmarkAddButton.setOnClickListener { OnBookmarkClickAdd(hackathon) }
            }
        }

        when(hackathon.Voted)
        {
            "yes" ->{

                holder.LikeAddButton.visibility = View.GONE
                holder.LikeRemoveButton.visibility = View.VISIBLE

                holder.LikeRemoveButton.setOnClickListener {
                    OnLikeClickRemove(hackathon)

                }
            }
            "no" -> {

                holder.LikeAddButton.visibility = View.VISIBLE
                holder.LikeRemoveButton.visibility = View.GONE

                holder.LikeAddButton.setOnClickListener {

                    OnLikeClickAdd(hackathon)


                }

            }
            else -> {
                holder.LikeAddButton.visibility = View.VISIBLE
                holder.LikeAddButton.setOnClickListener { OnLikeClickAdd(hackathon) }
            }
        }






    }

}