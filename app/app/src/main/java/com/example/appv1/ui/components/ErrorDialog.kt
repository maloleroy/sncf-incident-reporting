package com.example.appv1.ui.components

import android.app.AlertDialog
import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.example.appv1.ui.theme.SncfRed

fun showErrorDialog(context: Context, errorMessage: String) {
    val builder = AlertDialog.Builder(context)
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