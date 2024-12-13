package com.example.speaktoo.ui.activity.newpassword

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.speaktoo.api.model.user.NewPasswordResponse
import com.example.speaktoo.ui.activity.successful.PasswordSuccessful
import com.example.speaktoo.databinding.ActivityResetPasswordBinding
import com.example.speaktoo.utils.UserPreferences

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private val newPasswordSharedViewModel: NewPasswordSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPreferences = UserPreferences(this)
        val user = userPreferences.getUser()
        val email = user?.user_email ?: "default email"
        val currentPassword = user?.password ?: "Default password"

        setupObserver()

        val resetPasswordButton = binding.resetPasswordButton
        resetPasswordButton.setOnClickListener {
            val password = binding.currentPassword.text.toString()
            if (validateInputs(password, currentPassword)) {
                resetPassword(email)
            }
        }
    }

    private fun setupObserver() {
        newPasswordSharedViewModel.newPasswordResponse.observe(this){ response ->
            handlePasswordResponse(response)
        }
    }

    private fun handlePasswordResponse(response: NewPasswordResponse) {
        showLoading(false)
        if (response.success) {
            showLoading(false)
            toPasswordSuccess()
        }
    }

    private fun resetPassword(email: String) {
        showLoading(true)
        newPasswordSharedViewModel.newPassword(email, this)
    }

    private fun validateInputs(password: String, currentPassword: String): Boolean {
        if (password != currentPassword) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun toPasswordSuccess() {
        val intent = Intent(this, PasswordSuccessful::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}