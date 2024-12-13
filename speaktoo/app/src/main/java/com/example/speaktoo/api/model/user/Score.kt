package com.example.speaktoo.api.model.user

data class ChangeScoreRequest(
    val score: Int
)

data class ChangeScoreResponse(
    val success: Boolean,
    val status: Int,
    val message: String
)