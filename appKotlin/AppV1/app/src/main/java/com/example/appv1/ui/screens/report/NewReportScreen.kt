package com.example.appv1.ui.screens.report

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.appv1.api.ChatMessage
import com.example.appv1.api.MistralChatRequest
import com.example.appv1.api.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var reportText by remember { mutableStateOf("") }

    /* ---------- 1) launcher pour l’Intent de reconnaissance vocale ---------- */
    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )
            if (!matches.isNullOrEmpty()) {
                reportText = matches[0]
            }
        }
    }

    /* ---------- 2) launcher pour la permission micro ---------- */
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchSpeech(speechLauncher)           // on lance immédiatement
        } else {
            Toast.makeText(
                context,
                "Permission micro refusée : impossible d’utiliser la dictée vocale",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouveau signalement") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = reportText,
                onValueChange = { reportText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = { Text("Décrivez votre signalement ici…") }
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    when {
                        ContextCompat.checkSelfPermission(
                            context, Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            // Permission déjà accordée
                            launchSpeech(speechLauncher)
                        }

                        else -> {
                            // On demande la permission
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Filled.Mic, contentDescription = "Speech to Text")
                Spacer(Modifier.width(8.dp))
                Text("Speech-to-Text")
            }
            Button(
                onClick = {
                    sendReportToMistral(reportText, context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Générer le rapport")
            }
        }
    }
}


private fun sendReportToMistral(reportText: String, context: Context) {
    if (reportText.isBlank()) {
        Toast.makeText(context, "Le texte du rapport est vide", Toast.LENGTH_SHORT).show()
        return
    }

    // Construire la requête pour Mistral Chat API
    val chatRequest = MistralChatRequest(
        model = "mistral-small-latest", // Ou un autre modèle
        messages = listOf(
            ChatMessage(
                role = "system",
                content = "Tu es un assistant SNCF qui reformule les signalements des chefs de bord."
            ), // Instruction système (optionnel)
            ChatMessage(role = "user", content = reportText) // Le texte du chef de bord
        )
    )

    // Utilisation de CoroutineScope pour effectuer l'appel réseau
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Appeler le nouvel endpoint
            val response = RetrofitInstance.api.generateChatCompletion(chatRequest)

            // Traiter la réponse (prendre le premier choix par exemple)
            val generatedText =
                response.choices.firstOrNull()?.message?.content ?: "Aucune réponse générée."

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Réponse : $generatedText", Toast.LENGTH_LONG).show()
                // Mettre à jour l'UI avec generatedText si nécessaire
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.e("MistralAPI", "Error calling Mistral API", e) // Log l'erreur pour le débogage
                Toast.makeText(context, "Erreur réseau : ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun launchSpeech(
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Parlez maintenant"
        )
    }
    launcher.launch(intent)
}


