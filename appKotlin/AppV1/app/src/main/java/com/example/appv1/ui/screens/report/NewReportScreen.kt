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
import androidx.compose.foundation.rememberScrollState // Pour rendre la colonne scrollable
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.MaterialTheme // <-- AJOUTER CET IMPORT
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator // Pour l'indicateur de chargement
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect // Assurez-vous que cet import est présent
import androidx.compose.runtime.rememberCoroutineScope // Assurez-vous que cet import est présent
import kotlinx.coroutines.launch // Assurez-vous que cet import est présent
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.runtime.DisposableEffect // Assurez-vous que cet import est présent
import androidx.compose.runtime.rememberCoroutineScope // Assurez-vous que cet import est présent
import androidx.compose.material3.TextField // Import pour le TextField de Material 3
import com.example.appv1.GemmaEngine // Import pour votre classe GemmaEngine
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
    val scope = rememberCoroutineScope() // Coroutine scope pour les appels asynchrones

    var reportText by remember { mutableStateOf("") }
    var generatedReportText by remember { mutableStateOf<String?>(null) }
    var isLoadingMistral by remember { mutableStateOf(false) }
    var generatedGemmaText by remember { mutableStateOf<String?>(null) } // Pour Gemma
    var isLoadingGemma by remember { mutableStateOf(false) }


    // ----> 1. Initialiser GemmaEngine et gérer son cycle de vie <----
    val gemmaEngine = remember { GemmaEngine(context) }
    DisposableEffect(Unit) {
        onDispose {
            gemmaEngine.close() // Fermer l'engine quand l'écran est quitté
        }
    }

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
                .verticalScroll(rememberScrollState()) // Ajout du scroll
        ) {
            OutlinedTextField(
                value = reportText,
                onValueChange = { reportText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = { Text("Décrivez votre signalement ici…") },
                label = { Text("Description initiale") } // Ajout d'un label

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
                // ----> 3. Mettre à jour l'appel onClick <----
                onClick = {
                    if (reportText.isNotBlank()) {
                        isLoadingMistral = true // Début chargement
                        generatedReportText = null // Réinitialiser l'ancienne réponse
                        sendReportToMistral(reportText, context) { result ->
                            generatedReportText = result // Mettre à jour l'état avec le résultat
                            isLoadingMistral = false // Fin chargement
                        }
                    } else {
                        Toast.makeText(context, "Veuillez entrer ou dicter une description.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoadingMistral // Désactiver pendant le chargement
            ) {
                Text("Générer le rapport (Mistral - En ligne)")
            }

            Spacer(Modifier.height(16.dp))

            // ----> 2. Ajouter le bouton Gemma <----
            Button(
                onClick = {
                    if (reportText.isNotBlank()) {
                        isLoadingGemma = true
                        generatedGemmaText = null
                        // Réinitialiser aussi le texte Mistral
                        generatedReportText = null
                        scope.launch { // Utiliser la scope définie plus haut
                            gemmaEngine.askAsync(
                                prompt = reportText, // Utiliser le texte du rapport
                                onSuccess = { result ->
                                    generatedGemmaText = result
                                    isLoadingGemma = false
                                },
                                onError = { error ->
                                    generatedGemmaText = "Erreur Gemma: ${error.message}"
                                    isLoadingGemma = false
                                    Toast.makeText(context, "Erreur Gemma: ${error.message}", Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    } else { /* ... Toast ... */ }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoadingMistral && !isLoadingGemma // Désactiver si l'un ou l'autre charge
            ) {
                Text("Générer rapport (Gemma - Local)")
            }


            Spacer(Modifier.height(16.dp))

            // ----> 3. Afficher les résultats (Mistral OU Gemma) <----

            // Affichage chargement Mistral
            if (isLoadingMistral) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Génération Mistral en cours...", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            // Affichage résultat Mistral
            else if (generatedReportText != null) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Rapport généré par Mistral :", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(generatedReportText!!)
            }

            // Affichage chargement Gemma
            if (isLoadingGemma) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Génération Gemma en cours...", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            // Affichage résultat Gemma
            else if (generatedGemmaText != null) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Rapport généré par Gemma :", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(generatedGemmaText!!)
            }
        }
    }
}


@Composable
fun ChatScreen() {
    // ----> Déclarations des variables ICI <----
    val context = LocalContext.current // Obtenir le contexte ici
    val scope = rememberCoroutineScope() // Obtenir la coroutine scope ici
    val engine = remember { GemmaEngine(context) } // Créer l'instance de GemmaEngine ici

    // États pour le prompt, la réponse et le chargement
    var prompt by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Effet pour fermer l'engine lorsque le composable est détruit
    DisposableEffect(Unit) {
        onDispose {
            engine.close()
        }
    }
    // ----> FIN Déclarations <----


    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = prompt, // prompt est maintenant défini
            onValueChange = { prompt = it },
            label = { Text("Demande") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading // isLoading est maintenant défini
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (prompt.isNotBlank() && !isLoading) { // prompt et isLoading sont définis
                    isLoading = true // isLoading est défini
                    answer = "Génération en cours..." // answer est défini
                    scope.launch { // scope est défini
                        engine.askAsync( // engine est défini
                            prompt = prompt, // prompt est défini
                            onSuccess = { result ->
                                answer = result // answer est défini
                                isLoading = false // isLoading est défini
                            },
                            onError = { error ->
                                answer = "Erreur: ${error.message}" // answer est défini, error.message existe
                                isLoading = false // isLoading est défini
                                Toast.makeText(context, "Erreur Gemma: ${error.message}", Toast.LENGTH_LONG).show() // context et error.message sont définis
                            }
                        )
                    }
                }
            },
            enabled = !isLoading // isLoading est défini
        ) {
            Text("Envoyer")
        }
        Spacer(Modifier.height(12.dp))
        Text(answer) // answer est défini
    }
}

private fun sendReportToMistral(
    reportText: String,
    context: Context,
    onResult: (String) -> Unit // Lambda pour retourner le résultat
) {
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
                onResult(generatedText) // Appeler la lambda avec le résultat
            }
        } catch (e: Exception) {
            Log.e("MistralAPI", "Error calling Mistral API", e)
            withContext(Dispatchers.Main) {
                // Retourner un message d'erreur via la lambda
                onResult("Erreur réseau : ${e.message}")
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

