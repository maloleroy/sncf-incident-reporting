package com.example.appv1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appv1.ui.theme.AppV1Theme
import java.util.*
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppV1Theme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            onNewReport = { navController.navigate("new_report") },
                            onViewReports = { /* TODO: lister les rapports */ }
                        )
                    }
                    composable("new_report") {
                        NewReportScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNewReport: () -> Unit,
    onViewReports: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("SNCF Signalements", fontSize = 20.sp) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewReport) {
                Icon(Icons.Default.Add, contentDescription = "Nouveau signalement")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Bienvenue, Chef de bord",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Button(
                onClick = onNewReport,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Nouveau signalement")
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onViewReports,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Consulter les signalements")
            }
        }
    }
}

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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
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
        }
    }
}

/* ---------- Fonction utilitaire pour lancer l’intent ---------- */
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

