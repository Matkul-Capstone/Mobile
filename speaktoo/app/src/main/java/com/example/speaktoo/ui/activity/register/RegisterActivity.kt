package com.example.speaktoo.ui.activity.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.speaktoo.api.model.user.RegisterRequest
import com.example.speaktoo.api.model.user.RegisterResponse
import com.example.speaktoo.ui.activity.successful.RegisterSuccessfulActivity
import com.example.speaktoo.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe register response
        registerViewModel.registerResponse.observe(this) { response ->
            handleRegisterResponse(response)
        }

        binding.registerButton.setOnClickListener {
            val username = binding.username.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()

            if (validateInputs(username, email, password, confirmPassword)) {
                val registerRequest = RegisterRequest(email, password, username)
                registerViewModel.registerUser(registerRequest, this)
                binding.loading.visibility = android.view.View.VISIBLE
            }
        }

    }

    private fun validateInputs(username: String, email: String, password: String, confirmPassword: String): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun toRegisterSuccess() {
        val intent = Intent(this, RegisterSuccessfulActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleRegisterResponse(response: RegisterResponse) {
        binding.loading.visibility = android.view.View.GONE
        if (response.success) {
            toRegisterSuccess()
        }
    }
}