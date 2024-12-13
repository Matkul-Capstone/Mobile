package com.example.speaktoo.data.dao

import androidx.room.*
import com.example.speaktoo.data.models.Sentence

@Dao
interface SentencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSentence(sentence: Sentence)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSentences(sentences: List<Sentence>)

    @Update
    suspend fun updateSentence(sentence: Sentence)

    @Query("UPDATE sentences_table SET completed = :completed WHERE sentence_id = :sentenceId")
    suspend fun updateCompletionStatus(sentenceId: Int, completed: Int)

    @Delete
    suspend fun deleteSentence(sentence: Sentence)

    @Query("DELETE FROM sentences_table")
    suspend fun deleteAllSentences()

    @Query("SELECT * FROM sentences_table")
    suspend fun getAllSentences(): List<Sentence>

    @Query("SELECT * FROM sentences_table WHERE sentence_type = :sentenceType")
    suspend fun getSentencesByType(sentenceType: String): List<Sentence>

    @Query("SELECT * FROM sentences_table WHERE sentence_id = :sentenceId LIMIT 1")
    suspend fun getSentenceById(sentenceId: Int): Sentence?

    @Query("SELECT SUM(completed) FROM sentences_table WHERE sentence_type = :sentenceType")
    suspend fun getSumCompleted(sentenceType: String): Int?
}