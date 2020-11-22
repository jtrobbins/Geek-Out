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


class StartGameActivity: AppCompatActivity() {

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var databaseQuestions: DatabaseReference
    private lateinit var codeTextView: TextView
    private lateinit var playersListView: ListView
    private lateinit var readyButton: Button
    internal lateinit var players: MutableList<Player>
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_game)

        codeTextView = findViewById(R.id.code)
        playersListView = findViewById(R.id.players)
        readyButton = findViewById(R.id.readyButton)
        databaseGames = FirebaseDatabase.getInstance().getReference("games")
        databaseQuestions = FirebaseDatabase.getInstance().getReference("questions")

        code = intent.getStringExtra("code").toString()
        codeTextView.text = code
        databaseCurrentGame = databaseGames.child(code)

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

        players = ArrayList()
    }

    override fun onStart() {
        super.onStart()

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

    companion object {
        private const val TAG = "GeekOut:ReadyActivity"
    }
}