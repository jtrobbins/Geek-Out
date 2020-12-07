package com.example.geekout

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.*
import kotlin.Comparator

// Scoreboard Activity. Shows a list of players and their current points

class ScoreboardActivity : AppCompatActivity() {

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var playersListView: ListView
    private lateinit var returnButton: Button
    internal lateinit var playersScore: MutableList<Player>
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

        // Creates a list of players and their current points for display in a listView
        databaseCurrentGame.child("players").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                playersScore.clear()

                var user: Player? = null
                for (postSnapshot in dataSnapshot.children) {
                    try {
                        user = postSnapshot.getValue(Player::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    } finally {
                        playersScore.add(user!!)
                    }
                }
                // list of players and their points by most points
                Collections.sort(playersScore,
                    Comparator { object1, object2 ->
                        object1.points.compareTo(object2.points)
                    })
                playersScore.reverse()
                val playersListAdapter = PlayerScoreAdapter(this@ScoreboardActivity, playersScore)
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