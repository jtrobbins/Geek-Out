package com.example.geekout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AnswerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)
    }

    companion object {
        private const val TAG = "GeekOut:AnswerActivity"
    }
}