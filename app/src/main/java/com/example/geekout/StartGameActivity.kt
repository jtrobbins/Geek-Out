package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.*

// The game lobby activity

class StartGameActivity: AppCompatActivity() {

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var databaseQuestions: DatabaseReference
    private lateinit var codeTextView: TextView
    private lateinit var playersListView: ListView
    private lateinit var readyButton: Button
    private lateinit var changePoints: Button
    internal lateinit var players: MutableList<Player>
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_game)

        codeTextView = findViewById(R.id.code)
        playersListView = findViewById(R.id.players)
        readyButton = findViewById(R.id.readyButton)
        changePoints = findViewById(R.id.update_winning_points)
        databaseGames = FirebaseDatabase.getInstance().getReference("games")
        databaseQuestions = FirebaseDatabase.getInstance().getReference("questions")

        code = intent.getStringExtra("code").toString()
        codeTextView.text = code
        databaseCurrentGame = databaseGames.child(code)

        // Allows anyone to start the game if there is more than one player
        readyButton.setOnClickListener {
            databaseGames.child(code).child("num_players").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val numPlayers = dataSnapshot.getValue(Int::class.java) as Int
                    if (numPlayers >= 2) {
                        databaseCurrentGame.child("in_progress").setValue(true)
                    } else {
                        Toast.makeText(this@StartGameActivity, "Unable to start. Not enough players.", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // do nothing
                }
            })
        }

        // Allows for player to visit another activity to change the amount of points needed to win
        changePoints.setOnClickListener {
            val intent = Intent(this@StartGameActivity, UpdatePointsActivity::class.java)
            intent.putExtra("code", code)
            startActivity(intent)
        }

        players = ArrayList()
    }

    override fun onStart() {
        super.onStart()

        // Grabs the players in the current game and displays them as they join
        databaseCurrentGame.child("players").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                players.clear()

                var user: Player? = null
                for (postSnapshot in dataSnapshot.children) {
                    try {
                        user = postSnapshot.getValue(Player::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    } finally {
                        players.add(user!!)
                    }
                }
                // Order players by when they joined using a detemined player number
                Collections.sort(players,
                    Comparator { object1, object2 ->
                        object1.player_num.compareTo(object2.player_num)
                    })
                val playersListAdapter = UserAdapter(this@StartGameActivity, players)
                playersListView.adapter = playersListAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // do nothing
            }
        })

        // If game has been started by another player or yourself, then start the game
        databaseCurrentGame.child("in_progress").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val num = dataSnapshot.getValue(Boolean::class.java) as Boolean
                if (num) {
                    val newRoundIntent = Intent(this@StartGameActivity, NewRoundActivity::class.java)
                    newRoundIntent.putExtra("code", code)
                    startActivity(newRoundIntent)
                }
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
        private const val TAG = "GeekOut:ReadyActivity"
    }
}