package com.example.speaktoo.data.repository

import android.content.Context
import com.example.speaktoo.data.dao.LogsDao
import com.example.speaktoo.data.databases.LogsDatabase
import com.example.speaktoo.data.models.Log


class LogsRepository(context: Context) {

    private val logsDao: LogsDao

    init {
        val db = LogsDatabase.getDatabase(context)
        logsDao = db.logsDao()
    }

    suspend fun insertLogs(logs: List<Log>) {
        logsDao.insertLogs(logs)
    }

    suspend fun insertLog(log: Log) {
        logsDao.insertLogs(listOf(log))
    }

    suspend fun getLogsByUserId(userId: String): List<Log> {
        return logsDao.getLogsByUserId(userId)
    }

    suspend fun deleteLogsByUserId(userId: String) {
        logsDao.deleteLogsByUserId(userId)
    }
}