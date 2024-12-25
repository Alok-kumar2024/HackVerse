package com.example.hackverse

data class coders(
    val username: String? = null ,
    val name : String? = null ,
    val email : String? = null ,
    val password : String? = null ,
    val url : String? = null ,
    val friends : Friends? = Friends()
)

data class Friends(
    val incoming : Map<String,Boolean>? =null ,
    val outcoming : Map<String,Boolean>? =null ,
    val added : Map<String,Boolean>? =null ,
)

