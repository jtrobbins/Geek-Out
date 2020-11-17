package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class BidActivity : AppCompatActivity() {

    private lateinit var scoreboardButton: Button
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bid)

        scoreboardButton = findViewById<Button>(R.id.scoreboardButton)

        code = intent.getStringExtra("code").toString()

        scoreboardButton.setOnClickListener {
            val scoreboardIntent = Intent(this, ScoreboardActivity::class.java)
            scoreboardIntent.putExtra("code", code)
            startActivity(scoreboardIntent)
        }
    }

    companion object {
        private const val TAG = "GeekOut:BidActivity"
    }
}