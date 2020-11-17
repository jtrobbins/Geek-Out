package com.example.geekout

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserSettingsActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var databaseUsers: DatabaseReference
    private lateinit var usernameTextView: EditText
    private lateinit var passwordTextView: EditText
    private lateinit var saveButton: Button
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_settings)

        mAuth = FirebaseAuth.getInstance()
        databaseUsers = FirebaseDatabase.getInstance().getReference("users")

        usernameTextView = findViewById(R.id.username)
        passwordTextView = findViewById(R.id.password)
        saveButton = findViewById(R.id.save)

        uid = FirebaseAuth.getInstance().currentUser!!.uid

        saveButton.setOnClickListener { editProfile() }
    }

    private fun editProfile() {

        val username: String = usernameTextView.text.toString()
        val password: String = passwordTextView.text.toString()

        if (TextUtils.isEmpty(username) && TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter a username or password...", Toast.LENGTH_LONG).show()
            return
        }
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            changeUsername(username)
            changePassword(password)
            Toast.makeText(applicationContext, "Username and password successfully changed!", Toast.LENGTH_LONG).show()
        }
        if (!TextUtils.isEmpty(username)) {
            changeUsername(username)
            Toast.makeText(applicationContext, "Username successfully changed!", Toast.LENGTH_LONG).show()
        }
        if (!TextUtils.isEmpty(password)) {
            changePassword(password)
            Toast.makeText(applicationContext, "Password successfully changed!", Toast.LENGTH_LONG).show()
        }

    }

    private fun changeUsername(username: String) {
        val user = Player(username)
        databaseUsers.child(uid).setValue(user)
    }

    private fun changePassword(password: String) {
        val user = FirebaseAuth.getInstance().currentUser!!

        user.updatePassword(password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("Update Success")
            } else {
                println("Error Update")
            }
        }
    }

    companion object {
        private const val TAG = "GeekOut:UserSettingsActivity"
    }
}