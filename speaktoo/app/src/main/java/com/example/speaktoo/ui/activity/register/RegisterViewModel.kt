package com.example.speaktoo.ui.activity.register

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speaktoo.api.config.RetrofitClient
import com.example.speaktoo.api.model.user.RegisterRequest
import com.example.speaktoo.api.model.user.RegisterResponse
import com.example.speaktoo.utils.ErrorHandlingUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel() : ViewModel() {

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> get() = _registerResponse

    fun registerUser(registerRequest: RegisterRequest, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.registerUser(registerRequest)
                if (response.isSuccessful) {
                    _registerResponse.postValue(response.body())
                } else {
                    val errorMessage = ErrorHandlingUtil.extractErrorMessage(response)
                    _registerResponse.postValue(
                        RegisterResponse(false, response.code(), response.message() ?: "Registration failed", null)
                    )
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            context, // Replace this with the appropriate context
                            getErrorMessageForCode(errorMessage),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: HttpException) {
                _registerResponse.postValue(
                    RegisterResponse(false, e.code(), e.message ?: "Server error.", null)
                )
            } catch (e: Exception) {
                _registerResponse.postValue(
                    RegisterResponse(false, 500, "Error: ${e.message}", null)
                )
            }
        }
    }

    private fun getErrorMessageForCode(errorCode: String): String {
        return when (errorCode) {
            "auth/invalid-password" -> "Invalid password. Please try again."
            "auth/invalid-credential" -> "Invalid credentials. Please check your input."
            "auth/invalid-email" -> "Invalid email address. Please provide a valid email."
            "auth/email-already-in-use" -> "This email is already in use. Please try a different one."
            else -> "An unexpected error occurred. Please try again later."
        }
    }
}