package com.example.hackverse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class Comments_Adapter(
    private val CommentList : ArrayList<Comment_recycler> ,
) : RecyclerView.Adapter<Comments_Adapter.UserCommentViewHolder>(){
    inner class UserCommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val useridcomment = itemView.findViewById<TextView>(R.id.ShowUserIDComment)
        val usernamecomment = itemView.findViewById<TextView>(R.id.ShowUserNameComment)
        val profileImgUrl = itemView.findViewById<ImageView>(R.id.CommentImageView)
        val showcomment = itemView.findViewById<TextView>(R.id.TextViewShowComment)

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Comments_Adapter.UserCommentViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_for_comment,parent,false)
        return UserCommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: Comments_Adapter.UserCommentViewHolder, position: Int) {

        val Comment = CommentList[position]

        holder.useridcomment.text = Comment.userIDComment ?: "No ID"
        holder.usernamecomment.text = Comment.UserNameComment ?: "No Name"

        Glide.with(holder.itemView.context)
            .load(Comment.UserProfileUrl)
            .error(R.drawable.default_profile_pic_vector)
            .placeholder(R.drawable.loading_for_image_vector)
            .into(holder.profileImgUrl)

        holder.showcomment.text = Comment.CommentOfUsers ?: "No Comment"
    }

    override fun getItemCount(): Int {
        return CommentList.size
    }
}