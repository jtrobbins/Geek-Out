package com.example.geekout

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class WaitAnswerActivity : AppCompatActivity() {

    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait_answer)

        databaseGames = FirebaseDatabase.getInstance().getReference("games")
        code = intent.getStringExtra("code").toString()
        databaseCurrentGame = databaseGames.child(code)
    }

    override fun onStart() {
        super.onStart()

        databaseCurrentGame.child("answers_ready").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val isReady = dataSnapshot.getValue(Boolean::class.java) as Boolean
                if (isReady) {
                    val notImplementedIntent = Intent(this@WaitAnswerActivity, NotImplementedActivity::class.java)
                    notImplementedIntent.putExtra("code", code)
                    startActivity(notImplementedIntent)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // do nothing
            }
        })

    }

    companion object {
        private const val TAG = "GeekOut:WaitAnswerActivity"
    }
}