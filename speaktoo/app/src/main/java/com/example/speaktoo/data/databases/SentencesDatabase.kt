package com.example.speaktoo.data.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.speaktoo.data.dao.SentencesDao
import com.example.speaktoo.data.models.Sentence

@Database(entities = [Sentence::class], version = 1, exportSchema = false)
abstract class SentencesDatabase : RoomDatabase() {

    abstract fun sentencesDao(): SentencesDao

    companion object {
        @Volatile
        private var INSTANCE: SentencesDatabase? = null

        fun getDatabase(context: Context): SentencesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SentencesDatabase::class.java,
                    "sentence_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}