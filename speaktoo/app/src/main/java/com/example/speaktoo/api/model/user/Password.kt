package com.example.speaktoo.api.model.user

data class NewPasswordRequest(
    val email: String
)

data class NewPasswordResponse(
    val success: Boolean,
    val status: Int,
    val message: String
)