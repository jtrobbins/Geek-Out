package com.example.geekout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ReviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
    }

    companion object {
        private const val TAG = "GeekOut:ReviewActivity"
    }
}