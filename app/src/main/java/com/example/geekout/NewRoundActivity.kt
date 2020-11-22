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

    private var questionNum: Int? = null
    fun setQuestionNum(num: Int?) {
        this.questionNum = num
    }

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