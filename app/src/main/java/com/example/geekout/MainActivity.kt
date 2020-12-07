package com.example.geekout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment

// Main Activity for Geek-Out. Contains our four buttons New Game, Join Game, Rules, Edit Profile

class MainActivity : AppCompatActivity() {

    private lateinit var mDialog: DialogFragment
    private lateinit var newGameButton: Button
    private lateinit var joinGameButton: Button
    private lateinit var rulesButton: Button
    private lateinit var userSettingsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        newGameButton = findViewById(R.id.newGameButton)
        newGameButton.setOnClickListener {
            val newGameIntent = Intent(this, NewGameActivity::class.java)
            startActivity(newGameIntent)
        }

        joinGameButton = findViewById(R.id.joinGameButton)
        joinGameButton.setOnClickListener {
            val joinGameIntent = Intent(this, JoinGameActivity::class.java)
            startActivity(joinGameIntent)
        }

        rulesButton = findViewById(R.id.rulesButton)
        rulesButton.setOnClickListener {
            val rulesIntent = Intent(this, RulesActivity::class.java)
            startActivity(rulesIntent)
        }

        userSettingsButton = findViewById(R.id.userSettingsButton)
        userSettingsButton.setOnClickListener {
            val userSettingsIntent = Intent(this, UserSettingsActivity::class.java)
            startActivity(userSettingsIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_help -> {
                mDialog =
                    DialogFragmentMainActivity.newInstance()
                mDialog.show(
                    supportFragmentManager,
                    TAG
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        // do nothing
    }

    companion object {
        private const val TAG = "GeekOut:MainActivity"
    }
}