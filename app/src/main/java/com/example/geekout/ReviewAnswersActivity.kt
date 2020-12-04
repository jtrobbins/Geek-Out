package com.example.geekout

import android.content.Intent
import android.os.Bundle
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
        mAnswers = userAnswers
        mListView.adapter = ListViewAdapter(this, R.layout.list_item, round_Num, gameCode,
            mAnswers!!.toTypedArray())
        mListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val checkBoxView  = view.findViewById<CheckBox>(R.id.checkBox)

        }

        submitButton.setOnClickListener{
            val intent = Intent(this@ReviewAnswersActivity, BeforeRoundEndActivity::class.java)
            intent.putExtra("code", code)
            //intent.putExtra("roundNum", roundNum)
            intent.putExtra("highestBid", highestBid)
            intent.putExtra("userAnswers", mAnswers!!.toTypedArray())
            intent.putExtra("bidder_uid", uid)
            databaseCurrentGame.child("round_$round_Num")
                .child("answers_reviewed").child("$currUid").setValue(true)
            startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "GeekOut:ReviewAnswers"
    }

}