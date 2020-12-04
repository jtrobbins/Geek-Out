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
    private lateinit var databaseQuestions: DatabaseReference
    private lateinit var roundTextView: TextView
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_round)

        databaseGames = FirebaseDatabase.getInstance().getReference("games")

        roundTextView = findViewById(R.id.round)

        code = intent.getStringExtra("code").toString()
        databaseCurrentGame = databaseGames.child(code)
        databaseQuestions = FirebaseDatabase.getInstance().getReference("questions")

        databaseCurrentGame.child("round_num").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val roundNum = dataSnapshot.value.toString()

                databaseCurrentGame.child("players").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        for (postSnapshot in dataSnapshot.children) {
                            val user = postSnapshot.getValue(Player::class.java)
                            if (user!!.player_num == 1) {
                                val userUid = postSnapshot.ref.key
                                databaseCurrentGame.child("round_$roundNum").child("highest_bidder_uid").setValue(userUid)
                            }
                        }

                        databaseCurrentGame.child("round_$roundNum").child("num_pass").setValue(0)
                        databaseCurrentGame.child("round_$roundNum").child("highest_bid").setValue(1)
                        databaseCurrentGame.child("round_$roundNum").child("highest_bidder").setValue(1)

                        val roundStr = "Round $roundNum"
                        roundTextView.text = roundStr

                        if(roundNum.toInt() > 1) {
                            databaseQuestions.child("num_questions").addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val numQuestions = dataSnapshot.getValue(Int::class.java) as Int
                                    val randQuestionNum = (1..numQuestions).random()
                                    databaseGames.child(code).child("round_$roundNum")
                                        .child("question_num").setValue(randQuestionNum)

                                    databaseGames.child(code).child("round_$roundNum")
                                        .child("answers_ready").setValue(false)
                                }
                                override fun onCancelled(databaseError: DatabaseError) {
                                    // do nothing
                                }
                            })
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

    override fun onStart() {
        super.onStart()
        Timer().schedule(3000) {
            val bidIntent = Intent(this@NewRoundActivity, BidActivity::class.java)
            bidIntent.putExtra("code", code)
            startActivity(bidIntent)
        }
    }

    override fun onBackPressed() {
        // do nothing
    }

    companion object {
        private const val TAG = "GeekOut:NewRoundActivity"
    }
}