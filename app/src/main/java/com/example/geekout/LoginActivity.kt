package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

// reference: Lab7 - Firebase
// Login using Firebase Authentication

class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var userEmail: EditText
    private lateinit var userPassword: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        userEmail = findViewById(R.id.email)
        userPassword = findViewById(R.id.password)
        loginButton = findViewById(R.id.login)

        loginButton.setOnClickListener { loginUserAccount() }

    }

    private fun loginUserAccount() {
        val email: String = userEmail.text.toString()
        val password: String = userPassword.text.toString()

        // Makes sure email input is not empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Please enter an email!", Toast.LENGTH_LONG).show()
            return
        }
        // Makes sure password input is not empty
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter a password!", Toast.LENGTH_LONG).show()
            return
        }

        // Checks Firebase. If account exists and is successfully accessed then start main activity intent
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Login successful!", Toast.LENGTH_LONG)
                        .show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Login has failed! Please try again!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    companion object {
        private const val TAG = "GeekOut:LoginActivity"
    }
}