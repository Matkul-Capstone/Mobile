package com.example.speaktoo.ui.fragments.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.speaktoo.data.level.Level
import com.example.speaktoo.R
import com.example.speaktoo.api.config.RetrofitClient
import com.example.speaktoo.api.model.user.ChangeUserTypeRequest
import com.example.speaktoo.api.model.user.ChangeUserTypeResponse
import com.example.speaktoo.api.route.SpeaktooAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val api: SpeaktooAPI = RetrofitClient.instance

    private val _levels = MutableLiveData<List<Level>>()
    val levels: LiveData<List<Level>> get() = _levels

    private val _selectedLevel = MutableLiveData<String>()
    val selectedLevel: LiveData<String> get() = _selectedLevel

    private val _changeUserTypeResponse = MutableLiveData<ChangeUserTypeResponse>()
    val changeUserTypeResponse: LiveData<ChangeUserTypeResponse> get() = _changeUserTypeResponse

    fun loadLevels() {
        val levelList = listOf(
            Level("Beginner", R.drawable.image_comment_1),
            Level("Intermediate", R.drawable.image_translate_1),
            Level("Advanced", R.drawable.image_translate_2)
        )
        _levels.value = levelList
    }

    fun selectLevel(levelName: String, context: Context) {
        _selectedLevel.value = levelName
        val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("level_name", levelName)
        editor.apply()
    }

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