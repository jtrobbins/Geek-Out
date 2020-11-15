package com.example.geekout

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.lang.Exception


class StartGameActivity: AppCompatActivity() {

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var codeTextView: TextView
    private lateinit var playersListView: ListView
    private lateinit var readyButton: Button
    internal lateinit var players: MutableList<User>
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_game)

        codeTextView = findViewById(R.id.code)
        playersListView = findViewById(R.id.players)
        readyButton = findViewById(R.id.readyButton)
        databaseGames = FirebaseDatabase.getInstance().getReference("games")

        code = intent.getStringExtra("code").toString()
        codeTextView.text = code
        databaseCurrentGame = databaseGames.child(code)

        readyButton.setOnClickListener {
            databaseCurrentGame.child("in_progress").setValue(1)
        }
    }

    companion object {
        private const val TAG = "GeekOut:ReadyActivity"
    }
}