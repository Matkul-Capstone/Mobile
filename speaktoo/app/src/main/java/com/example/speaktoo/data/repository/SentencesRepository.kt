package com.example.speaktoo.data.repository

import android.content.Context
import com.example.speaktoo.data.dao.SentencesDao
import com.example.speaktoo.data.databases.SentencesDatabase
import com.example.speaktoo.data.models.Sentence

class SentencesRepository (context: Context) {
    private val sentencesDao: SentencesDao

    init {
        val db = SentencesDatabase.getDatabase(context)
        sentencesDao = db.sentencesDao()
    }

    suspend fun insertSentence(sentence: Sentence) {
        sentencesDao.insertSentence(sentence)
    }

    suspend fun insertSentences(sentences: List<Sentence>) {
        sentencesDao.insertSentences(sentences)
    }

    suspend fun updateSentence(sentence: Sentence) {
        sentencesDao.updateSentence(sentence)
    }

    suspend fun updateCompletionStatus(sentenceId: Int, completed: Int) {
        sentencesDao.updateCompletionStatus(sentenceId, completed)
    }

    suspend fun deleteSentence(sentence: Sentence) {
        sentencesDao.deleteSentence(sentence)
    }

    suspend fun deleteAllSentences() {
        sentencesDao.deleteAllSentences()
    }

    suspend fun getAllSentences(): List<Sentence> {
        return sentencesDao.getAllSentences()
    }

    suspend fun getSentencesByType(sentenceType: String): List<Sentence> {
        return sentencesDao.getSentencesByType(sentenceType)
    }

    suspend fun getSentenceById(sentenceId: Int): Sentence? {
        return sentencesDao.getSentenceById(sentenceId)
    }

    suspend fun getSumCompleted(sentenceType: String): Int? {
        return sentencesDao.getSumCompleted(sentenceType)
    }
}