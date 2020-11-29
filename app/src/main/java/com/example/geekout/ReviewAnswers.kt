package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class ReviewAnswers : AppCompatActivity() {
    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var scoreboardButton: Button
    private lateinit var code: String
    private var answers: ArrayList<String>? = ArrayList<String>()
    private lateinit var mListView: ListView
    private lateinit var adapter: ListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_answers)

        databaseGames = FirebaseDatabase.getInstance().getReference("games")


        scoreboardButton = findViewById<Button>(R.id.scoreboardButton)

        code = intent.getStringExtra("code").toString()
        databaseCurrentGame = databaseGames.child(code)

        mListView = findViewById<ListView>(R.id.answersView)


        answers = intent.getStringArrayListExtra("answers_list")

        for(s in answers!!){
            Log.i(TAG, s)
        }

        mListView.adapter = ListViewAdapter(this, R.layout.list_item, answers!!.toTypedArray())
        mListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val checkBoxView :CheckBox = view.findViewById(R.id.checkBox)
        }

        scoreboardButton.setOnClickListener {
            val scoreboardIntent = Intent(this@ReviewAnswers, ScoreboardActivity::class.java)
            scoreboardIntent.putExtra("code", code)
            startActivity(scoreboardIntent)
        }

    }

    companion object {
        private const val TAG = "GeekOut:ReviewAnswers"
    }

}