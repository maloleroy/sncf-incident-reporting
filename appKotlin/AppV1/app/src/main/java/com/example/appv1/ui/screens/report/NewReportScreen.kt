package com.example.appv1.ui.screens.report


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.appv1.api.IncidentAnalysisRequest
import com.example.appv1.api.IncidentAnalysisResponse
import com.example.appv1.api.RetrofitInstance
import com.example.appv1.api.getIncidentAnalysis
import com.example.appv1.ui.components.NewReportScreenDivider
import com.example.appv1.ui.components.showErrorDialog
import com.example.appv1.ui.util.launchSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen(
    onBack: () -> Unit,
    onSuccess: (IncidentAnalysisResponse, String, String) -> Unit
) {
    val context = LocalContext.current
    rememberCoroutineScope() // Coroutine scope pour les appels asynchrones

    var trainType by remember { mutableStateOf("") }
    var trainCar by remember { mutableStateOf("") }
    var numberOfSeat by remember { mutableStateOf("") }
    var transcription by remember { mutableStateOf("") }
    var generatedReportText by remember { mutableStateOf<String?>(null) }
   

    var isOnlineAILoading by remember { mutableStateOf(false) }

    val trainTypes = mapOf(
        "Dasye" to "DASYE_eau_incidents",
        "DUPLEX WC chimique" to "DUPLEX_WC_chimique_incidents",
        "DUPLEX WC EAU" to "DUPLEX_WC_EAU_incidents",
        "NEODUPLEX chimique" to "NEODUPLEX_chimique_incidents",
        "OCEANE LIKE" to "OCEANE_LIKE_incidents",
        "OUIGO1" to "OUIGO1_incidents",
        "OUIGO2" to "OUIGO2_incidents",
        "PLT" to "PLT_incidents",
        "POS" to "POS_incidents",
        "P DUPLEX" to "P_DUPLEX_incidents",
        "RDOM" to "RDOM_incidents",
        "RITA" to "RITA_incidents",
        "TANGO" to "TANGO_incidents",
        "TGV R TRI FO" to "TGV_R_TRI_FO_incidents",
        "TGV R TRI" to "TGV_R_TRI_incidents",
        "TRAIN 2N2 3UA LYRIA" to "TRAIN_2N2_3UA_LYRIA_incidents",
        "TRAIN 2N2 3UA" to "TRAIN_2N2_3UA_incidents",
        "TRAIN 2N2 3UFC" to "TRAIN_2N2_3UFC_incidents",
        "TRAIN 2N2 3UF" to "TRAIN_2N2_3UF_incidents",
        "TRAIN 2N2 3UH" to "TRAIN_2N2_3UH_incidents"
    )

    /* ---------- 1) launcher pour l’Intent de reconnaissance vocale ---------- */
    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )
            if (!matches.isNullOrEmpty()) {
                transcription = matches[0]
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
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = trainType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type de train") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    trainTypes.keys.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                trainType = type
                                expanded = false
                            }
                        )
                    }
                }
            }
            // Champ pour le numéro de rame
            OutlinedTextField(
                value = trainCar,
                onValueChange = { trainCar = it },
                label = { Text("Numéro de rame") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

                        // Champ pour le numéro de siège
            OutlinedTextField(
                value = numberOfSeat,
                onValueChange = { numberOfSeat = it },
                label = { Text("Numéro de siège") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            
            OutlinedTextField(
                value = transcription,
                onValueChange = { transcription = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = { Text("Décrivez votre signalement ici…") },
                label = { Text("Description initiale") } // Ajout d'un label

            )

            Spacer(Modifier.height(8.dp)) // Espace entre Annuler et Valider

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
                    .height(64.dp)
            ) {
                Icon(Icons.Filled.Mic, contentDescription = "Speech to Text")
            }
            Spacer(Modifier.height(56.dp))
            Button(
                // ----> 3. Mettre à jour l'appel onClick <----
                onClick = {
                    if (transcription.isNotBlank() && trainType.isNotBlank() && trainCar.isNotBlank()) {
                        isOnlineAILoading = true // Début chargement
                        generatedReportText = null // Réinitialiser l'ancienne réponse
                        getIncidentAnalysis(
                            IncidentAnalysisRequest(
                                trainTypes[trainType]!!,
                                trainCar,
                                transcription
                            ),
                            context,
                            onResult = { result ->
                                generatedReportText = result.failure
                                isOnlineAILoading = false
                                onSuccess(result, trainType, trainCar)
                            },
                            onError = { errorMessage ->
                                isOnlineAILoading = false
                                showErrorDialog(context, errorMessage)
                            }
                        )
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
            } else if (generatedReportText != null) { // Cette partie pourrait être enlevée si on navigue directement
                NewReportScreenDivider()
                Text("Rapport généré par l'IA :", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(generatedReportText!!)
            }
        }
    }
}

private fun getIncidentAnalysis(
    incidentAnalysisRequest: IncidentAnalysisRequest,
    context: Context,
    onResult: (IncidentAnalysisResponse) -> Unit,
    onError: (String) -> Unit,
) {
    if (incidentAnalysisRequest.trainType.isBlank()) {
        Toast.makeText(context, "Veuillez préciser le train", Toast.LENGTH_SHORT).show()
        return
    }
    if (incidentAnalysisRequest.trainCar.isBlank()) {
        Toast.makeText(context, "Veuillez préciser le numéro de rame", Toast.LENGTH_SHORT).show()
        return
    }

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.getIncidentApiService(context)
                .generateIncidentAnalysis(incidentAnalysisRequest)
            withContext(Dispatchers.Main) {
                onResult(response)
            }
        } catch (e: IOException) { // Catch only network errors (IOException)
            withContext(Dispatchers.Main) {
                onError("Erreur de réseau lors de l'appel API : ${e.message}")
            }
        } catch (e: Exception) { // Catch other exceptions with a generic message
            withContext(Dispatchers.Main) {
                onError("Erreur inattendue lors de l'appel API : ${e.message}")
            }
        }
    }
}
