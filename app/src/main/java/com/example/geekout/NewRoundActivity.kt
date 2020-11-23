package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.*
import kotlin.concurrent.schedule

class NewRoundActivity : AppCompatActivity() {

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var roundTextView: TextView
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_round)

        databaseGames = FirebaseDatabase.getInstance().getReference("games")

        roundTextView = findViewById(R.id.round)

        code = intent.getStringExtra("code").toString()
        databaseCurrentGame = databaseGames.child(code)

        databaseCurrentGame.child("num_pass").setValue(0)

        databaseCurrentGame.child("round_num").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val roundNum = dataSnapshot.value.toString()
                val roundStr = "Round $roundNum"
                roundTextView.text = roundStr
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // do nothing
            }
        })
    }

    override fun onStart() {
        super.onStart()
        Timer().schedule(3000) {
            val bidIntent = Intent(this@NewRoundActivity, BidActivity::class.java)
            bidIntent.putExtra("code", code)
            startActivity(bidIntent)
        }
    }

    companion object {
        private const val TAG = "GeekOut:NewRoundActivity"
    }
}