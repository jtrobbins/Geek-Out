package com.example.geekout

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class UserList (private val context: Activity, private var players: List<User>) : ArrayAdapter<User>(context,
    R.layout.layout_player_list, players) {

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.layout_player_list, null, true)

        val textViewName = listViewItem.findViewById<View>(R.id.textViewName) as TextView

        val player = players[position]
        textViewName.text = player.username

        return listViewItem
    }
}