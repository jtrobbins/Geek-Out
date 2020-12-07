package com.example.geekout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// Displays the rules for Geek-Out

class RulesActivity: AppCompatActivity() {

    private lateinit var backHome: Button
    private lateinit var instructions:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rules)
        backHome = findViewById(R.id.back_home)
        backHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        instructions = findViewById(R.id.instructions)
        instructions.text = "Geek-Out is a trivia game where players attempt to list as many answers as possible for a given question. " +
                "Players see the question and must decide whether to accept the challenge " +
                "(one answer), or whether they want to bid higher." +
                "Each player has the option to pass, or bid higher; the player with the highest " +
                "bid gets to attempt the challenge.\n\n" +
                "The player attempting the challenge must list as many answers as they bet. After they have submitted their answers, " +
                "the remaining players can vote on the validity of the answers by clicking on answers they want to contest. " +
                "If at least half of the players (not counting the player who attempted the challenge) " +
                "contest an answser, it is no longer valid. \n\n" +
                "If the player successfully completes the challenge by providing a number of answers that is equal to the bid, " +
                        "they receive 1 point. If they fail, they lose 2 points. \n\n" +
                "The winner is the first player to get to 5 points, although this “winning number” can be changed " +
                        "depending on the number of players and whether they want a shorter or longer game experience. \n\n" +
                "Create a new game or join an existing one with the four letter game code. Show off your impressive trivia knowledge!"
    }
}