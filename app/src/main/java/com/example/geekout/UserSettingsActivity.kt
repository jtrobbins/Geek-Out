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

// Allows for a user to change their username or password

class UserSettingsActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var databaseUsers: DatabaseReference
    private lateinit var usernameTextView: EditText
    private lateinit var passwordTextView: EditText
    private lateinit var saveButton: Button
    private lateinit var uid: String
    private var epValidator = EmailPasswordValidator()

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

        // If both fields are empty then do nothing and display toast failure
        // Validates password
        if (TextUtils.isEmpty(username) && !epValidator.validPassword(password)) {
            Toast.makeText(applicationContext, "Please enter a username or password!", Toast.LENGTH_LONG).show()
            return
        }
        // If both fields are present then change both username and password and display toast success
        // Validates password
        if (!TextUtils.isEmpty(username) && !epValidator.validPassword(password)) {
            changeUsername(username)
            changePassword(password)
            Toast.makeText(applicationContext, "Username and password successfully changed!", Toast.LENGTH_LONG).show()
        }
        // If password field is empty then change username and display toast success
        if (!TextUtils.isEmpty(username)) {
            changeUsername(username)
            Toast.makeText(applicationContext, "Username successfully changed!", Toast.LENGTH_LONG).show()
        }
        // If username field is empty then change password and display toast success
        // Validates password
        if (!epValidator.validPassword(password)) {
            changePassword(password)
            Toast.makeText(applicationContext, "Password successfully changed!", Toast.LENGTH_LONG).show()
        }

    }

    // Access Firebase to change username
    private fun changeUsername(username: String) {
        val user = Player(username)
        databaseUsers.child(uid).setValue(user)
    }

    // Access Firebase to change password
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