package com.example.speaktoo.ui.fragments.levelCompleted

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.speaktoo.api.config.RetrofitClient
import com.example.speaktoo.api.model.sentences.SentencesData
import com.example.speaktoo.api.model.sentences.SentencesResponse
import com.example.speaktoo.data.models.Sentence
import com.example.speaktoo.data.repository.SentencesRepository
import com.example.speaktoo.utils.ErrorHandlingUtil
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LevelCompletedViewModel(application: Application) : AndroidViewModel(application) {
    private val _levelName = MutableLiveData<String>("Beginner")
    val levelName: LiveData<String> get() = _levelName

    private val _sentencesResponse = MutableLiveData<SentencesResponse>()
    val sentencesResponse: LiveData<SentencesResponse> get() = _sentencesResponse

    fun setLevelName(level: String) {
        _levelName.value = level
    }

    fun getSentencesByType(type: String, uid: String) {
        val api = RetrofitClient.instance

        viewModelScope.launch {
            try {
                val response = api.getSentencesByType(type, uid)

                if (response.isSuccessful) {
                    _sentencesResponse.postValue(response.body())
                } else {
                    val errorMessage = ErrorHandlingUtil.extractErrorMessage(response)
                    SentencesResponse(false, response.code(), response.message(), null)
                    Toast.makeText(getApplication(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                _sentencesResponse.postValue(
                    SentencesResponse(false, e.code(), e.message ?: "Server error.", null)
                )
            }
            catch (e: Exception) {
                _sentencesResponse.postValue(
                    SentencesResponse(false, 500, "Error: ${e.message}", null)
                )
            }
        }
    }

    fun saveSentencesToLocal(apiSentences: List<SentencesData>, sentencesRepository: SentencesRepository) {
        viewModelScope.launch {
            val dbSentences = apiSentences.map { apiSentence ->
                Sentence(
                    sentence_id = apiSentence.sentence_id,
                    sentence_type = apiSentence.sentence_type,
                    chapter = apiSentence.chapter,
                    sentence = apiSentence.sentence,
                    completed = apiSentence.completed
                )
            }
            sentencesRepository.insertSentences(dbSentences)
        }
    }
}