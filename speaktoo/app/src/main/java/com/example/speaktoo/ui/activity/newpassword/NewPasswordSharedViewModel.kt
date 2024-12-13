package com.example.speaktoo.ui.activity.newpassword

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speaktoo.api.config.RetrofitClient
import com.example.speaktoo.api.model.user.NewPasswordRequest
import com.example.speaktoo.api.model.user.NewPasswordResponse
import com.example.speaktoo.utils.ErrorHandlingUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class NewPasswordSharedViewModel() : ViewModel() {
    private val _newPasswordResponse = MutableLiveData<NewPasswordResponse>()
    val newPasswordResponse: LiveData<NewPasswordResponse> get() = _newPasswordResponse

    fun newPassword(email: String, context: Context) {
        val request = NewPasswordRequest(email)

        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = RetrofitClient.instance.newPassword(request)
                if (response.isSuccessful) {
                    _newPasswordResponse.postValue(response.body())
                } else {
                    val errorMessage = ErrorHandlingUtil.extractErrorMessage(response)
                    _newPasswordResponse.postValue(
                        NewPasswordResponse(false, response.code(), response.message() ?: "Change password failed")
                    )
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            context, // Replace this with the appropriate context
                            "$errorMessage, make sure your Email address is valid",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: HttpException) {
                _newPasswordResponse.postValue(
                    NewPasswordResponse(false, e.code(), e.message ?: "Server error.")
                )
            } catch (e: Exception) {
                _newPasswordResponse.postValue(
                    NewPasswordResponse(false, 500, "Error: ${e.message}")
                )
            }
        }
    }
}