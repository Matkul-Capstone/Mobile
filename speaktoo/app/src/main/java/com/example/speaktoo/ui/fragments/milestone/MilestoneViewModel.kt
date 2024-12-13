package com.example.speaktoo.ui.fragments.milestone

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.speaktoo.data.history.History
import com.example.speaktoo.data.models.Log
import com.example.speaktoo.data.repository.LogsRepository
import kotlinx.coroutines.launch

class MilestoneViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LogsRepository(application)

    private val _historyList = MutableLiveData<List<History>>()
    val historyList: LiveData<List<History>> get() = _historyList

    init {
        _historyList.value = listOf()
    }

    private fun mapLogsToHistory(log: Log): History {
        return History(
            level = log.sentence_id.toString(),
            timestamp = log.timestamp,
            score = log.score
        )
    }

    // Fetch logs for a user
    fun fetchAllLogsForUser(userId: String) {
        viewModelScope.launch {
            try {
                val logs = repository.getLogsByUserId(userId)

                if (logs.isNotEmpty()) {
                    _historyList.value = logs.map { mapLogsToHistory(it) }
                } else {
                    _historyList.value = emptyList()
                }
            } catch (exception: Exception) {
                _historyList.value = emptyList()
            }
        }
    }

    fun addHistory(levelName: String, id: Int, timestamp: String, score: Int) {
        val updatedHistory = _historyList.value.orEmpty().toMutableList()
        updatedHistory.add(History(levelName, timestamp, score))
        _historyList.value = updatedHistory
    }

    fun clearHistory() {
        _historyList.value = emptyList()
    }
}