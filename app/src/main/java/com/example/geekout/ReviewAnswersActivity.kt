package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ReviewAnswersActivity : AppCompatActivity() {
    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var scoreboardButton: Button
    private lateinit var submitButton: Button
    private lateinit var code: String
    private var mAnswers: MutableList<String>? = mutableListOf()
    private lateinit var mListView: ListView
    //private var roundNum = 1
    private var highestBid: Long = 0
    private lateinit var uid: String
    private lateinit var currUid :String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_answers)

        databaseGames = FirebaseDatabase.getInstance().getReference("games")
        currUid = FirebaseAuth.getInstance().currentUser!!.uid

        scoreboardButton = findViewById(R.id.scoreboardButton)
        submitButton = findViewById(R.id.submitButton)

        code = intent.getStringExtra("code").toString()
        highestBid = intent.getLongExtra("highest_bid", 1)
        //roundNum = intent.getIntExtra("roundNum", 1)
        uid = intent.getStringExtra("bidder_uid").toString()

        databaseCurrentGame = databaseGames.child(code)

        mListView = findViewById<ListView>(R.id.answersView)


        databaseCurrentGame.child("round_num")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val roundNum = dataSnapshot.value as Long
                    /*
                    databaseCurrentGame.child("round_$roundNum")
                        .child("answers_reviewed").child("$currUid").setValue(false)
                     */
                    addAnswersToList(roundNum, code)
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })




        scoreboardButton.setOnClickListener {
            val scoreboardIntent = Intent(this@ReviewAnswersActivity, ScoreboardActivity::class.java)
            scoreboardIntent.putExtra("code", code)
            startActivity(scoreboardIntent)
        }

    }

    private fun addAnswersToList(roundNum: Long, gameCode: String) {
        var userAnswers = mutableListOf<String>()

        for(i in 1 .. highestBid) {
            databaseCurrentGame.child("round_$roundNum").child("answers")
                .child("$i").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        userAnswers!!.add(dataSnapshot.value.toString())
                        if(userAnswers!!.size.toLong() == i) {
                            generateList(userAnswers, roundNum, gameCode)
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
        }
    }

    private fun generateList(userAnswers: MutableList<String>, round_Num: Long, gameCode: String) {
        for(i in 0 until userAnswers.size) {
            val currAnswer = userAnswers[i]
            databaseCurrentGame.child("round_$round_Num").child("answers_contested")
                .child("$currAnswer").child("Contested").setValue(0)
        }
        mAnswers = userAnswers
        mListView.adapter = ListViewAdapter(this, R.layout.list_item, round_Num, gameCode,
            mAnswers!!.toTypedArray())
        mListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val checkBoxView  = view.findViewById<CheckBox>(R.id.checkBox)

        }

        submitButton.setOnClickListener{
            databaseCurrentGame.child("round_num")
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        val roundNum = p0.value as Long
                        databaseCurrentGame.child("round_$roundNum").child("answers_reviewed")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val usersReviewed = dataSnapshot.children
                                    databaseCurrentGame.child("num_players")
                                        .addListenerForSingleValueEvent(object: ValueEventListener {
                                            override fun onDataChange(p0: DataSnapshot) {
                                                val num_players = p0.value as Long
                                                lateinit var intent: Intent
                                                if(usersReviewed.count().toLong() == num_players-2) {
                                                    setAcceptedAnswers(roundNum, num_players)
                                                    intent = Intent(this@ReviewAnswersActivity, DeterminePointsActivity::class.java)
                                                }
                                                else {
                                                    intent = Intent(this@ReviewAnswersActivity, BeforeRoundEndActivity::class.java)
                                                }
                                                databaseCurrentGame.child("round_$roundNum").child("round_over")
                                                    .setValue(false)
                                                databaseCurrentGame.child("round_$round_Num")
                                                    .child("answers_reviewed").child("$currUid").setValue(true)

                                                intent.putExtra("code", code)
                                                intent.putExtra("highestBid", highestBid)
                                                intent.putExtra("userAnswers", mAnswers!!.toTypedArray())
                                                startActivity(intent)



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

                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }

    private fun setAcceptedAnswers(roundNum: Long, numPlayers: Long) {
        var mDisplayAnswers = arrayListOf<String>()
        for(x in 1 .. highestBid) {
            databaseCurrentGame.child("round_$roundNum").child("answers").child("$x")
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        Log.i(TAG, "ANSWER NUMBER: $x")
                        val currAnswer = p0.value as String

                        databaseCurrentGame.child("round_$roundNum").child("answers_contested")
                            .child("$currAnswer").child("Contested")
                            .addListenerForSingleValueEvent(object: ValueEventListener {
                                override fun onDataChange(p0: DataSnapshot) {

                                    val contestVal = p0.value as Long
                                    if(contestVal == 0L || contestVal < ((numPlayers-1)/2)) {
                                        mDisplayAnswers!!.add(currAnswer)
                                    }

                                    if(x == highestBid) {
                                        for (i in 0 until mDisplayAnswers.size) {
                                            val pathNum = i + 1
                                            databaseCurrentGame.child("round_$roundNum")
                                                .child("accepted_answers").child("$pathNum")
                                                .setValue(mDisplayAnswers[i])
                                        }

                                        databaseCurrentGame.child("round_$roundNum")
                                            .child("accepted_answers_size").setValue(mDisplayAnswers.size)
                                    }

                                }

                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })


                    }

                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }

    }

    override fun onBackPressed() {
        // do nothing
    }

    companion object {
        private const val TAG = "GeekOut:ReviewAnswers"
    }

}