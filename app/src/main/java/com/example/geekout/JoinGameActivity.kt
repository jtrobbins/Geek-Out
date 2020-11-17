package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class JoinGameActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var databaseUsers: DatabaseReference
    private lateinit var databaseGames: DatabaseReference
    private lateinit var codeEditTextView: EditText
    private lateinit var joinButton: Button
    private lateinit var uid: String
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)

        mAuth = FirebaseAuth.getInstance()
        databaseUsers = FirebaseDatabase.getInstance().getReference("users")
        databaseGames = FirebaseDatabase.getInstance().getReference("games")

        codeEditTextView = findViewById(R.id.enter_code)
        joinButton = findViewById(R.id.joinGame)

        uid = FirebaseAuth.getInstance().currentUser!!.uid

        joinButton.setOnClickListener {
            code = codeEditTextView.text.toString()
            checkGameExists()
        }
    }

    private fun checkGameExists() {
        databaseGames.child(code).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    checkGameJoinable()
                } else {
                    Toast.makeText(this@JoinGameActivity, "Unable to join game. Invalid code.", Toast.LENGTH_LONG).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // do nothing
            }
        })
    }

    private fun checkGameJoinable() {
        databaseGames.child(code).child("in_progress").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val inProgress = dataSnapshot.getValue(Boolean::class.java) as Boolean
                if (!inProgress) {
                    databaseGames.child(code).child("num_players").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val numPlayers = dataSnapshot.getValue(Int::class.java) as Int
                            if (numPlayers < 8) {
                                joinGame()
                            } else {
                                Toast.makeText(this@JoinGameActivity, "Unable to join. Lobby Full.", Toast.LENGTH_LONG).show()
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            // do nothing
                        }
                    })
                } else {
                    Toast.makeText(this@JoinGameActivity, "Unable to join. Game in progress.", Toast.LENGTH_LONG).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // do nothing
            }
        })
    }

    private fun joinGame() {
        databaseUsers.child(uid).child("username").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val username = dataSnapshot.value.toString()
                databaseGames.child(code).child("num_players").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val numPlayers = dataSnapshot.getValue(Int::class.java) as Int
                        val player = Player(username,numPlayers + 1)
                        databaseGames.child(code).child("num_players").setValue(numPlayers + 1)
                        databaseGames.child(code).child("players").child(uid).setValue(player)
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        // do nothing
                    }
                })
                val readyIntent = Intent(this@JoinGameActivity, StartGameActivity::class.java)
                readyIntent.putExtra("code", code)
                startActivity(readyIntent)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // do nothing
            }
        })
    }

    companion object {
        private const val TAG = "GeekOut:JoinGameActivity"
    }
}