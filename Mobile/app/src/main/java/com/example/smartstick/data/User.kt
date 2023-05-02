package com.example.smartstick.data

data class User(
    var email:String? = null ,
                var password:String? = null  ,
                var relative_number :String?=null ,
                val profilePicUrl: String
){
    constructor() : this("", "", "", "")
}

