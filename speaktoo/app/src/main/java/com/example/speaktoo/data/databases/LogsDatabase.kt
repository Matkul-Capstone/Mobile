package com.example.speaktoo.data.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.speaktoo.data.dao.LogsDao
import com.example.speaktoo.data.models.Log

@Database(entities = [Log::class], version = 1, exportSchema = false)
abstract class LogsDatabase : RoomDatabase() {

    abstract fun logsDao(): LogsDao

    companion object {
        @Volatile
        private var INSTANCE: LogsDatabase? = null

        fun getDatabase(context: Context): LogsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LogsDatabase::class.java,
                    "logs_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}