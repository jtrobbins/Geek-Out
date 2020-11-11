package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginRegistrationActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_registration)

        loginButton = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        registerButton = findViewById(R.id.registerButton)
        registerButton.setOnClickListener {
            val registerIntent = Intent(this, RegistrationActivity::class.java)
            startActivity(registerIntent)
        }
    }

    companion object {
        private const val TAG = "GeekOut:LoginRegisterActivity"
    }
}