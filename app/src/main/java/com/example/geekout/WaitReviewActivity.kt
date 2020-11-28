package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class WaitReviewActivity : AppCompatActivity() {

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var scoreboardButton: Button
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait_review)

        databaseGames = FirebaseDatabase.getInstance().getReference("games")

        scoreboardButton = findViewById<Button>(R.id.scoreboardButton)

        code = intent.getStringExtra("code").toString()
        databaseCurrentGame = databaseGames.child(code)

        scoreboardButton.setOnClickListener {
            val scoreboardIntent = Intent(this@WaitReviewActivity, ScoreboardActivity::class.java)
            scoreboardIntent.putExtra("code", code)
            startActivity(scoreboardIntent)
        }
    }

    companion object {
        private const val TAG = "GeekOut:WaitReviewActivity"
    }
}