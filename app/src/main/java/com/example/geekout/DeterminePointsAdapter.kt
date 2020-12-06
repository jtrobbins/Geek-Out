package com.example.geekout

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DeterminePointsAdapter(context: Context, resource: Int, objects: ArrayList<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    private var mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        databaseGames = FirebaseDatabase.getInstance().getReference("games")
       // databaseCurrentGame = databaseGames.child(code)

        val newView: View
        val viewHolder = ViewHolder()

        // Check for recycled View
        if (null == convertView) {

            // Not recycled. Create the View
            newView = mLayoutInflater.inflate(R.layout.updated_list_item, parent, false)

            // Cache View information in ViewHolder Object
            //val viewHolder = ViewHolder()
            newView.tag = viewHolder
            viewHolder.textView = newView.findViewById(R.id.textViewList)

        } else {
            newView = convertView
        }

        // Set the View's data

        // Retrieve the viewHolder Object
        val storedViewHolder = newView.tag as ViewHolder

        //Set the data in the data View
        storedViewHolder.textView.text = getItem(position)

        return newView
    }

    // The ViewHolder class. See:
    // http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
    internal class ViewHolder {
        lateinit var textView: TextView
    }

    companion object {
        private const val TAG = "GeekOut:DeterminePointsAdapter"
    }
}
