package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.concurrent.schedule

// Displays who won the bid

class HighestBidderActivity : AppCompatActivity() {

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var highestBidderTextView: TextView
    private var highestBid: Long = 1
    private lateinit var code: String
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highest_bidder)

        databaseGames = FirebaseDatabase.getInstance().getReference("games")

        highestBidderTextView = findViewById(R.id.highestBidderText)

        code = intent.getStringExtra("code").toString()
        highestBid = intent.getLongExtra("highest_bid", 1)
        databaseCurrentGame = databaseGames.child(code)

        uid = FirebaseAuth.getInstance().currentUser!!.uid

        // updates interface to display who won the round from Firebase
        databaseCurrentGame.child("round_num").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val roundNum = dataSnapshot.value.toString()
                databaseCurrentGame.child("round_$roundNum").child("highest_bidder").addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val highestBidder = dataSnapshot.getValue(Int::class.java) as Int
                        databaseCurrentGame.child("players").addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                var user: Player? = null
                                for (postSnapshot in dataSnapshot.children) {
                                    try {
                                        user = postSnapshot.getValue(Player::class.java)
                                        if (user!!.player_num == highestBidder) {
                                            val username = user.username
                                            highestBidderTextView.text = username
                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, e.toString())
                                    }
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                // do nothing
                            }
                        })
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        // do nothing
                    }
                })
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // do nothing
            }
        })
    }

    override fun onStart() {
        super.onStart()

        // After 3 seconds start the next activity
        // highest bidder moves on to answer the question
        // everyone else moves on to waiting
        Timer().schedule(3000) {
            databaseCurrentGame.child("round_num").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val roundNum = dataSnapshot.value.toString()
                    databaseCurrentGame.child("round_$roundNum").child("highest_bidder").addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val highestBidder = dataSnapshot.getValue(Int::class.java) as Int
                            databaseCurrentGame.child("players").child(uid).child("player_num").addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val playerNum = dataSnapshot.getValue(Int::class.java) as Int
                                    if (playerNum == highestBidder) {
                                        val answerIntent = Intent(this@HighestBidderActivity, AnswerActivity::class.java)
                                        answerIntent.putExtra("code", code)
                                        answerIntent.putExtra("highest_bid", highestBid)
                                        startActivity(answerIntent)
                                    } else {
                                        val waitIntent = Intent(this@HighestBidderActivity, WaitAnswerActivity::class.java)
                                        waitIntent.putExtra("code", code)
                                        waitIntent.putExtra("highest_bid", highestBid)
                                        //waitIntent.putExtra("bidder_uid", uid)
                                        startActivity(waitIntent)
                                    }
                                }
                                override fun onCancelled(databaseError: DatabaseError) {
                                    // do nothing
                                }
                            })
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            // do nothing
                        }
                    })
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // do nothing
                }
            })
        }
    }

    override fun onBackPressed() {
        // do nothing
    }

    companion object {
        private const val TAG = "GeekOut:HighestBidderActivity"
    }
}