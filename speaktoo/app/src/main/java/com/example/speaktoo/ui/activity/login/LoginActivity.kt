package com.example.speaktoo.ui.activity.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.speaktoo.api.model.logs.Logs
import com.example.speaktoo.api.model.user.LoginRequest
import com.example.speaktoo.api.model.user.LoginResponse
import com.example.speaktoo.data.models.Log
import com.example.speaktoo.data.repository.LogsRepository
import com.example.speaktoo.ui.activity.newpassword.ForgotPasswordActivity
import com.example.speaktoo.databinding.ActivityLoginBinding
import com.example.speaktoo.ui.activity.nav.NavActivity
import com.example.speaktoo.utils.UserPreferences
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val logsRepository = LogsRepository(application)

        val forgotPassword = binding.forgotPassword
        forgotPassword.setOnClickListener {
            toForgotPassword()
        }

        val loginButton = binding.loginButton
        loginButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            if (validateInputs(email, password)) {
                val loginRequest = LoginRequest(email, password)
                loginViewModel.loginUser(loginRequest, this)
                binding.loading.visibility = android.view.View.VISIBLE
            }
        }

        loginViewModel.loginResponse.observe(this) { response ->
            handleLoginResponse(response, logsRepository)
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun toNavActivity() {
        val intent = Intent(this, NavActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun toForgotPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleLoginResponse(response: LoginResponse, logsRepository: LogsRepository) {
        val password = binding.password.text.toString()
        binding.loading.visibility = android.view.View.GONE

        if (response != null && response.success) {
            val user = response.data
            if (user != null) {
                val userPreferences = UserPreferences(this)
                userPreferences.saveUser(user)
                userPreferences.saveUserPassword(password)

                lifecycleScope.launch {
                    loginViewModel.storeUserLogs(user.logs, logsRepository)
                }

                toNavActivity()
            }
        }
    }
}