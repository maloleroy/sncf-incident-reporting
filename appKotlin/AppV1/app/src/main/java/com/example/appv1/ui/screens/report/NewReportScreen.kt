package com.example.appv1.ui.screens.report

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState // Pour rendre la colonne scrollable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme // <-- AJOUTER CET IMPORT
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect // Assurez-vous que cet import est présent
import androidx.compose.runtime.rememberCoroutineScope // Assurez-vous que cet import est présent
import kotlinx.coroutines.launch // Assurez-vous que cet import est présent
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.appv1.GemmaEngine
import com.example.appv1.api.ChatMessage
import com.example.appv1.api.ChatRequest
import com.example.appv1.api.IncidentObjectsListRequest
import com.example.appv1.api.RetrofitInstance
import com.example.appv1.ui.components.NewReportScreenDivider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import com.example.appv1.ui.util.launchSpeech

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    rememberCoroutineScope() // Coroutine scope pour les appels asynchrones

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
                        doSomething(reportText, trainType, trainNumber, context) { result ->
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
                shape = RoundedCornerShape(15.dp), // Ajuste la valeur pour plus ou moins d’arrondi
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isOnlineAILoading // Désactiver pendant le chargement
            ) {
                Text(
                    "Valider",
                    fontSize = 16.sp // Choisis la taille que tu veux, par exemple 20.sp
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Valider",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(16.dp)
                )
            }

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

private fun doSomething(
    reportText: String,
    trainType: String,
    trainNumber: String,
    context: Context,
    onResult: (String) -> Unit // Lambda pour retourner le résultat
) {
    if (trainType.isBlank()) {
        Toast.makeText(context, "Veuillez préciser le train", Toast.LENGTH_SHORT).show()
        return
    }
    if (trainNumber.isBlank()) {
        Toast.makeText(context, "Veuillez préciser le numéro de rame", Toast.LENGTH_SHORT).show()
        return
    }

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val objs = RetrofitInstance.getIncidentObjectsListApiService(context)
                .getObjectsList(IncidentObjectsListRequest(trainType, trainNumber))
            val items = objs.objects.map { it[2] }

            withContext(Dispatchers.Main) {
                sendReportToAI(reportText, items, context, onResult)
            }
        } catch (e: IOException) { // Catch only network errors (IOException)
            Toast.makeText(context, "Network error calling AI API ${e.message}", Toast.LENGTH_SHORT).show()
            withContext(Dispatchers.Main) {
                // Retourner un message d'erreur via la lambda
                sendReportToAI(reportText, emptyList(), context, onResult)
            }
        } catch (e: Exception) { // Let other exceptions propagate
            Toast.makeText(context, "Unexpected error calling AI API ${e.message}", Toast.LENGTH_SHORT).show()
            throw e // Re-throw the exception
        }
    }
}

private fun sendReportToAI(
    reportText: String,
    items: List<String>,
    context: Context,
    onResult: (String) -> Unit // Lambda pour retourner le résultat
) {
    if (reportText.isBlank()) {
        Toast.makeText(context, "Le texte du rapport est vide", Toast.LENGTH_SHORT).show()
        return
    }

    // Construire la requête pour Chat API
    val chatRequest = ChatRequest(
        messages = listOf(
            ChatMessage(
                role = "system",
                content = "Tu es un assistant chargé d'analyser des transcriptions audio d'agents SNCF pour identifier l'objet concerné par l'incident."
            ),
            ChatMessage(
                role = "user", content = """Voici les objets possibles :
${items.joinToString("\n")}

Transcription :
${reportText}

⚠️ Réponds uniquement par l'incident qui se rapproche le plus du signalement.
Si tu n'es pas sûr, écris : Je ne sais pas."""
            )
        )
    )

    CoroutineScope(Dispatchers.IO).launch {
        try {
            //val objs = RetrofitInstance.getIncidentObjectsListApiService(context).getObjectsList(IncidentObjectsListRequest(trainType, trainNumber))
            //val response = RetrofitInstance.getChatApiService(context).generateChatCompletion(chatRequest)
            val response = RetrofitInstance.getChatApiService()

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
