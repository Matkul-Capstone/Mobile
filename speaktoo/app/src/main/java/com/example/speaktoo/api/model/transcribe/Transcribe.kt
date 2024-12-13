package com.example.speaktoo.api.model.transcribe

data class TranscribeRequest(
    val uid: String,
    val sentence: String,
    val timestamp: String
)

data class TranscribeResponse(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: TranscribeData?
)

data class TranscribeData(
    val uid: String,
    val chapter: String,
    val sid: String,
    val timestamp: String,
    val sentence: String,
    val score: Int,
    val correct_words: List<String>?,
    val wrong_words: List<String>?,
    val completed: Boolean
)