package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class NewGameActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var databaseUsers: DatabaseReference
    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseQuestions: DatabaseReference
    private lateinit var codeTextView: TextView
    private lateinit var createButton: Button
    private lateinit var uid: String
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)

        mAuth = FirebaseAuth.getInstance()
        databaseUsers = FirebaseDatabase.getInstance().getReference("users")
        databaseGames = FirebaseDatabase.getInstance().getReference("games")
        databaseQuestions = FirebaseDatabase.getInstance().getReference("questions")

        codeTextView = findViewById(R.id.code)
        createButton = findViewById(R.id.createGame)

        code = createCode()
        codeTextView.text = code
        uid = FirebaseAuth.getInstance().currentUser!!.uid

        createButton.setOnClickListener {
            addGame()

            val readyIntent = Intent(this, StartGameActivity::class.java)
            readyIntent.putExtra("code", code)
            startActivity(readyIntent)
        }
    }

    private fun addGame() {
        databaseGames.child(code).child("in_progress").setValue(false)
        databaseGames.child(code).child("num_players").setValue(1)
        databaseGames.child(code).child("round_num").setValue(1)
        databaseGames.child(code).child("answers_ready").setValue(false)
        databaseUsers.child(uid).child("username").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val username = dataSnapshot.value.toString()
                val player = Player(username, 1)
                databaseGames.child(code).child("players").child(uid).setValue(player)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // do nothing
            }
        })
        databaseQuestions.child("num_questions").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val numQuestions = dataSnapshot.getValue(Int::class.java) as Int
                val randQuestionNum = (0 until (numQuestions + 1)).random()
                databaseGames.child(code).child("question_num").setValue(randQuestionNum)
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