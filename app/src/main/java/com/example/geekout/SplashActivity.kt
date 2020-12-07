package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

// Splash Activity for the project that presents the Geek-Out logo

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashButton = findViewById<ImageButton>(R.id.logo)
        splashButton.setOnClickListener {
            val loginRegisterIntent = Intent(this, LoginRegistrationActivity::class.java)
            startActivity(loginRegisterIntent)
        }

    }

    companion object {
        private const val TAG = "GeekOut:SplashActivity"
    }
}