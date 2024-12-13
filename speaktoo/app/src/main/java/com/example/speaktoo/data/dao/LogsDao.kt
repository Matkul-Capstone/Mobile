package com.example.speaktoo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.speaktoo.data.models.Log

@Dao
interface LogsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<Log>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: Log)

    @Query("SELECT * FROM logs_table WHERE user_id = :userId ORDER BY timestamp DESC")
    suspend fun getLogsByUserId(userId: String): List<Log>

    @Query("DELETE FROM logs_table WHERE user_id = :userId")
    suspend fun deleteLogsByUserId(userId: String)
}