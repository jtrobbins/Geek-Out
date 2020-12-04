package com.example.geekout

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.*

class UpdatePointsActivity : AppCompatActivity() {
    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference
    private lateinit var mCurrPoints : TextView
    private lateinit var mTextCurrPoints: TextView
    private lateinit var mUpdtPoints: EditText
    private lateinit var mTextUpdtPoints: TextView
    private lateinit var updateButton: Button
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_points)

        mCurrPoints = findViewById(R.id.curr_points_win_num)
        mTextCurrPoints = findViewById(R.id.curr_points_txt)
        mUpdtPoints = findViewById(R.id.updated_points_win_num)
        mTextUpdtPoints = findViewById(R.id.upd_points_win)
        updateButton = findViewById(R.id.update_button)

        code = intent.getStringExtra("code").toString()
        databaseGames = FirebaseDatabase.getInstance().getReference("games")
        databaseCurrentGame = databaseGames.child(code)

        databaseCurrentGame.child("winning_points")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val currWin = p0.value as Long
                    mCurrPoints.text = currWin.toString()
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        updateButton.setOnClickListener {
            val newPoints = mUpdtPoints.text.toString().toLong()
            databaseCurrentGame.child("winning_points").setValue(newPoints)
            Toast.makeText(applicationContext,
                "Number of points needed to win has been updated!", Toast.LENGTH_LONG)
                .show()
        }
    }


}