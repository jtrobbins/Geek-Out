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
    private lateinit var codeTextView: TextView
    private lateinit var playersListView: ListView
    private lateinit var readyButton: Button
    internal lateinit var players: MutableList<User>
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_game)

        codeTextView = findViewById(R.id.code)
        playersListView = findViewById(R.id.players)
        readyButton = findViewById(R.id.readyButton)
        databaseGames = FirebaseDatabase.getInstance().getReference("games")

        code = intent.getStringExtra("code").toString()
        codeTextView.text = code
        databaseCurrentGame = databaseGames.child(code)

        readyButton.setOnClickListener {
            databaseCurrentGame.child("in_progress").setValue(true)
        }

        players = ArrayList()
    }

    override fun onStart() {
        super.onStart()

        databaseCurrentGame.child("players").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                players.clear()

                var user: User? = null
                for (postSnapshot in dataSnapshot.children) {
                    try {
                        user = postSnapshot.getValue(User::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    } finally {
                        players.add(user!!)
                    }
                }
                val playersListAdapter = UserList(this@StartGameActivity, players)
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
                    val notImplementedIntent = Intent(this@StartGameActivity, NotImplementedActivity::class.java)
                    startActivity(notImplementedIntent)
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