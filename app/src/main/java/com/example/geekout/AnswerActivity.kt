package com.example.geekout

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// Allows for highest bidder to answer the questions

class AnswerActivity : AppCompatActivity() {

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var databaseQuestions: DatabaseReference
    private lateinit var linearLayout: LinearLayout
    private lateinit var roundTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var categoryImageView: ImageView
    private lateinit var allEditText: ArrayList<EditText>
    private lateinit var code: String
    private var highestBid: Long = 1
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)

        databaseGames = FirebaseDatabase.getInstance().getReference("games")
        databaseQuestions = FirebaseDatabase.getInstance().getReference("questions")
        uid = FirebaseAuth.getInstance().currentUser!!.uid

        linearLayout = findViewById(R.id.linearLayout)
        roundTextView = findViewById(R.id.round)
        questionTextView = findViewById(R.id.question)
        categoryImageView = findViewById(R.id.categoryIcon)

        allEditText = arrayListOf<EditText>()

        code = intent.getStringExtra("code").toString()
        highestBid = intent.getLongExtra("highest_bid", 1)
        databaseCurrentGame = databaseGames.child(code)

        // Change interface to match round number and question from firebase
        databaseCurrentGame.child("round_num").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val roundNum = dataSnapshot.value.toString()

                val roundStr = "Round: $roundNum"
                roundTextView.text = roundStr

                databaseCurrentGame.child("round_$roundNum").child("question_num").addListenerForSingleValueEvent(object :
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

                // Programmatically modifies interface
                databaseCurrentGame.child("round_$roundNum").child("highest_bid").addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val numAnswers = dataSnapshot.getValue(Int::class.java) as Int
                        // Adds answer fields programmatically to match the bid number
                        for (i in 1..numAnswers) {
                            val textView = TextView(this@AnswerActivity)
                            val str = "Answer $i"
                            textView.text = str
                            linearLayout.addView(textView)

                            val editText = EditText(this@AnswerActivity)
                            editText.setHint(R.string.enter_answer)
                            editText.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            editText.setPadding(20, 20, 20, 20)
                            linearLayout.addView(editText)
                            allEditText.add(editText)
                        }

                        // Adds the submit button programmatically
                        val submitButton = Button(this@AnswerActivity)
                        submitButton.setText(R.string.submit)
                        submitButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                        submitButton.setOnClickListener {
                            val answers = arrayListOf<String>()
                            var num = 0
                            // Checks to make sure all fields have been filled and no two fields have the same answer
                            for (i in 0 until allEditText.size) {
                                val strAnswer = allEditText[i].text.toString()
                                if (strAnswer == "") {
                                    Toast.makeText(applicationContext, "Please fill all answer fields!", Toast.LENGTH_LONG).show()
                                    break
                                }
                                if(!answers.contains(strAnswer)) {
                                    answers.add(allEditText[i].text.toString())
                                    num++
                                } else {
                                    Toast.makeText(applicationContext, "Please enter a non-duplicate answer!", Toast.LENGTH_LONG).show()
                                    break
                                }
                            }
                            // pushes answers to firebase
                            if (num == numAnswers) {
                                for (i in 0 until answers.size) {
                                    val pathNum = i + 1
                                    databaseGames.child(code).child("round_$roundNum")
                                        .child("answers").child("$pathNum")
                                        .setValue(answers[i])
                                }

                                Toast.makeText(applicationContext, "Answers submitted!", Toast.LENGTH_LONG).show()
                                databaseCurrentGame.child("round_$roundNum").child("answers_ready").setValue(true)

                                // Starts next activity once finished answering
                                val waitReviewIntent = Intent(this@AnswerActivity, WaitReviewActivity::class.java)
                                waitReviewIntent.putExtra("code", code)
                                waitReviewIntent.putExtra("highest_bid", highestBid)
                                waitReviewIntent.putExtra("userAnswers", answers!!.toTypedArray())
                                startActivity(waitReviewIntent)
                            }


                        }
                        linearLayout.addView(submitButton)

                        // Adds the scoreboard button programmatically
                        val scoreboardButton = Button(this@AnswerActivity)
                        scoreboardButton.setText(R.string.scoreboard)
                        scoreboardButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                        scoreboardButton.setOnClickListener {
                            val scoreboardIntent = Intent(this@AnswerActivity, ScoreboardActivity::class.java)
                            scoreboardIntent.putExtra("code", code)
                            startActivity(scoreboardIntent)
                        }
                        linearLayout.addView(scoreboardButton)
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

    override fun onBackPressed() {
        // do nothing
    }

    companion object {
        private const val TAG = "GeekOut:AnswerActivity"
    }
}