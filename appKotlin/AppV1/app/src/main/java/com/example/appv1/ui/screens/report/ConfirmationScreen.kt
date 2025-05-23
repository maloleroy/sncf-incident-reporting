package com.example.appv1.ui.screens.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.appv1.api.IncidentAnalysisResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    response: IncidentAnalysisResponse?,
    onBack: () -> Unit,
) {
    if (response == null) {
        // Affichage d'un message d'erreur ou d'un loader si besoin
        Text("Aucune donnée à afficher.")
        return
    }
    // États locaux pour chaque champ modifiable
    val (location, setLocation) = remember { mutableStateOf(response.location) }
    val (category, setCategory) = remember { mutableStateOf(response.category) }
    val (system, setSystem) = remember { mutableStateOf(response.system) }
    val (precision1, setPrecision1) = remember { mutableStateOf(response.precision1) }
    val (precision2, setPrecision2) = remember { mutableStateOf(response.precision2) }
    val (precision3, setPrecision3) = remember { mutableStateOf(response.precision3) }
    val (subSystem, setSubSystem) = remember { mutableStateOf(response.subSystem) }
    val (failure, setFailure) = remember { mutableStateOf(response.failure) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmation incident") },
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Vérifiez et modifiez les informations de l'incident :",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = location,
                onValueChange = setLocation,
                label = { Text("Lieu") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
            OutlinedTextField(
                value = precision1,
                onValueChange = setPrecision1,
                label = { Text("Précision 1") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
            OutlinedTextField(
                value = category,
                onValueChange = setCategory,
                label = { Text("Catégorie") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
            OutlinedTextField(
                value = precision2,
                onValueChange = setPrecision2,
                label = { Text("Précision 2") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
            OutlinedTextField(
                value = system,
                onValueChange = setSystem,
                label = { Text("Système") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
            OutlinedTextField(
                value = precision3,
                onValueChange = setPrecision3,
                label = { Text("Précision 3") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
            OutlinedTextField(
                value = subSystem,
                onValueChange = setSubSystem,
                label = { Text("Sous-système") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
            OutlinedTextField(
                value = failure,
                onValueChange = setFailure,
                label = { Text("Défaillance") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { /* TODO: callback de sauvegarde ou validation */ }) {
                Text("Confirmer l'incident")
            }
        }
    }
}
