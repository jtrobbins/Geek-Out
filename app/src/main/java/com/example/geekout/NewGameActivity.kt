package com.example.geekout

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class NewGameActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var databaseUsers: DatabaseReference
    private lateinit var databaseGames: DatabaseReference
    private lateinit var codeTextView: TextView
    private lateinit var uid: String
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)

        mAuth = FirebaseAuth.getInstance()
        databaseUsers = FirebaseDatabase.getInstance().getReference("users")
        databaseGames = FirebaseDatabase.getInstance().getReference("games")

        codeTextView = findViewById(R.id.code)

        code = createCode()
        codeTextView.text = code
        uid = FirebaseAuth.getInstance().currentUser!!.uid

        addGame()
    }

    private fun addGame() {
        databaseGames.child(code).child("in_progress").setValue(0)
        databaseUsers.child(uid).child("username").addValueEventListener(object : ValueEventListener {
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

    private fun createCode(): String {
        val r = Random()
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        var code = ""
        for (x in 0..3) {
            val c = r.nextInt(26)
            code += alphabet[c]
        }
        return code
    }

    companion object {
        private const val TAG = "GeekOut:NewGameActivity"
    }
}