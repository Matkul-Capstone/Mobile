package com.example.speaktoo.api.model.user

data class RegisterRequest (
    val email: String,
    val password: String,
    val username: String
)

data class RegisterResponse (
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: RegisterData?
)

data class RegisterData (
    val uid: String
)