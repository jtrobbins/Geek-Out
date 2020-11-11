package com.example.geekout

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
        joinButton = findViewById(R.id.createGame)

        uid = FirebaseAuth.getInstance().currentUser!!.uid

        joinButton.setOnClickListener {
            code = codeEditTextView.text.toString()
            joinGame()
        }
    }

    private fun joinGame() {
        databaseUsers.child(uid).child("username").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val username = dataSnapshot.value.toString()
                val player = Player(username)
                databaseGames.child(code).child("players").child(uid).setValue(player)
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