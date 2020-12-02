package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.*

class DeterminePoints : AppCompatActivity(){

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var updatedAnswers: Array<String>
    private var mDisplayAnswers:MutableList<String>? = mutableListOf()
    private lateinit var scoreboardButton: Button
    private lateinit var code: String
    private lateinit var mListView: ListView
    private lateinit var mConstraintView :ConstraintLayout
    private var highestBid: Long = 0
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.determine_points)

        mListView = findViewById(R.id.updatedAnswersView)
        scoreboardButton = findViewById(R.id.scoreboardButton)
        mConstraintView = findViewById(R.id.dpLayout)

        code = intent.getStringExtra("code").toString()
        highestBid = intent.getLongExtra("highestBid", 1)
        uid = intent.getStringExtra("bidder_uid").toString()
        updatedAnswers = intent.getStringArrayExtra("userAnswers")!! as Array<String>

        databaseGames = FirebaseDatabase.getInstance().getReference("games")
        databaseCurrentGame = databaseGames.child(code)

        databaseCurrentGame.child("num_players")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val numPlayers = dataSnapshot.value as Long
                    databaseCurrentGame.child("round_num")
                        .addValueEventListener(object: ValueEventListener {
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
            val scoreboardIntent = Intent(this@DeterminePoints, ScoreboardActivity::class.java)
            scoreboardIntent.putExtra("code", code)
            startActivity(scoreboardIntent)
        }

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

                        //Log.i(TAG, "A: $a UA: ${updatedAnswers.get(updatedAnswers.size-1)}")

                        if(a == updatedAnswers.get(updatedAnswers.size-1)) {
                            Log.i(TAG, "Ça doit passer qu'une fois")
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

        updatePoints(mDisplayAnswers, roundNum)
    }

    private fun updatePoints(mDisplayAnswers: Array<String>, roundNum: Long) {
        Log.i(TAG, "Round Num: $roundNum Updating list")
        if(mDisplayAnswers.size >= highestBid) {
            databaseCurrentGame.child("players").child("$uid")
                .child("points")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var currVal = dataSnapshot.value as Long
                        currVal++

                        databaseCurrentGame.child("players")
                            .child("$uid").child("points")
                            .setValue(currVal)

                        databaseCurrentGame.child("winning_points")
                            .addValueEventListener(object: ValueEventListener {

                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val pointsToWin = dataSnapshot.value as Long

                                    if(pointsToWin == currVal) {
                                        mConstraintView.setOnClickListener {
                                            val intent = Intent(this@DeterminePoints, PlayerWon::class.java)
                                            intent.putExtra("bidder_uid",uid)
                                            startActivity(intent)
                                        }
                                    }
                                    else {
                                        mConstraintView.setOnClickListener {

                                            val updatedRoundNum = roundNum+1

                                            val intent = Intent(this@DeterminePoints, NewRoundActivity::class.java)
                                            databaseCurrentGame.child("round_num").setValue(updatedRoundNum)
                                            intent.putExtra("code", code)
                                            startActivity(intent)

                                        }
                                    }

                                }

                                override fun onCancelled(databaseError: DatabaseError) {

                                }
                            })

                    }
                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })

        }
        else {
            mConstraintView.setOnClickListener {
                val updatedRoundNum = roundNum+1
                val intent = Intent(this@DeterminePoints, NewRoundActivity::class.java)
                databaseCurrentGame.child("round_num").setValue(updatedRoundNum)
                intent.putExtra("code", code)
                startActivity(intent)
            }

        }
    }

    companion object {
        private const val TAG = "GeekOut:DeterminePoints"
    }
}
