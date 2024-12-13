package com.example.speaktoo.ui.activity.newusername

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.speaktoo.api.model.user.ChangeUsernameResponse
import com.example.speaktoo.databinding.ActivityNewUsernameBinding
import com.example.speaktoo.ui.activity.nav.NavActivity
import com.example.speaktoo.ui.fragments.profile.ProfileViewModel
import com.example.speaktoo.utils.UserPreferences

class NewUsernameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewUsernameBinding
    private val newUsernameViewModel: NewUsernameViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewUsernameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPreferences = UserPreferences(this)
        val user = userPreferences.getUser()
        val userId = user?.user_id ?: "default uid"

        setupObserver(userPreferences)
        setupButtonListener(userId)
    }

    private fun setupObserver(userPreferences: UserPreferences) {
        newUsernameViewModel.changeUsernameResponse.observe(this) { response ->
            handleUsernameResponse(response, userPreferences)
        }
    }

    private fun setupButtonListener(userId: String) {
        binding.newUsernameButton.setOnClickListener {
            val username = binding.username.text.toString()
            updateUsername(userId, username)
        }
    }

    private fun updateUsername(userId: String, newUsername: String) {
        showLoading(true)
        newUsernameViewModel.changeUsername(userId, newUsername)
    }

    private fun handleUsernameResponse(response: ChangeUsernameResponse, userPreferences: UserPreferences) {
        if (response.success) {
            val user = userPreferences.getUser()
            if (user != null) {
                user.username = response.data?.newUsername ?: user.username
                userPreferences.saveUser(user)
                showLoading(false)

                finish()
            }

        } else {
            showLoading(false)
            Toast.makeText(this, response.message ?: "Change username failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}