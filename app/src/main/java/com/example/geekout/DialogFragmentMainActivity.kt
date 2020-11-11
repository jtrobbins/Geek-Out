package com.example.geekout

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DialogFragmentMainActivity : DialogFragment() {

    companion object {

        fun newInstance(): DialogFragmentMainActivity {
            return DialogFragmentMainActivity()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
            .setTitle("Lorem Ipsum")
            .setMessage("Lorem Ipsum.")
            .create()
    }
}