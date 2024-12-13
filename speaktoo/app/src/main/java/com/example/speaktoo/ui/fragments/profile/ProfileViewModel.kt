package com.example.speaktoo.ui.fragments.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.speaktoo.api.model.user.UserData
import com.example.speaktoo.utils.UserPreferences

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userPreferences = UserPreferences(application)

    private val _user = MutableLiveData<UserData?>()
    val user: LiveData<UserData?> = _user

    init {
        _user.value = userPreferences.getUser()  // Initialize the user data
    }

    fun updateUser(user: UserData) {
        _user.postValue(user)
    }
}