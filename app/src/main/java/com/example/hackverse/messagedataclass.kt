package com.example.hackverse

data class messagedataclass(
    val chatbetween : Map<String,chat>? = null
)
data class chat(
    val usertimestamp : Map<String,users>? = null
)

data class users(
    val userid : String? = null ,
    val message : String? = null ,
    val timestamp : String? = null
)
