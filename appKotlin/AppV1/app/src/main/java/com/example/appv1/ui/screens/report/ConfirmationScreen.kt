package com.example.appv1.ui.screens.report

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appv1.api.IncidentAnalysisResponse
import com.example.appv1.ui.components.showErrorDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    response: IncidentAnalysisResponse?,
    onBack: () -> Unit,
    trainType: String,
    car: String,
    loadOptions: suspend (level: String, selections: Map<String, String>) -> List<String> = { _, _ -> emptyList() }
) {
    val context = LocalContext.current
    if (response == null) {
        Text("Aucune donnée à afficher.")
        return
    }
    val scope = rememberCoroutineScope()

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

    var expandedLocation by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedSystem by remember { mutableStateOf(false) }
    var expandedPrecision1 by remember { mutableStateOf(false) }
    var expandedPrecision2 by remember { mutableStateOf(false) }
    var expandedPrecision3 by remember { mutableStateOf(false) }
    var expandedSubSystem by remember { mutableStateOf(false) }
    var expandedFailure by remember { mutableStateOf(false) }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DropdownField("Lieu", location, locationOptions, expandedLocation, {
                expandedLocation = it
                if (it) scope.launch { locationOptions = loadOptions("location", mapOf()) }
            }) {
                location = it; resetBelow("location")
            }

            DropdownField("Catégorie", category, categoryOptions, expandedCategory, {
                expandedCategory = it
                if (it && location.isNotBlank()) scope.launch {
                    categoryOptions = loadOptions("category", mapOf("location" to location))
                }
            }) {
                category = it; resetBelow("category")
            }

            DropdownField("Système", system, systemOptions, expandedSystem, {
                expandedSystem = it
                if (it && category.isNotBlank()) scope.launch {
                    systemOptions = loadOptions("system", mapOf("location" to location, "category" to category))
                }
            }) {
                system = it; resetBelow("system")
            }

            DropdownField("Précision 1", precision1, precision1Options, expandedPrecision1, {
                expandedPrecision1 = it
                if (it && system.isNotBlank()) scope.launch {
                    precision1Options = loadOptions("precision1", mapOf(
                        "location" to location, "category" to category, "system" to system
                    ))
                }
            }) {
                precision1 = it; resetBelow("precision1")
            }

            DropdownField("Précision 2", precision2, precision2Options, expandedPrecision2, {
                expandedPrecision2 = it
                if (it && precision1.isNotBlank()) scope.launch {
                    precision2Options = loadOptions("precision2", mapOf(
                        "location" to location, "category" to category, "system" to system, "precision1" to precision1
                    ))
                }
            }) {
                precision2 = it; resetBelow("precision2")
            }

            DropdownField("Précision 3", precision3, precision3Options, expandedPrecision3, {
                expandedPrecision3 = it
                if (it && precision2.isNotBlank()) scope.launch {
                    precision3Options = loadOptions("precision3", mapOf(
                        "location" to location, "category" to category, "system" to system,
                        "precision1" to precision1, "precision2" to precision2
                    ))
                }
            }) {
                precision3 = it; resetBelow("precision3")
            }

            DropdownField("Sous-système", subSystem, subSystemOptions, expandedSubSystem, {
                expandedSubSystem = it
                if (it && precision3.isNotBlank()) scope.launch {
                    subSystemOptions = loadOptions("subSystem", mapOf(
                        "location" to location, "category" to category, "system" to system,
                        "precision1" to precision1, "precision2" to precision2, "precision3" to precision3
                    ))
                }
            }) {
                subSystem = it; resetBelow("subSystem")
            }

            DropdownField("Défaillance", failure, failureOptions, expandedFailure, {
                expandedFailure = it
                if (it && subSystem.isNotBlank()) scope.launch {
                    failureOptions = loadOptions("failure", mapOf(
                        "location" to location, "category" to category, "system" to system,
                        "precision1" to precision1, "precision2" to precision2, "precision3" to precision3,
                        "subSystem" to subSystem
                    ))
                }
            }) {
                failure = it
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}