package com.example.loginandregisterkotlinfragment.data

import com.google.gson.annotations.SerializedName

data class UserDataInfo(
    @SerializedName("info")
    val userDataModel: UserDataModel
)