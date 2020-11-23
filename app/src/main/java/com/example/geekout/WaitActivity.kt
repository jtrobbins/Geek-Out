package com.example.geekout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class WaitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait)
    }

    companion object {
        private const val TAG = "GeekOut:WaitActivity"
    }
}