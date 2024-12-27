package com.example.hackverse

import com.google.android.gms.common.api.internal.StatusPendingResult

data class coders(
    val username: String? = null ,
    val name : String? = null ,
    val email : String? = null ,
    val password : String? = null ,
    val url : String? = null ,
    val friends : Map<String,FriendID>? = null
)

data class FriendID(
    val urlOfFriend : String? =null ,
    val userNameOfFriend: String? = null ,
    val status : String? = null
)

