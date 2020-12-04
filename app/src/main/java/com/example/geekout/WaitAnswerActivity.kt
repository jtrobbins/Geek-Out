package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class WaitAnswerActivity : AppCompatActivity() {

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var scoreboardButton: Button
    private lateinit var code: String
    private lateinit var uid: String
    //private var roundNum :Int = 1
    private var highestBid: Long = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait_answer)

        databaseGames = FirebaseDatabase.getInstance().getReference("games")

        scoreboardButton = findViewById<Button>(R.id.scoreboardButton)

        code = intent.getStringExtra("code").toString()
        //roundNum = intent.getIntExtra("roundNum",1)
        uid = intent.getStringExtra("bidder_uid").toString()
        highestBid = intent.getLongExtra("highest_bid", 1)

        databaseCurrentGame = databaseGames.child(code)

        scoreboardButton.setOnClickListener {
            val scoreboardIntent = Intent(this@WaitAnswerActivity, ScoreboardActivity::class.java)
            scoreboardIntent.putExtra("code", code)
            startActivity(scoreboardIntent)
        }
    }

    override fun onStart() {
        super.onStart()

        databaseCurrentGame.child("round_num")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val roundNum = dataSnapshot.value as Long
                    databaseCurrentGame.child("round_$roundNum").child("answers_ready")
                        .addValueEventListener(object :ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if(dataSnapshot.value == true) {
                                    val intent = Intent(this@WaitAnswerActivity, ReviewAnswersActivity::class.java)
                                    intent.putExtra("code", code)
                                    intent.putExtra("highest_bid", highestBid)
                                    //intent.putExtra("bidder_uid", uid)
                                    startActivity(intent)
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {

                            }
                        })
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })



    }

    companion object {
        private const val TAG = "GeekOut:WaitAnswerActivity"
    }
}