package com.example.speaktoo.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logs_table")
data class Log(
    @PrimaryKey val timestamp: String,
    val user_id: String,
    val sentence_id: Int,
    val score: Int,
    val completed: Int,
    val chapter: String
)