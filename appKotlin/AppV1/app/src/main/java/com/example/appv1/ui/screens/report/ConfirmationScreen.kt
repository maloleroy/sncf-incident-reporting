package com.example.appv1.ui.screens.report

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appv1.api.IncidentAnalysisResponse
import com.example.appv1.api.submitIncident
import com.example.appv1.ui.components.showErrorDialog
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    response: IncidentAnalysisResponse?,
    onBack: () -> Unit,
    // Ajoute un callback pour charger dynamiquement les options
    loadOptions: suspend (level: String, selections: Map<String, String>) -> List<String> = { _, _ -> emptyList() }
) {
    val context = LocalContext.current
    if (response == null) {
        // Affichage d'un message d'erreur ou d'un loader si besoin
        Text("Aucune donnée à afficher.")
        return
    }
    val scope = rememberCoroutineScope()
    // États pour chaque champ et leurs options
    var location by remember { mutableStateOf(response.location) }
    var locationOptions by remember { mutableStateOf(listOf(response.location)) }
    var category by remember { mutableStateOf(response.category) }
    var categoryOptions by remember { mutableStateOf(listOf(response.category)) }
    var system by remember { mutableStateOf(response.system) }
    var systemOptions by remember { mutableStateOf(listOf(response.system)) }
    var precision1 by remember { mutableStateOf(response.precision1) }
    var precision1Options by remember { mutableStateOf(listOf(response.precision1)) }
    var precision2 by remember { mutableStateOf(response.precision2) }
    var precision2Options by remember { mutableStateOf(listOf(response.precision2)) }
    var precision3 by remember { mutableStateOf(response.precision3) }
    var precision3Options by remember { mutableStateOf(listOf(response.precision3)) }
    var subSystem by remember { mutableStateOf(response.subSystem) }
    var subSystemOptions by remember { mutableStateOf(listOf(response.subSystem)) }
    var failure by remember { mutableStateOf(response.failure) }
    var failureOptions by remember { mutableStateOf(listOf(response.failure)) }

    // Dropdown states
    var expandedLocation by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedSystem by remember { mutableStateOf(false) }
    var expandedPrecision1 by remember { mutableStateOf(false) }
    var expandedPrecision2 by remember { mutableStateOf(false) }
    var expandedPrecision3 by remember { mutableStateOf(false) }
    var expandedSubSystem by remember { mutableStateOf(false) }
    var expandedFailure by remember { mutableStateOf(false) }

    // Chargement dynamique des options à chaque ouverture de dropdown
    fun resetBelow(level: String) {
        when (level) {
            "location" -> {
                category = ""; categoryOptions = listOf()
                system = ""; systemOptions = listOf()
                precision1 = ""; precision1Options = listOf()
                precision2 = ""; precision2Options = listOf()
                precision3 = ""; precision3Options = listOf()
                subSystem = ""; subSystemOptions = listOf()
                failure = ""; failureOptions = listOf()
            }
            "category" -> {
                system = ""; systemOptions = listOf()
                precision1 = ""; precision1Options = listOf()
                precision2 = ""; precision2Options = listOf()
                precision3 = ""; precision3Options = listOf()
                subSystem = ""; subSystemOptions = listOf()
                failure = ""; failureOptions = listOf()
            }
            "system" -> {
                precision1 = ""; precision1Options = listOf()
                precision2 = ""; precision2Options = listOf()
                precision3 = ""; precision3Options = listOf()
                subSystem = ""; subSystemOptions = listOf()
                failure = ""; failureOptions = listOf()
            }
            "precision1" -> {
                precision2 = ""; precision2Options = listOf()
                precision3 = ""; precision3Options = listOf()
                subSystem = ""; subSystemOptions = listOf()
                failure = ""; failureOptions = listOf()
            }
            "precision2" -> {
                precision3 = ""; precision3Options = listOf()
                subSystem = ""; subSystemOptions = listOf()
                failure = ""; failureOptions = listOf()
            }
            "precision3" -> {
                subSystem = ""; subSystemOptions = listOf()
                failure = ""; failureOptions = listOf()
            }
            "subSystem" -> {
                failure = ""; failureOptions = listOf()
            }
            // Ajoute d'autres niveaux si nécessaire
        }
    }

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
            // LOCATION DROPDOWN
            ExposedDropdownMenuBox(
                expanded = expandedLocation,
                onExpandedChange = {
                    expandedLocation = !expandedLocation
                    if (expandedLocation) {
                        scope.launch {
                            locationOptions = loadOptions("location", mapOf())
                        }
                    }
                }
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Lieu") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLocation) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth().padding(vertical = 4.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedLocation,
                    onDismissRequest = { expandedLocation = false }
                ) {
                    locationOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                location = option
                                expandedLocation = false
                                resetBelow("location")
                            }
                        )
                    }
                }
            }
            // CATEGORY DROPDOWN
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = {
                    expandedCategory = !expandedCategory
                    if (expandedCategory && location.isNotBlank()) {
                        scope.launch {
                            categoryOptions = loadOptions("category", mapOf("location" to location))
                        }
                    }
                }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Catégorie") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth().padding(vertical = 4.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categoryOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                category = option
                                expandedCategory = false
                                resetBelow("category")
                            }
                        )
                    }
                }
            }
            // SYSTEM DROPDOWN
            ExposedDropdownMenuBox(
                expanded = expandedSystem,
                onExpandedChange = {
                    expandedSystem = !expandedSystem
                    if (expandedSystem && category.isNotBlank()) {
                        scope.launch {
                            systemOptions = loadOptions("system", mapOf("category" to category, "location" to location))
                        }
                    }
                }
            ) {
                OutlinedTextField(
                    value = system,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Système") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSystem) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth().padding(vertical = 4.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedSystem,
                    onDismissRequest = { expandedSystem = false }
                ) {
                    systemOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                system = option
                                expandedSystem = false
                                resetBelow("system")
                            }
                        )
                    }
                }
            }
            // PRECISION 1 DROPDOWN
            ExposedDropdownMenuBox(
                expanded = expandedPrecision1,
                onExpandedChange = {
                    expandedPrecision1 = !expandedPrecision1
                    if (expandedPrecision1 && system.isNotBlank()) {
                        scope.launch {
                            precision1Options = loadOptions("precision1", mapOf("system" to system, "category" to category, "location" to location))
                        }
                    }
                }
            ) {
                OutlinedTextField(
                    value = precision1,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Précision 1") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPrecision1) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth().padding(vertical = 4.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedPrecision1,
                    onDismissRequest = { expandedPrecision1 = false }
                ) {
                    precision1Options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                precision1 = option
                                expandedPrecision1 = false
                                resetBelow("precision1")
                            }
                        )
                    }
                }
            }
            // PRECISION 2 DROPDOWN
            ExposedDropdownMenuBox(
                expanded = expandedPrecision2,
                onExpandedChange = {
                    expandedPrecision2 = !expandedPrecision2
                    if (expandedPrecision2 && precision1.isNotBlank()) {
                        scope.launch {
                            precision2Options = loadOptions("precision2", mapOf("precision1" to precision1, "system" to system, "category" to category, "location" to location))
                        }
                    }
                }
            ) {
                OutlinedTextField(
                    value = precision2,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Précision 2") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPrecision2) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth().padding(vertical = 4.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedPrecision2,
                    onDismissRequest = { expandedPrecision2 = false }
                ) {
                    precision2Options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                precision2 = option
                                expandedPrecision2 = false
                                resetBelow("precision2")
                            }
                        )
                    }
                }
            }
            // PRECISION 3 DROPDOWN
            ExposedDropdownMenuBox(
                expanded = expandedPrecision3,
                onExpandedChange = {
                    expandedPrecision3 = !expandedPrecision3
                    if (expandedPrecision3 && precision2.isNotBlank()) {
                        scope.launch {
                            precision3Options = loadOptions("precision3", mapOf("precision2" to precision2, "precision1" to precision1, "system" to system, "category" to category, "location" to location))
                        }
                    }
                }
            ) {
                OutlinedTextField(
                    value = precision3,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Précision 3") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPrecision3) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth().padding(vertical = 4.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedPrecision3,
                    onDismissRequest = { expandedPrecision3 = false }
                ) {
                    precision3Options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                precision3 = option
                                expandedPrecision3 = false
                                resetBelow("precision3")
                            }
                        )
                    }
                }
            }
            // SUB-SYSTEM DROPDOWN
            ExposedDropdownMenuBox(
                expanded = expandedSubSystem,
                onExpandedChange = {
                    expandedSubSystem = !expandedSubSystem
                    if (expandedSubSystem && precision3.isNotBlank()) {
                        scope.launch {
                            subSystemOptions = loadOptions("subSystem", mapOf("precision3" to precision3, "precision2" to precision2, "precision1" to precision1, "system" to system, "category" to category, "location" to location))
                        }
                    }
                }
            ) {
                OutlinedTextField(
                    value = subSystem,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sous-système") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubSystem) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth().padding(vertical = 4.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedSubSystem,
                    onDismissRequest = { expandedSubSystem = false }
                ) {
                    subSystemOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                subSystem = option
                                expandedSubSystem = false
                                resetBelow("subSystem")
                            }
                        )
                    }
                }
            }
            // FAILURE DROPDOWN
            ExposedDropdownMenuBox(
                expanded = expandedFailure,
                onExpandedChange = {
                    expandedFailure = !expandedFailure
                    if (expandedFailure && subSystem.isNotBlank()) {
                        scope.launch {
                            failureOptions = loadOptions("failure", mapOf("subSystem" to subSystem, "precision3" to precision3, "precision2" to precision2, "precision1" to precision1, "system" to system, "category" to category, "location" to location))
                        }
                    }
                }
            ) {
                OutlinedTextField(
                    value = failure,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Défaillance") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFailure) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth().padding(vertical = 4.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedFailure,
                    onDismissRequest = { expandedFailure = false }
                ) {
                    failureOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                failure = option
                                expandedFailure = false
                            }
                        )
                    }
                }
            }
            // Ajoute un bouton de sauvegarde ou de validation si besoin
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                // Soumettre les données modifiées
                submitIncident(
                    IncidentAnalysisResponse(
                        location = location,
                        category = category,
                        system = system,
                        precision1 = precision1,
                        precision2 = precision2,
                        precision3 = precision3,
                        subSystem = subSystem,
                        failure = failure
                    ),
                    context,
                    onResult = { response ->
                        // Gérer la réponse de l'API
                        if (response.status == "success") {
                            // Afficher un message de succès ou naviguer ailleurs
                            onBack()
                        } else {
                            showErrorDialog(context, "Erreur lors de la soumission : ${response.status}")
                        }
                    },
                    onError = { errorMessage ->
                        // Afficher un message d'erreur
                    }
                )
            }) {
                Text("Sauvegarder les modifications")
            }
        }
    }
}
