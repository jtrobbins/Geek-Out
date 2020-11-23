package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.Comparator

class BidActivity : AppCompatActivity() {

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var databaseQuestions: DatabaseReference
    private lateinit var roundTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var highestBidderTextView: TextView
    private lateinit var categoryImageView: ImageView
    private lateinit var bidEditText: EditText
    private lateinit var passButton: Button
    private lateinit var bidButton: Button
    private lateinit var scoreboardButton: Button
    private lateinit var code: String
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bid)

        databaseGames = FirebaseDatabase.getInstance().getReference("games")
        databaseQuestions = FirebaseDatabase.getInstance().getReference("questions")

        roundTextView = findViewById(R.id.round)
        questionTextView = findViewById(R.id.question)
        highestBidderTextView = findViewById(R.id.highestBidder)
        categoryImageView = findViewById(R.id.categoryIcon)
        bidEditText = findViewById(R.id.bidEditText)
        passButton = findViewById<Button>(R.id.passButton)
        bidButton = findViewById<Button>(R.id.bidButton)
        scoreboardButton = findViewById<Button>(R.id.scoreboardButton)

        uid = FirebaseAuth.getInstance().currentUser!!.uid

        code = intent.getStringExtra("code").toString()
        databaseCurrentGame = databaseGames.child(code)

        passButton.setOnClickListener {
            databaseCurrentGame.child("num_pass").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val passNum = dataSnapshot.getValue(Int::class.java) as Int
                    databaseCurrentGame.child("num_pass").setValue(passNum + 1)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // do nothing
                }
            })
            bidButton.isEnabled = false
        }

        bidButton.setOnClickListener {
            val bid = Integer.parseInt(bidEditText.text.toString())
            databaseCurrentGame.child("highest_bid").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val currHighestBid = dataSnapshot.getValue(Int::class.java) as Int
                    databaseCurrentGame.child("players").child(uid).child("player_num").addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val playerNum = dataSnapshot.getValue(Int::class.java) as Int
                            if (bid > currHighestBid) {
                                databaseCurrentGame.child("highest_bid").setValue(bid)
                                databaseCurrentGame.child("highest_bidder").setValue(playerNum)
                            } else {
                                Toast.makeText(applicationContext, "Enter a higher bid!", Toast.LENGTH_LONG).show()
                            }
                            bidEditText.setText("")
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            // do nothing
                        }
                    })
                    if (bid > currHighestBid) {
                        databaseCurrentGame.child("highest_bid").setValue(bid)
                    } else {
                        Toast.makeText(applicationContext, "Enter a higher bid!", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // do nothing
                }
            })
        }

        scoreboardButton.setOnClickListener {
            val scoreboardIntent = Intent(this, ScoreboardActivity::class.java)
            scoreboardIntent.putExtra("code", code)
            startActivity(scoreboardIntent)
        }

        databaseCurrentGame.child("round_num").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val roundNum = dataSnapshot.value.toString()
                val roundStr = "Round: $roundNum"
                roundTextView.text = roundStr
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // do nothing
            }
        })

        databaseCurrentGame.child("question_num").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val questionNum = dataSnapshot.value.toString()

                databaseQuestions.child(questionNum).child("question").addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val question = dataSnapshot.value.toString()
                        questionTextView.text = question
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        // do nothing
                    }
                })

                databaseQuestions.child(questionNum).child("category").addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val category = dataSnapshot.value.toString()
                        if (category.equals("movie",true)) {
                            categoryImageView.setImageResource(R.drawable.ic_movies_icon)
                        } else if (category.equals("literature",true)) {
                            categoryImageView.setImageResource(R.drawable.ic_literature_icon)
                        } else if (category.equals("music",true)) {
                            categoryImageView.setImageResource(R.drawable.ic_music_icon)
                        } else if (category.equals("television",true)) {
                            categoryImageView.setImageResource(R.drawable.ic_television_icon)
                        } else if (category.equals("misc",true)) {
                            categoryImageView.setImageResource(R.drawable.ic_misc_icon)
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

        databaseCurrentGame.child("highest_bid").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val highestBid = dataSnapshot.getValue(Int::class.java) as Int
                databaseCurrentGame.child("highest_bidder").addListenerForSingleValueEvent(object :
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
                                            val bidStr = "Username: $username Bid: $highestBid"
                                            highestBidderTextView.text = bidStr
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

        databaseCurrentGame.child("num_pass").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val passNum = dataSnapshot.getValue(Int::class.java) as Int
                databaseCurrentGame.child("num_players").addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val numPlayers = dataSnapshot.getValue(Int::class.java) as Int
                        if (passNum == numPlayers - 1) {
                            val notImplementedIntent = Intent(this@BidActivity, NotImplementedActivity::class.java)
                            notImplementedIntent.putExtra("code", code)
                            startActivity(notImplementedIntent)
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

    companion object {
        private const val TAG = "GeekOut:BidActivity"
    }
}