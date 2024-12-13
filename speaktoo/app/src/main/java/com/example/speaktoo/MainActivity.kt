package com.example.speaktoo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.speaktoo.databinding.ActivityMainBinding
import com.example.speaktoo.ui.activity.login.LoginActivity
import com.example.speaktoo.ui.activity.register.RegisterActivity

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginButton = binding.loginButton
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val registerButton = binding.registerButton
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}