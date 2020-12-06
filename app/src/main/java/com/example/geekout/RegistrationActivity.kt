package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// reference: Lab7 - Firebase

class RegistrationActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var databaseUsers: DatabaseReference
    private lateinit var uid: String
    private lateinit var emailTextView: EditText
    private lateinit var usernameTextView: EditText
    private lateinit var passwordTextView: EditText
    private lateinit var registerButton: Button
    private var epValidator = EmailPasswordValidator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        mAuth = FirebaseAuth.getInstance()
        databaseUsers = FirebaseDatabase.getInstance().getReference("users")

        emailTextView = findViewById(R.id.email)
        usernameTextView = findViewById(R.id.username)
        passwordTextView = findViewById(R.id.password)
        registerButton = findViewById(R.id.register)

        registerButton.setOnClickListener { registerNewUser() }
    }

    private fun registerNewUser() {

        val email: String = emailTextView.text.toString()
        val username: String = usernameTextView.text.toString()
        val password: String = passwordTextView.text.toString()

        if (!epValidator.validEmail(email)) {
            Toast.makeText(applicationContext, "Please enter an email!", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(applicationContext, "Please enter a username!", Toast.LENGTH_LONG).show()
            return
        }
        if (!epValidator.validPassword(password)) {
            Toast.makeText(applicationContext, "Please enter a password!", Toast.LENGTH_LONG).show()
            return
        }

        val x = mAuth!!.createUserWithEmailAndPassword(email, password)

        x.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                addUser(username)
                Toast.makeText(applicationContext, getString(R.string.register_success), Toast.LENGTH_LONG).show()
                startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
            } else {
                Toast.makeText(applicationContext, getString(R.string.register_failed), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addUser(username: String) {
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        val user = Player(username)
        databaseUsers.child(uid).setValue(user)
    }

    companion object {
        private const val TAG = "GeekOut:RegistrationActivity"
    }
}