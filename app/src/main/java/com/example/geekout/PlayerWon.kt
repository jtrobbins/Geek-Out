package com.example.geekout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PlayerWon : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_won)
    }

    companion object {
        private const val TAG = "GeekOut:NotImplementedActivity"
    }
}