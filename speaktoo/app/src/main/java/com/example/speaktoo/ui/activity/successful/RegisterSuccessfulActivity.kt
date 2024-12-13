package com.example.speaktoo.ui.activity.successful

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.speaktoo.databinding.ActivityRegisterSuccessfulBinding
import com.example.speaktoo.ui.activity.login.LoginActivity

class RegisterSuccessfulActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterSuccessfulBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterSuccessfulBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toLoginButton = binding.toLoginButton
        toLoginButton.setOnClickListener {
            toLogin()
        }
    }

    private fun toLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}