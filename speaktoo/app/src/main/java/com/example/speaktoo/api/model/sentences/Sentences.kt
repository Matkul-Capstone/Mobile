package com.example.speaktoo.api.model.sentences

data class SentencesResponse (
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: List<SentencesData>?
)

data class SentencesData (
    val sentence_id: Int,
    val sentence_type: String,
    val chapter: String,
    val sentence: String,
    var completed: Int
)