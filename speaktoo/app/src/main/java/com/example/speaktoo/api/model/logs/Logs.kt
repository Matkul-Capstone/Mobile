package com.example.speaktoo.api.model.logs

data class Logs (
    val log_id: Int,
    val user_id: String,
    val sentence_id: Int,
    val score: Int,
    val completed: Int,
    val timestamp: String,
    val chapter: String
)