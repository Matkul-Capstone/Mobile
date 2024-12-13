package com.example.speaktoo.api.model.user

data class ChangeUsernameRequest (
    val newUsername: String
)

data class ChangeUsernameResponse (
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: ChangeUsernameData?
)

data class ChangeUsernameData (
    val uid: String,
    val newUsername: String
)