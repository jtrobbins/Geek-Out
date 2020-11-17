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

class RegistrationActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var databaseUsers: DatabaseReference
    private lateinit var uid: String
    private lateinit var emailTV: EditText
    private lateinit var usernameTV: EditText
    private lateinit var passwordTV: EditText
    private lateinit var regBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        mAuth = FirebaseAuth.getInstance()
        databaseUsers = FirebaseDatabase.getInstance().getReference("users")

        emailTV = findViewById(R.id.email)
        usernameTV = findViewById(R.id.username)
        passwordTV = findViewById(R.id.password)
        regBtn = findViewById(R.id.register)

        regBtn.setOnClickListener { registerNewUser() }
    }

    private fun registerNewUser() {

        val email: String = emailTV.text.toString()
        val username: String = usernameTV.text.toString()
        val password: String = passwordTV.text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(applicationContext, "Please enter username...", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter password!", Toast.LENGTH_LONG).show()
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