package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.*

class PlayerWonActivity : AppCompatActivity() {

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var mConstraintView : ConstraintLayout
    private lateinit var mTextView :TextView
    private lateinit var code: String
    //private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_won)

        mConstraintView = findViewById(R.id.pwLayout)
        mTextView = findViewById(R.id.playerWon)

        code = intent.getStringExtra("code").toString()
        //uid = intent.getStringExtra("bidder_uid").toString()
        databaseGames = FirebaseDatabase.getInstance().getReference("games")
        databaseCurrentGame = databaseGames.child(code)

        databaseCurrentGame.child("winner").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val uid = p0.value as String
                databaseCurrentGame.child("players").child("$uid").child("username")
                    .addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            val userName = p0.value as String
                            mTextView.text = "$userName WON THE GAME!"
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    override fun onStart() {
        super.onStart()
        mConstraintView.setOnClickListener {
            val intent = Intent(this@PlayerWonActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        // do nothing
    }

    companion object {
        private const val TAG = "GeekOut:NotImplementedActivity"
    }
}