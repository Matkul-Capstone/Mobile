package com.example.speaktoo.api.model.user

import com.example.speaktoo.api.model.logs.Logs


data class UserModel (
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: UserData
)

data class UserData(
    val user_id: String,
    var username: String,
    var password: String?,
    val user_email: String,
    var user_type: String?,
    var beginner_score: Int,
    var intermediate_score: Int,
    var advance_score: Int,
    var logs: List<Logs>?
)