package com.example.hackverse

data class hackathons_info(
    val bannerUrl : String? =null ,
    val EventName : String? = null ,
    val HostedBy : String? = null ,
    val Prize : String? = null ,
    val CreatedBy : String? = null ,
    val BookMark : Map<String,String> = mapOf() ,
    val status : String? = null ,
    val description : String? = null ,
    val comment : Map<String,String> = mapOf() ,
    val votes : Vote = Vote() ,
    val Time : time = time()
)

data class Vote(
    val upvoted : Map<String,String> = mapOf() ,
    val upvotes : Int = 0 ,
    val downvotes : Int = 0

)

data class time(
    val ActiveDate : String?= null ,
    val ActiveTime : String? = null ,
    val CreatedDateTime : String? = null ,
    val ClosedDate : String? = null ,
    val CloseTime : String? = null

)
