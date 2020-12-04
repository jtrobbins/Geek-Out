package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.*
import java.util.*
import kotlin.concurrent.schedule

class DeterminePointsActivity : AppCompatActivity(){

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var updatedAnswers: Array<String>
    private var mDisplayAnswers:MutableList<String>? = mutableListOf()
    private lateinit var scoreboardButton: Button
    private lateinit var code: String
    private lateinit var mListView: ListView
    private lateinit var mConstraintView :ConstraintLayout
    private var highestBid: Long = 0
    //private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_determine_points)

        mListView = findViewById(R.id.updatedAnswersView)
        scoreboardButton = findViewById(R.id.scoreboardButton)
        mConstraintView = findViewById(R.id.dpLayout)

        code = intent.getStringExtra("code").toString()
        highestBid = intent.getLongExtra("highestBid", 1)
        //uid = intent.getStringExtra("bidder_uid").toString()
        updatedAnswers = intent.getStringArrayExtra("userAnswers")!! as Array<String>

        databaseGames = FirebaseDatabase.getInstance().getReference("games")
        databaseCurrentGame = databaseGames.child(code)

        databaseCurrentGame.child("num_players")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val numPlayers = dataSnapshot.value as Long
                    databaseCurrentGame.child("round_num")
                        .addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val roundNum = dataSnapshot.value as Long
                                addAnswersToList(numPlayers, roundNum)
                            }

                            override fun onCancelled(dataSnapshot: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                }
                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

        scoreboardButton.setOnClickListener {
            val scoreboardIntent = Intent(this@DeterminePointsActivity, ScoreboardActivity::class.java)
            scoreboardIntent.putExtra("code", code)
            startActivity(scoreboardIntent)
        }

        gameContinueOrOver()


    }

    override fun onStart() {
        super.onStart()
    }

    private fun addAnswersToList(numPlayers: Long, roundNum: Long) {
        for(a in updatedAnswers) {
            databaseCurrentGame.child("round_$roundNum").child("answers")
                .child("$a").child("Contested").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if(dataSnapshot.value != null) {
                            val contestVal = dataSnapshot.value as Long
                            if(contestVal < (numPlayers-1)/2) {
                                mDisplayAnswers!!.add(a)
                            }
                        }
                        else {
                            mDisplayAnswers!!.add(a)
                        }

                        if(a == updatedAnswers.get(updatedAnswers.size-1)) {
                            generateList(mDisplayAnswers!!.toTypedArray(), roundNum)
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
        }
    }

    private fun generateList(mDisplayAnswers: Array<String>, roundNum: Long) {
        Log.i(TAG, "Round Num: $roundNum Generating list")
        mListView.adapter = DeterminePointsAdapter(this,
            R.layout.updated_list_item, mDisplayAnswers)
        mListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val textView  = view.findViewById<TextView>(R.id.textViewList)
        }

        databaseCurrentGame.child("round_$roundNum").child("highest_bidder_uid")
            .addListenerForSingleValueEvent(object:ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.value == null) {
                        Log.i(TAG, "ROUND NUM: $roundNum")
                    }
                    else {
                        val highest_uid = p0.value as String
                        updatePoints(mDisplayAnswers, roundNum, highest_uid)
                    }

                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun updatePoints(mDisplayAnswers: Array<String>, roundNum: Long, uid: String) {
        Log.i(TAG, "Round Num: $roundNum Updating list")
        databaseCurrentGame.child("players").child("$uid")
            .child("points")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var currVal = dataSnapshot.value as Long
                    currVal++

                    databaseCurrentGame.child("players").child("$uid")
                        .child("username").addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                              val username = p0.value as String
                                databaseCurrentGame.child("round_$roundNum").child("updated_vals")
                                    .addListenerForSingleValueEvent(object: ValueEventListener {
                                        override fun onDataChange(p0: DataSnapshot) {
                                            if(p0.value == null) {
                                                databaseCurrentGame.child("round_$roundNum")
                                                    .child("updated_vals").setValue(true)
                                                if(mDisplayAnswers.size >= highestBid) {
                                                    databaseCurrentGame.child("players")
                                                        .child("$uid").child("points")
                                                        .setValue(currVal)

                                                    databaseCurrentGame.child("winning_points")
                                                        .addValueEventListener(object: ValueEventListener {

                                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                                val pointsToWin = dataSnapshot.value as Long

                                                                if(currVal == pointsToWin) {
                                                                    databaseCurrentGame.child("winner").setValue(uid)
                                                                    Toast.makeText(applicationContext,
                                                                        "Game over!", Toast.LENGTH_LONG)
                                                                        .show()
                                                                }
                                                                else {
                                                                    val updatedRoundNum = roundNum+1
                                                                    databaseCurrentGame.child("round_num").setValue(updatedRoundNum)
                                                                    Toast.makeText(applicationContext,
                                                                        "$username won the bet!", Toast.LENGTH_LONG)
                                                                        .show()
                                                                }

                                                            }

                                                            override fun onCancelled(databaseError: DatabaseError) {

                                                            }
                                                        })
                                                }
                                                else {
                                                    val updatedRoundNum = roundNum+1
                                                    databaseCurrentGame.child("round_num").setValue(updatedRoundNum)
                                                    Toast.makeText(applicationContext,
                                                        "$username did not win the bet!", Toast.LENGTH_LONG)
                                                        .show()
                                                }
                                            }
                                        }

                                        override fun onCancelled(p0: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }
                                    })

                                //gameContinueOrOver()
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                }
                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }

    private fun gameContinueOrOver() {
        databaseCurrentGame.child("winner").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.value == null) {
                    Log.i(TAG, "Game not yet over")
                    Timer().schedule(3000) {
                        val intent = Intent(this@DeterminePointsActivity, NewRoundActivity::class.java)
                        intent.putExtra("code", code)
                        startActivity(intent)
                    }
                }
                else {
                    Timer().schedule(3000){
                        Log.i(TAG, "Game over")
                        val intent = Intent(this@DeterminePointsActivity, PlayerWonActivity::class.java)
                        //intent.putExtra("bidder_uid",uid)
                        intent.putExtra("code", code)
                        startActivity(intent)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    companion object {
        private const val TAG = "GeekOut:DeterminePoints"
    }

    /*


     */
}
