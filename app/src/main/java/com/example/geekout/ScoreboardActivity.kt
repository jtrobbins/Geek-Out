package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.ArrayList

class ScoreboardActivity : AppCompatActivity() {

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var playersListView: ListView
    private lateinit var returnButton: Button
    internal lateinit var playersScore: MutableList<PlayerScore>
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)

        playersListView = findViewById(R.id.players)
        returnButton = findViewById(R.id.returnButton)
        databaseGames = FirebaseDatabase.getInstance().getReference("games")

        code = intent.getStringExtra("code").toString()
        databaseCurrentGame = databaseGames.child(code)

        returnButton.setOnClickListener {
            finish()
        }

        playersScore = ArrayList()
    }

    override fun onStart() {
        super.onStart()

        databaseCurrentGame.child("players").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                playersScore.clear()

                var user: PlayerScore? = null
                for (postSnapshot in dataSnapshot.children) {
                    try {
                        user = postSnapshot.getValue(PlayerScore::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    } finally {
                        playersScore.add(user!!)
                    }
                }
                val playersListAdapter = PlayerScoreList(this@ScoreboardActivity, playersScore)
                playersListView.adapter = playersListAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // do nothing
            }
        })
    }

    companion object {
        private const val TAG = "GeekOut:ScoreboardActivity"
    }

}