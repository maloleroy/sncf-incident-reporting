package com.example.appv1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appv1.ui.screens.home.HomeScreen
import com.example.appv1.ui.screens.NewReportScreen
import com.example.appv1.ui.theme.AppV1Theme

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
            ChatMessage(role = "system", content = "Tu es un assistant SNCF qui reformule les signalements des chefs de bord."), // Instruction système (optionnel)
            ChatMessage(role = "user", content = reportText) // Le texte du chef de bord
        )
    )

    // Utilisation de CoroutineScope pour effectuer l'appel réseau
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Appeler le nouvel endpoint
            val response = RetrofitInstance.api.generateChatCompletion(chatRequest)

            // Traiter la réponse (prendre le premier choix par exemple)
            val generatedText = response.choices.firstOrNull()?.message?.content ?: "Aucune réponse générée."

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
