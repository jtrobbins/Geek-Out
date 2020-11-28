package com.example.geekout

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.system.Os.link
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)

        databaseGames = FirebaseDatabase.getInstance().getReference("games")
        databaseQuestions = FirebaseDatabase.getInstance().getReference("questions")

        linearLayout = findViewById(R.id.linearLayout)
        roundTextView = findViewById(R.id.round)
        questionTextView = findViewById(R.id.question)
        categoryImageView = findViewById(R.id.categoryIcon)

        allEditText = arrayListOf<EditText>()

        code = intent.getStringExtra("code").toString()
        databaseCurrentGame = databaseGames.child(code)

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

        databaseCurrentGame.child("highest_bid").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val numAnswers = dataSnapshot.getValue(Int::class.java) as Int
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
                val submitButton = Button(this@AnswerActivity)
                submitButton.setText(R.string.submit)
                submitButton.setOnClickListener {
                    Toast.makeText(applicationContext, "TEST TEST", Toast.LENGTH_LONG).show()
                }
                linearLayout.addView(submitButton)

                val scoreboardButton = Button(this@AnswerActivity)
                scoreboardButton.setText(R.string.scoreboard)
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

    companion object {
        private const val TAG = "GeekOut:AnswerActivity"
    }
}