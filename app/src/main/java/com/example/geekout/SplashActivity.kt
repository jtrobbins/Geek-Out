package com.example.geekout

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashLayout = findViewById<ConstraintLayout>(R.id.splashLayout)
        splashLayout.setOnClickListener {
            val loginRegisterIntent = Intent(this, LoginRegistrationActivity::class.java)
            startActivity(loginRegisterIntent)
        }

    }

    companion object {
        private const val TAG = "GeekOut:SplashActivity"
    }
}