package com.example.speaktoo.ui.fragments.practice

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.speaktoo.api.config.RetrofitClient
import com.example.speaktoo.api.model.transcribe.TranscribeResponse
import com.example.speaktoo.api.model.user.ChangeScoreRequest
import com.example.speaktoo.api.model.user.ChangeScoreResponse
import com.example.speaktoo.data.models.Log
import com.example.speaktoo.data.models.Sentence
import com.example.speaktoo.data.repository.LogsRepository
import com.example.speaktoo.data.repository.SentencesRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class PracticeViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val DEFAULT_LEVEL_NAME = "Beginner"
        private const val DEFAULT_CHAPTER_TITLE = "Chapter 1"
        private const val DEFAULT_CHAPTER_DESC = "Basic Greetings and Introductions"
        private const val DEFAULT_SENTENCE = "Hello. My name is..."
    }

    private val _sentenceData = MutableLiveData<Sentence?>()
    val sentenceData: LiveData<Sentence?> get() = _sentenceData

    private val _practiceTitle = MutableLiveData(DEFAULT_CHAPTER_TITLE)
    val practiceTitle: LiveData<String> get() = _practiceTitle

    private val _sentenceId = MutableLiveData(1)
    val sentenceId: LiveData<Int> get() = _sentenceId

    private val _sentenceText = MutableLiveData(DEFAULT_SENTENCE)
    val sentenceText: LiveData<String> get() = _sentenceText

    private var mediaPlayer: MediaPlayer? = null

    private val _transcribeResponse = MutableLiveData<TranscribeResponse>()
    val transcribeResponse: LiveData<TranscribeResponse> get() = _transcribeResponse

    private val _changeScoreResponse = MutableLiveData<ChangeScoreResponse>()
    val changeScoreResponse: LiveData<ChangeScoreResponse> get() = _changeScoreResponse

    private val sharedPreferences by lazy {
        getApplication<Application>().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
    }

    init {
        loadSentenceFromPreferences()
    }

    private fun loadSentenceFromPreferences() {
        val sentencesRepository = SentencesRepository(getApplication())
        _practiceTitle.value = sharedPreferences.getString("chapterTitle", DEFAULT_CHAPTER_TITLE)
        _sentenceId.value = sharedPreferences.getInt("sentenceId", 1)
        _sentenceText.value = sharedPreferences.getString("sentence", DEFAULT_SENTENCE)

        viewModelScope.launch {
            val sentenceId = sharedPreferences.getInt("sentenceId", 1)
            val completionStatus = getSentenceStatus(sentenceId, sentencesRepository)
            saveToPreferences("completionStatus", completionStatus)
        }
    }

    fun setPracticeTitle(title: String) {
        _practiceTitle.value = title
    }

    fun setSentenceId(id: Int) {
        _sentenceId.value = id
    }

    fun setSentenceText(sentence: String) {
        _sentenceText.value = sentence
    }

    fun playAudio(sentence: String) {
        val url = "https://translate.google.com/translate_tts?ie=UTF-8&client=tw-ob&tl=en&q=$sentence"
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener { start() }
                setOnCompletionListener {
                    release()
                    mediaPlayer = null
                }
                setOnErrorListener { _, what, extra ->
                    showToast("Error playing audio: $what, $extra")
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            showToast("Error initializing audio: ${e.message}")
        }
    }

    private fun updateSentenceFromPreferences(sentence: Sentence, isNext: Boolean) {
        saveToPreferences("sentenceId", sentence.sentence_id)
        saveToPreferences("sentence", sentence.sentence)

        var nextChapterTitle = sharedPreferences.getString("chapterTitle", DEFAULT_CHAPTER_TITLE) ?: "Chapter 1"
        val currentChapterDesc = sharedPreferences.getString("chapterDesc", DEFAULT_CHAPTER_DESC)
        if (sentence.chapter != currentChapterDesc) {
            val currentChapterTitle = sharedPreferences.getString("chapterTitle", DEFAULT_CHAPTER_TITLE)
            if (isNext) {
                val chapterNumber = currentChapterTitle?.removePrefix("Chapter ")?.toIntOrNull() ?: 1
                nextChapterTitle = "Chapter ${chapterNumber + 1}"
            }
            else {
                val chapterNumber = currentChapterTitle?.removePrefix("Chapter ")?.toIntOrNull() ?: 1
                nextChapterTitle = "Chapter ${chapterNumber - 1}"
            }
        }

        saveToPreferences("chapterTitle", nextChapterTitle)
        saveToPreferences("chapterDesc", sentence.chapter)
        saveToPreferences("completionStatus", sentence.completed)

        updateSentence(sentence.sentence_id, nextChapterTitle, sentence.sentence)
    }

    private fun calculateChapterNumber(currentChapterTitle: String?, isNext: Boolean): Int {
        val currentNumber = currentChapterTitle?.removePrefix("Chapter ")?.toIntOrNull() ?: 1
        return if (isNext) currentNumber + 1 else maxOf(currentNumber - 1, 1)
    }

    private fun updateSentence(sentenceId: Int, chapterTitle: String, sentence: String) {
        _sentenceId.postValue(sentenceId)
        _practiceTitle.postValue(chapterTitle)
        _sentenceText.postValue(sentence)
    }

    fun getSentenceById(sentenceId: Int, isNext: Boolean, sentencesRepository: SentencesRepository) {
        viewModelScope.launch {
            try {
                val sentence = sentencesRepository.getSentenceById(sentenceId)
                if (sentence != null) {
                    _sentenceData.postValue(sentence)
                    updateSentenceFromPreferences(sentence, isNext)
                } else {
                    showToast("Sentence not found for ID: $sentenceId")
                }
            } catch (e: Exception) {
                showToast("Error loading sentences: ${e.message}")
            }
        }
    }

    fun transcribeAudio(
        sid: Int,
        chapter: String,
        uid: String,
        sentence: String,
        timestamp: String,
        audioFile: File
    ) {
        val api = RetrofitClient.instance
        val audioPart = MultipartBody.Part.createFormData(
            "audio",
            audioFile.name,
            audioFile.asRequestBody("audio/wave".toMediaTypeOrNull())
        )
        val requestBodyMap = mapOf(
            "chapter" to chapter.toRequestBody("text/plain".toMediaTypeOrNull()),
            "uid" to uid.toRequestBody("text/plain".toMediaTypeOrNull()),
            "sentence" to sentence.toRequestBody("text/plain".toMediaTypeOrNull()),
            "timestamp" to timestamp.toRequestBody("text/plain".toMediaTypeOrNull())
        )

        viewModelScope.launch {
            try {
                val response = api.postAudio(
                    sentenceId = sid,
                    chapter = requestBodyMap["chapter"]!!,
                    uid = requestBodyMap["uid"]!!,
                    sentence = requestBodyMap["sentence"]!!,
                    timestamp = requestBodyMap["timestamp"]!!,
                    audio = audioPart
                )
                if (response.isSuccessful) {
                    _transcribeResponse.postValue(response.body())
                } else {
                    _transcribeResponse.postValue(
                        TranscribeResponse(false, response.code(), response.message() ?: "Failed to transcribe", null)
                    )
                }
            } catch (e: HttpException) {
                _transcribeResponse.postValue(
                    TranscribeResponse(false, e.code(), "Server error: ${e.message}", null)
                )
            } catch (e: Exception) {
                _transcribeResponse.postValue(
                    TranscribeResponse(false, 500, "Error: ${e.message}", null)
                )
            }
        }
    }

    fun postScore(userId: String, type: String, score: Int) {
        val request = ChangeScoreRequest(score)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.changeScore(userId, type, request)
                if (response.isSuccessful) {
                    _changeScoreResponse.postValue(response.body())
                } else {
                    _changeScoreResponse.postValue(ChangeScoreResponse(false, response.code(), response.message()))
                }
            } catch (e: HttpException) {
                _changeScoreResponse.postValue(
                    ChangeScoreResponse(false, e.code(), e.message())
                )
            } catch (e: Exception) {
                _changeScoreResponse.postValue(
                    ChangeScoreResponse(false, 500, "Error: ${e.message}")
                )
            }
        }
    }

    fun postLog(logsRepository: LogsRepository, log: Log) {
        viewModelScope.launch {
            try {
                logsRepository.insertLog(log)
            } catch (e: Exception) {
                showToast("Error posting log: ${e.message}")
            }
        }
    }

    private suspend fun getSentenceStatus(sentenceId: Int, sentencesRepository: SentencesRepository): Int {
        return try {
            sentencesRepository.getSentenceById(sentenceId)?.completed ?: 0
        } catch (e: Exception) {
            showToast("Error getting sentence status: ${e.message}")
            0
        }
    }

    suspend fun changeSentenceStatus(sentenceId: Int, sentencesRepository: SentencesRepository) {
        try {
            sentencesRepository.updateCompletionStatus(sentenceId, 1)
            saveToPreferences("completionStatus", 1)
        } catch (e: Exception) {
            showToast("Error changing sentence status: ${e.message}")
        }
    }

    suspend fun getCompletedSentences(sentenceType: String, sentencesRepository: SentencesRepository): Int {
        return try {
            sentencesRepository.getSumCompleted(sentenceType) ?: 0
        } catch (e: Exception) {
            showToast("Error getting completed sentences: ${e.message}")
            0
        }
    }

    private fun saveToPreferences(key: String, value: Any) {
        val editor = sharedPreferences.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            else -> throw IllegalArgumentException("Unsupported preference type")
        }
        editor.apply()
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }
}
