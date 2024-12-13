package com.example.speaktoo.api.model.user

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: UserData?
)