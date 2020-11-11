package com.example.geekout

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class StartGameActivity: AppCompatActivity() {

    private lateinit var databaseGames: DatabaseReference
    private lateinit var codeTextView: TextView
    private lateinit var readyButton: Button
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_game)

        codeTextView = findViewById(R.id.code)
        readyButton = findViewById(R.id.readyButton)
        databaseGames = FirebaseDatabase.getInstance().getReference("games")

        code = intent.getStringExtra("code").toString()

        codeTextView.text = code

        readyButton.setOnClickListener {
            databaseGames.child(code).child("in_progress").setValue(1)
        }
    }

    companion object {
        private const val TAG = "GeekOut:ReadyActivity"
    }
}