package com.sncf.reports.ui.components

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.toArgb
import com.sncf.reports.ui.theme.SncfRed

// Helper function to try and get an Activity from a Context
private fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

fun showErrorDialog(context: Context, errorMessage: String) {
    val activity = context.findActivity()

    if (activity == null || activity.isFinishing || activity.isDestroyed) {
        Log.e("ErrorDialog", "Cannot show dialog, no valid Activity context for: \"$errorMessage\".")
        return
    }

    // Use the found activity context for the AlertDialog
    val builder = AlertDialog.Builder(activity)
        .setTitle("Erreur")
        .setMessage(errorMessage)
        .setPositiveButton("Signaler l'erreur") { dialog, which ->
            // Send incident report
        }
        .setNegativeButton("Fermer") { dialog, which ->
            // Do nothing else.
        }

    val dialog = builder.create()
    dialog.setOnShowListener {
        // Get the Compose color as an Android color int
        val colorInt = SncfRed.toArgb()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(colorInt)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(colorInt)
    }
    dialog.show()
}