package com.example.speaktoo.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sentences_table")
data class Sentence (
    @PrimaryKey val sentence_id: Int,
    val sentence_type: String,
    val chapter: String,
    val sentence: String,
    var completed: Int
)