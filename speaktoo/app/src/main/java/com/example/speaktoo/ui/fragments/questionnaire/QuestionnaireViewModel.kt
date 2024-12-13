package com.example.speaktoo.ui.fragments.questionnaire

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.speaktoo.api.config.RetrofitClient
import com.example.speaktoo.api.model.user.ChangeUserTypeRequest
import com.example.speaktoo.api.model.user.ChangeUserTypeResponse
import com.example.speaktoo.api.route.SpeaktooAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class QuestionnaireViewModel(application: Application) : AndroidViewModel(application) {

    private val api: SpeaktooAPI = RetrofitClient.instance

    private val _changeUserTypeResponse = MutableLiveData<ChangeUserTypeResponse>()
    val changeUserTypeResponse: LiveData<ChangeUserTypeResponse> get() = _changeUserTypeResponse

    fun changeUserType(userId: String, userType: String) {
        val request = ChangeUserTypeRequest(userType)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.changeUserType(userId, request)
                if (response.isSuccessful) {
                    _changeUserTypeResponse.postValue(response.body())
                } else {
                    _changeUserTypeResponse.postValue(
                        ChangeUserTypeResponse(false, response.code(), response.message() ?: "Change userType failed.", null)
                    )
                }
            } catch (e: HttpException) {
                _changeUserTypeResponse.postValue(
                    ChangeUserTypeResponse(false, e.code(), e.message ?: "Server error.", null)
                )
            } catch (e: Exception) {
                _changeUserTypeResponse.postValue(
                    ChangeUserTypeResponse(false, 500, "Error: ${e.message}", null)
                )
            }
        }
    }
}