package com.example.speaktoo.ui.activity.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speaktoo.api.config.RetrofitClient
import com.example.speaktoo.api.model.logs.Logs
import com.example.speaktoo.api.model.user.LoginRequest
import com.example.speaktoo.api.model.user.LoginResponse
import com.example.speaktoo.data.models.Log
import com.example.speaktoo.data.repository.LogsRepository
import com.example.speaktoo.utils.ErrorHandlingUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel() : ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> get() = _loginResponse

    fun loginUser(loginRequest: LoginRequest, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.loginUser(loginRequest)
                if (response.isSuccessful) {
                    _loginResponse.postValue(response.body())
                }
                else {
                    val errorMessage = ErrorHandlingUtil.extractErrorMessage(response)
                    _loginResponse.postValue(
                        LoginResponse(false, response.code(), response.message() ?: "Login failed.", null)
                    )
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            context, // Replace this with the appropriate context
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: HttpException) {
                _loginResponse.postValue(
                    LoginResponse(false, e.code(), e.message ?: "Server error.", null)
                )
            } catch (e: Exception) {
                _loginResponse.postValue(
                    LoginResponse(false, 500, "Error: ${e.message}", null)
                )
            }
        }

    }

    suspend fun storeUserLogs(apiLogs: List<Logs>?, logsRepository: LogsRepository) {
        if (apiLogs != null) {
            val dbLogs = apiLogs.map { apiLog ->
                Log(
                    timestamp = apiLog.timestamp,
                    user_id = apiLog.user_id,
                    sentence_id = apiLog.sentence_id,
                    score = apiLog.score,
                    completed = apiLog.completed,
                    chapter = apiLog.chapter
                )
            }

            logsRepository.insertLogs(dbLogs)
        }
    }
}