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
    //private var mDisplayAnswers:MutableList<String>? = mutableListOf()
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

        mConstraintView.setOnClickListener {
            databaseCurrentGame.child("round_num")
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val roundNum = dataSnapshot.value as Long
                        databaseCurrentGame.child("round_$roundNum").child("round_over").setValue(true)

                    }

                    override fun onCancelled(dataSnapshot: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }

    }

    override fun onStart() {
        super.onStart()
        databaseCurrentGame.child("round_num")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val roundNum = dataSnapshot.value as Long
                    databaseCurrentGame.child("round_$roundNum")
                        .child("round_over").addValueEventListener(object: ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                val over = p0.value as Boolean
                                if(over) {
                                    databaseCurrentGame.child("winner").addValueEventListener(object: ValueEventListener {
                                        override fun onDataChange(p0: DataSnapshot) {
                                            val updatedRoundNum = roundNum+1
                                            databaseCurrentGame.child("round_num").setValue(updatedRoundNum)
                                            if(p0.value == null) {
                                                val intent = Intent(this@DeterminePointsActivity, NewRoundActivity::class.java)
                                                intent.putExtra("code", code)
                                                startActivity(intent)

                                            }
                                            else {
                                                val intent = Intent(this@DeterminePointsActivity, PlayerWonActivity::class.java)
                                                intent.putExtra("code", code)
                                                startActivity(intent)

                                            }
                                        }

                                        override fun onCancelled(p0: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }
                                    })
                                }

                            }

                            override fun onCancelled(p0: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                }

                override fun onCancelled(dataSnapshot: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun addAnswersToList(numPlayers: Long, roundNum: Long) {
        var mDisplayAnswers = arrayListOf<String>()
        databaseCurrentGame.child("round_$roundNum").child("accepted_answers_size")
            .addValueEventListener(object:ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.value != null) {
                    val size = p0.value as Long
                    for(i in 1 .. size) {
                        databaseCurrentGame.child("round_$roundNum").child("accepted_answers")
                            .child("$i").addListenerForSingleValueEvent(object :ValueEventListener {
                                override fun onDataChange(p0: DataSnapshot) {
                                    val answer = p0.value as String
                                    mDisplayAnswers!!.add(answer)

                                    if(i == size) {
                                        Log.i(TAG, "ANSWERS SIZE $size")
                                        generateList(mDisplayAnswers, roundNum)
                                    }
                                }

                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun generateList(mDisplayAnswers: ArrayList<String>, roundNum: Long) {
        mListView.adapter = DeterminePointsAdapter(this,
            R.layout.updated_list_item, mDisplayAnswers)
        mListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val textView  = view.findViewById<TextView>(R.id.textViewList)
        }

        databaseCurrentGame.child("round_$roundNum").child("highest_bidder_uid")
            .addListenerForSingleValueEvent(object:ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.value == null) {
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

    private fun updatePoints(mDisplayAnswers: ArrayList<String>, roundNum: Long, uid: String) {
        databaseCurrentGame.child("players").child("$uid")
            .child("points")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var currVal = dataSnapshot.value as Long
                    //currVal++
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
                                                    currVal++
                                                    databaseCurrentGame.child("players")
                                                        .child("$uid").child("points")
                                                        .setValue(currVal)
                                                    databaseCurrentGame.child("round_$roundNum")
                                                        .child("roundWon").setValue(true)
                                                }
                                                else {
                                                    currVal -= 2
                                                    databaseCurrentGame.child("players")
                                                        .child("$uid").child("points")
                                                        .setValue(currVal)
                                                    databaseCurrentGame.child("round_$roundNum")
                                                        .child("roundWon").setValue(false)
                                                }
                                            }
                                            databaseCurrentGame.child("winning_points")
                                                .addValueEventListener(object: ValueEventListener {
                                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                        val pointsToWin = dataSnapshot.value as Long

                                                        if(currVal == pointsToWin) {
                                                            databaseCurrentGame.child("winner").setValue(uid)
                                                            Toast.makeText(applicationContext,
                                                                "$username won the game!", Toast.LENGTH_LONG)
                                                                .show()
                                                        }
                                                        else {
                                                            databaseCurrentGame.child("round_$roundNum").child("roundWon")
                                                                .addListenerForSingleValueEvent(object: ValueEventListener {
                                                                    override fun onDataChange(p0: DataSnapshot) {
                                                                       val won = p0.value as Boolean
                                                                        if(won) {
                                                                            Toast.makeText(applicationContext,
                                                                                "$username won the bet!", Toast.LENGTH_LONG)
                                                                                .show()
                                                                        }
                                                                        else {
                                                                            Toast.makeText(applicationContext,
                                                                                "$username did not win the bet!", Toast.LENGTH_LONG)
                                                                                .show()
                                                                        }
                                                                    }

                                                                    override fun onCancelled(p0: DatabaseError) {
                                                                        TODO("Not yet implemented")
                                                                    }
                                                                })

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

                            override fun onCancelled(p0: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                }
                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }

    override fun onBackPressed() {
        // do nothing
    }

    companion object {
        private const val TAG = "GeekOut:DeterminePoints"
    }

    /*


     */
}
