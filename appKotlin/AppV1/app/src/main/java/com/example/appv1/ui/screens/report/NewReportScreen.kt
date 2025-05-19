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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.appv1.GemmaEngine
import com.example.appv1.api.ChatMessage
import com.example.appv1.api.ChatRequest
import com.example.appv1.api.RetrofitInstance
import com.example.appv1.ui.components.NewReportScreenDivider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Coroutine scope pour les appels asynchrones

    var trainType by remember { mutableStateOf("") }
    var trainNumber by remember { mutableStateOf("") }
    var reportText by remember { mutableStateOf("") }
    var generatedReportText by remember { mutableStateOf<String?>(null) }
    var isOnlineAILoading by remember { mutableStateOf(false) }
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
            // Champ pour le type de train
            OutlinedTextField(
                value = trainType,
                onValueChange = { trainType = it },
                label = { Text("Type de train") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            // Champ pour le numéro de rame
            OutlinedTextField(
                value = trainNumber,
                onValueChange = { trainNumber = it },
                label = { Text("Numéro de rame") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
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
                modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(56.dp)
            ) {
                Icon(Icons.Filled.Mic, contentDescription = "Speech to Text")
                //Spacer(Modifier.width(8.dp))
                //Text("Speech-to-Text")
            }
            Spacer(Modifier.height(56.dp))
            Button(
                // ----> 3. Mettre à jour l'appel onClick <----
                onClick = {
                    if (reportText.isNotBlank() && trainType.isNotBlank() && trainNumber.isNotBlank()) {
                        isOnlineAILoading = true // Début chargement
                        generatedReportText = null // Réinitialiser l'ancienne réponse
                        sendReportToAI(reportText, trainType, trainNumber, context) { result ->
                            generatedReportText = result // Mettre à jour l'état avec le résultat
                            isOnlineAILoading = false // Fin chargement
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Veuillez entrer ou dicter une description.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isOnlineAILoading // Désactiver pendant le chargement
            ) {
                Text("Générer le rapport par IA")
            }

            Spacer(Modifier.height(16.dp))

            // ----> 2. Ajouter le bouton Gemma <----
            // Button(
            //     onClick = {
            //         if (reportText.isNotBlank()) {
            //             isLoadingGemma = true
            //             generatedGemmaText = null
            //             generatedReportText = null
            //             scope.launch { // Utiliser la scope définie plus haut
            //                 gemmaEngine.askAsync(
            //                     prompt = reportText, // Utiliser le texte du rapport
            //                     onSuccess = { result ->
            //                         generatedGemmaText = result
            //                         isLoadingGemma = false
            //                     },
            //                     onError = { error ->
            //                         generatedGemmaText = "Erreur Gemma: ${error.message}"
            //                         isLoadingGemma = false
            //                         Toast.makeText(
            //                             context,
            //                             "Erreur Gemma: ${error.message}",
            //                             Toast.LENGTH_LONG
            //                         ).show()
            //                     }
            //                 )
            //             }
            //         } else { /* ... Toast ... */
            //         }
            //     },
            //     modifier = Modifier
            //         .fillMaxWidth()
            //         .height(56.dp),
            //     enabled = !isOnlineAILoading && !isLoadingGemma // Désactiver si l'un ou l'autre charge
            // ) {
            //     Text("Générer rapport (Gemma - Local)")
            // }


            Spacer(Modifier.height(16.dp))

            // ----> 3. Afficher les résultats <----

            // Affichage chargement IA
            if (isOnlineAILoading) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Génération en cours...", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (generatedReportText != null) {
                NewReportScreenDivider()
                Text("Rapport généré par l'IA :", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(generatedReportText!!)
            }

            // Affichage chargement Gemma
            if (isLoadingGemma) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Génération Gemma en cours...", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            // Affichage résultat Gemma
            else if (generatedGemmaText != null) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
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


    Column(Modifier
        .fillMaxSize()
        .padding(16.dp)) {
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
                                answer =
                                    "Erreur: ${error.message}" // answer est défini, error.message existe
                                isLoading = false // isLoading est défini
                                Toast.makeText(
                                    context,
                                    "Erreur Gemma: ${error.message}",
                                    Toast.LENGTH_LONG
                                ).show() // context et error.message sont définis
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

private fun sendReportToAI(
    reportText: String,
    trainType: String,
    trainNumber: String,
    context: Context,
    onResult: (String) -> Unit // Lambda pour retourner le résultat
) {
    if (reportText.isBlank()) {
        Toast.makeText(context, "Le texte du rapport est vide", Toast.LENGTH_SHORT).show()
        return
    }
    if (trainType.isBlank()) {
        Toast.makeText(context, "Veuillez préciser le train", Toast.LENGTH_SHORT).show()
        return
    }
    if (trainNumber.isBlank()) {
        Toast.makeText(context, "Veuillez préciser le numéro de rame", Toast.LENGTH_SHORT).show()
        return
    }

    // Construire la requête pour Chat API
    val chatRequest = ChatRequest(
        messages = listOf(
            ChatMessage(
                role = "system",
                content = "Tu es un assistant SNCF qui reformule les signalements des chefs de bord."
            ),
            ChatMessage(role = "user", content = reportText)
        )
    )

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.getApi(context).generateChatCompletion(chatRequest)

            withContext(Dispatchers.Main) {
                onResult(response.content)
            }
        } catch (e: IOException) { // Catch only network errors (IOException)
            Log.e("AiApi", "Network error calling AI API", e)
            withContext(Dispatchers.Main) {
                // Retourner un message d'erreur via la lambda
                onResult("Erreur réseau : ${e.message}")
            }
        } catch (e: Exception) { // Let other exceptions propagate
            Log.e("AiApi", "Unexpected error calling AI API", e)
            throw e // Re-throw the exception
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

