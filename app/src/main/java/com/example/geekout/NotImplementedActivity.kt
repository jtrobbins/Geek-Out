package com.example.geekout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NotImplementedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_implemented)
    }

    companion object {
        private const val TAG = "GeekOut:NotImplementedActivity"
    }
}