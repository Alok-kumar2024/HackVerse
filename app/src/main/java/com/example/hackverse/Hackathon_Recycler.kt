package com.example.hackverse

data class Hackathon_Recycler(
    val bannerUrl : String? =null ,
    val EventId : String? = null ,
    val EventName : String? = null ,
    val HostedBy : String? = null ,
    val Prize : String? = null ,
    var BookMark : String? = null , // Added , None
    var Voted : String? = null ,
    val status : String? = null // All , Registered , Created , Active , Inactive
)
