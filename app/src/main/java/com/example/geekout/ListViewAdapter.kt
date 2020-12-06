package com.example.geekout

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.google.firebase.database.*
import java.util.*
internal class ListViewAdapter(context: Context, resource: Int, private val roundNum: Long,
                               private val code: String,
                               objects: Array<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    private var mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var databaseGames: DatabaseReference
    private lateinit var databaseCurrentGame: DatabaseReference

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        databaseGames = FirebaseDatabase.getInstance().getReference("games")
        databaseCurrentGame = databaseGames.child(code)

        val newView: View
        val viewHolder = ViewHolder()

        // Check for recycled View
        if (null == convertView) {

            // Not recycled. Create the View
            newView = mLayoutInflater.inflate(R.layout.list_item, parent, false)

            // Cache View information in ViewHolder Object
            //val viewHolder = ViewHolder()
            newView.tag = viewHolder
            viewHolder.checkBoxView = newView.findViewById(R.id.checkBox)

        } else {
            newView = convertView
        }

        // Set the View's data

        // Retrieve the viewHolder Object
        val storedViewHolder = newView.tag as ViewHolder

        //Set the data in the data View
        storedViewHolder.checkBoxView.text = getItem(position)
        val txt = storedViewHolder.checkBoxView.text

        val answerCheckBox = viewHolder.checkBoxView?.findViewById<View>(R.id.checkBox) as CheckBox
        val originalStatus = answerCheckBox.isChecked

        answerCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            if(answerCheckBox.isChecked) {
                Log.i(TAG, "Checkbox was checked ")
                databaseCurrentGame.child("round_$roundNum").child("answers_contested")
                    .child("$txt").child("Contested")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if(dataSnapshot.value == null) {
                                databaseCurrentGame.child("round_$roundNum").child("answers_contested")
                                    .child("$txt").child("Contested")
                                    .setValue(1)
                            }
                            else{
                                var currVal = dataSnapshot.value as Long
                                currVal++
                                databaseCurrentGame.child("round_$roundNum").child("answers_contested")
                                    .child("$txt").child("Contested")
                                    .setValue(currVal)
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {

                        }
                    })
            }
            else if (!answerCheckBox.isChecked){
                databaseCurrentGame.child("round_$roundNum").child("answers_contested")
                    .child("$txt").child("Contested")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var currVal = dataSnapshot.value as Long
                            currVal--
                            databaseCurrentGame.child("round_$roundNum").child("answers_contested")
                                .child("$txt").child("Contested")
                                .setValue(currVal)

                        }
                        override fun onCancelled(databaseError: DatabaseError) {

                        }
                    })
            }
            //answerCheckBox.isChecked = !answerCheckBox.isChecked
        }

        return newView
    }

    internal class ViewHolder {
        lateinit var checkBoxView: CheckBox
    }

    companion object {
        private const val TAG = "GeekOut:ListViewAdapter"
    }
}


