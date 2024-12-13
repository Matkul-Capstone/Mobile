package com.example.speaktoo.ui.activity.newusername

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speaktoo.api.config.RetrofitClient
import com.example.speaktoo.api.model.user.ChangeUsernameRequest
import com.example.speaktoo.api.model.user.ChangeUsernameResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class NewUsernameViewModel() : ViewModel() {

    private val _changeUsernameResponse = MutableLiveData<ChangeUsernameResponse>()
    val changeUsernameResponse: LiveData<ChangeUsernameResponse> get() = _changeUsernameResponse

    fun changeUsername(userId: String, newUsername: String) {
        val request = ChangeUsernameRequest(newUsername)

        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = RetrofitClient.instance.changeUsername(userId, request)
                if (response.isSuccessful) {
                    _changeUsernameResponse.postValue(response.body())
                } else {
                    _changeUsernameResponse.postValue(
                        ChangeUsernameResponse(false, response.code(), response.message() ?: "Change username failed", null)
                    )
                }
            } catch (e: HttpException) {
                _changeUsernameResponse.postValue(
                    ChangeUsernameResponse(false, e.code(), e.message ?: "Server error.", null)
                )
            } catch (e: Exception) {
                _changeUsernameResponse.postValue(
                    ChangeUsernameResponse(false, 500, "Error: ${e.message}", null)
                )
            }
        }
    }
}