package com.example.speaktoo.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.speaktoo.api.model.user.UserData
import com.google.gson.Gson

class UserPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _user = MutableLiveData<UserData?>()
    val user: LiveData<UserData?> get() = _user

    init {
        _user.value = getUser()
    }

    fun saveUser(user: UserData) {
        val userCopy = user.copy(logs = emptyList())
        val userJson = gson.toJson(userCopy)
        sharedPreferences.edit().putString("user_data", userJson).apply()
        _user.value = userCopy
    }

    fun saveUserPassword(password: String) {
        val user = getUser()
        if (user != null) {
            user.password = password
            saveUser(user)
        }
    }

    fun getUser(): UserData? {
        val userJson = sharedPreferences.getString("user_data", null)
        return if (userJson != null) {
            gson.fromJson(userJson, UserData::class.java)
        } else {
            null
        }
    }

    fun clearUser() {
        sharedPreferences.edit().remove("user_data").apply()
        _user.value = null
    }
}