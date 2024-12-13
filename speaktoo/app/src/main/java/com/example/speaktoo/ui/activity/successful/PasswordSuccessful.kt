package com.example.speaktoo.ui.activity.successful

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.speaktoo.MainActivity
import com.example.speaktoo.databinding.ActivityPasswordSuccessfulBinding
import com.example.speaktoo.ui.activity.login.LoginActivity

class PasswordSuccessful : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordSuccessfulBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPasswordSuccessfulBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toLoginButton = binding.toLoginButton
        toLoginButton.setOnClickListener {
            toLogin()
        }
    }

    private fun toLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}