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
    private lateinit var questionTextView: TextView
    private lateinit var categoryImageView: ImageView
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_round)

        databaseGames = FirebaseDatabase.getInstance().getReference("games")
        databaseQuestions = FirebaseDatabase.getInstance().getReference("questions")

        roundTextView = findViewById(R.id.round)
        questionTextView = findViewById(R.id.question)
        categoryImageView = findViewById(R.id.categoryIcon)

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
    }

    override fun onStart() {
        super.onStart()
        Timer().schedule(5000) {
            val bidIntent = Intent(this@NewRoundActivity, BidActivity::class.java)
            bidIntent.putExtra("code", code)
            startActivity(bidIntent)
        }
    }

    companion object {
        private const val TAG = "GeekOut:NewRoundActivity"
    }
}