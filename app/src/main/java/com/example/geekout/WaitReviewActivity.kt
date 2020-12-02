package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class WaitReviewActivity : AppCompatActivity() {

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var scoreboardButton: Button
    private lateinit var userAnswers: Array<String>
    private lateinit var code: String
    private lateinit var uid: String
    private var highestBid: Long = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait_review)

        databaseGames = FirebaseDatabase.getInstance().getReference("games")

        scoreboardButton = findViewById<Button>(R.id.scoreboardButton)

        code = intent.getStringExtra("code").toString()
        highestBid = intent.getLongExtra("highest_bid", 1)
        uid = intent.getStringExtra("bidder_uid").toString()
        userAnswers = intent.getStringArrayExtra("userAnswers")!! as Array<String>
        databaseCurrentGame = databaseGames.child(code)

        scoreboardButton.setOnClickListener {
            val scoreboardIntent = Intent(this@WaitReviewActivity, ScoreboardActivity::class.java)
            scoreboardIntent.putExtra("code", code)
            startActivity(scoreboardIntent)
        }
    }

    override fun onStart() {
        super.onStart()

        databaseCurrentGame.child("round_num")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val roundNum = p0.value as Long
                    databaseCurrentGame.child("round_$roundNum").child("answers_reviewed")
                        .addValueEventListener(object :ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {

                                if(dataSnapshot.value == true) {
                                    val intent = Intent(this@WaitReviewActivity, DeterminePoints::class.java)
                                    intent.putExtra("code", code)
                                    Log.i(TAG, "HIGHEST BID WRA: $highestBid")
                                    intent.putExtra("highestBid", highestBid)
                                    intent.putExtra("bidder_uid", uid)
                                    intent.putExtra("userAnswers", userAnswers)
                                    startActivity(intent)
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {

                            }
                        })
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }
        })



    }
    companion object {
        private const val TAG = "GeekOut:WaitReviewActivity"
    }
}