@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.appv1.ui.screens.report

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appv1.api.ConservedInformations
import com.example.appv1.api.IncidentAnalysisResponse
import com.example.appv1.api.IncidentCompletionRequest
import com.example.appv1.api.RetrofitInstance
import com.example.appv1.domain.model.trainTypes
import com.example.appv1.ui.components.showErrorDialog
import com.example.appv1.ui.components.SubmitIncidentButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    response: IncidentAnalysisResponse?,
    onBack: () -> Unit,
    trainType: String,
    trainCar: String,
    navigateToNewReport: () -> Unit // Added this parameter
) {
    val context = LocalContext.current
    if (response == null) {
        Text("Aucune donnée à afficher.")
        return
    }
    val scope = rememberCoroutineScope()
    val loadOptions: (String, Map<String, String>) -> List<String> = { level, selections ->
        val options = mutableListOf<String>()
        loadOptionsWithContext(
            scope,
            context,
            trainType,
            trainCar,
            level,
            selections,
            onSuccess = { options.addAll(it) },
            onError = { error -> showErrorDialog(context, "Erreur lors du chargement des options : $error") }
        )
        options
    }

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
            "precision1" -> {
                category = ""; categoryOptions = listOf()
                system = ""; systemOptions = listOf()
                precision2 = ""; precision2Options = listOf()
                precision3 = ""; precision3Options = listOf()
                subSystem = ""; subSystemOptions = listOf()
                failure = ""; failureOptions = listOf()
            }
            "category" -> {
                system = ""; systemOptions = listOf()
                precision2 = ""; precision2Options = listOf()
                precision3 = ""; precision3Options = listOf()
                subSystem = ""; subSystemOptions = listOf()
                failure = ""; failureOptions = listOf()
            }
            "system" -> {
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DropdownField(
                label = "Lieu",
                value = location,
                options = locationOptions,
                expanded = expandedLocation,
                onExpandedChange = { isExpanded ->
                    expandedLocation = isExpanded
                    if (isExpanded && locationOptions.isEmpty()) {
                        scope.launch {
                            try {
                                val result = loadOptionsSuspend(context, trainType, trainCar, "location", mapOf())
                                locationOptions = result
                            } catch (e: Exception) {
                                showErrorDialog(context, e.message ?: "Erreur inconnue")
                            }
                        }
                    }
                },
                onSelected = {
                    location = it
                    resetBelow("location")
                }
            )

            DropdownField(
                label = "Précision 1",
                value = precision1,
                options = precision1Options,
                expanded = expandedPrecision1,
                onExpandedChange = { expanded ->
                    expandedPrecision1 = expanded
                    if (expanded && system.isNotBlank()) {
                        scope.launch {
                            precision1Options = loadOptionsSuspend(
                                context,
                                trainType,
                                trainCar,
                                "precision1",
                                mapOf(
                                    "location" to location,
                                    "category" to category,
                                    "system" to system
                                )
                            )
                        }
                    }
                },
                onSelected = {
                    precision1 = it
                    resetBelow("precision1")
                },
                onRequestOptions = { emptyList() } // ou null si paramètre nullable
            )

            DropdownField(
                label = "Catégorie",
                value = category,
                options = categoryOptions,
                expanded = expandedCategory,
                onExpandedChange = { isExpanded ->
                    expandedCategory = isExpanded
                    if (isExpanded && categoryOptions.isEmpty() && location.isNotBlank()) {
                        scope.launch {
                            try {
                                val result = loadOptionsSuspend(context, trainType, trainCar, "category", mapOf("location" to location))
                                categoryOptions = result
                            } catch (e: Exception) {
                                showErrorDialog(context, e.message ?: "Erreur inconnue")
                            }
                        }
                    }
                },
                onSelected = {
                    category = it
                    resetBelow("category")
                }
            )
            

            DropdownField(
                label = "Système",
                value = system,
                options = systemOptions,
                expanded = expandedSystem,
                onExpandedChange = { expanded ->
                    expandedSystem = expanded
                    if (expanded && category.isNotBlank()) {
                        scope.launch {
                            systemOptions = loadOptionsSuspend(context, trainType, trainCar, "system", mapOf("location" to location, "category" to category))
                        }
                    }
                },
                onSelected = {
                    system = it
                    resetBelow("system")
                },
                onRequestOptions = { emptyList() } // ou null si paramètre nullable, car on charge via onExpandedChange
            )

            DropdownField(
                label = "Précision 2",
                value = precision2,
                options = precision2Options,
                expanded = expandedPrecision2,
                onExpandedChange = { expanded ->
                    expandedPrecision2 = expanded
                    if (expanded && precision1.isNotBlank()) {
                        scope.launch {
                            precision2Options = loadOptionsSuspend(
                                context,
                                trainType,
                                trainCar,
                                "precision2",
                                mapOf(
                                    "location" to location,
                                    "category" to category,
                                    "system" to system,
                                    "precision1" to precision1
                                )
                            )
                        }
                    }
                },
                onSelected = {
                    precision2 = it
                    resetBelow("precision2")
                },
                onRequestOptions = { emptyList() }
            )

            DropdownField(
                label = "Précision 3",
                value = precision3,
                options = precision3Options,
                expanded = expandedPrecision3,
                onExpandedChange = { expanded ->
                    expandedPrecision3 = expanded
                    if (expanded && precision2.isNotBlank()) {
                        scope.launch {
                            precision3Options = loadOptionsSuspend(
                                context,
                                trainType,
                                trainCar,
                                "precision3",
                                mapOf(
                                    "location" to location,
                                    "category" to category,
                                    "system" to system,
                                    "precision1" to precision1,
                                    "precision2" to precision2
                                )
                            )
                        }
                    }
                },
                onSelected = {
                    precision3 = it
                    resetBelow("precision3")
                },
                onRequestOptions = { emptyList() }
            )

            DropdownField(
                label = "Sous-système",
                value = subSystem,
                options = subSystemOptions,
                expanded = expandedSubSystem,
                onExpandedChange = { expanded ->
                    expandedSubSystem = expanded
                    if (expanded && precision3.isNotBlank()) {
                        scope.launch {
                            subSystemOptions = loadOptionsSuspend(
                                context,
                                trainType,
                                trainCar,
                                "subSystem",
                                mapOf(
                                    "location" to location,
                                    "category" to category,
                                    "system" to system,
                                    "precision1" to precision1,
                                    "precision2" to precision2,
                                    "precision3" to precision3
                                )
                            )
                        }
                    }
                },
                onSelected = {
                    subSystem = it
                    resetBelow("subSystem")
                },
                onRequestOptions = { emptyList() }
            )

            DropdownField(
                label = "Défaillance",
                value = failure,
                options = failureOptions,
                expanded = expandedFailure,
                onExpandedChange = { expanded ->
                    expandedFailure = expanded
                    if (expanded && subSystem.isNotBlank()) {
                        scope.launch {
                            failureOptions = loadOptionsSuspend(
                                context,
                                trainType,
                                trainCar,
                                "failure",
                                mapOf(
                                    "location" to location,
                                    "category" to category,
                                    "system" to system,
                                    "precision1" to precision1,
                                    "precision2" to precision2,
                                    "precision3" to precision3,
                                    "subSystem" to subSystem
                                )
                            )
                        }
                    }
                },
                onSelected = {
                    failure = it
                },
                onRequestOptions = { emptyList() }
            )

            // Pass navigateToNewReport to SubmitIncidentButton
            val incidentToSubmit = IncidentAnalysisResponse(
                location = location,
                category = category,
                system = system,
                precision1 = precision1,
                precision2 = precision2,
                precision3 = precision3,
                subSystem = subSystem,
                failure = failure
                // Removed options and completed as they are not in the data class
            )

            SubmitIncidentButton(
                scope = scope,
                context = context,
                incident = incidentToSubmit,
                onSubmissionSuccess = navigateToNewReport, // Keep this as it was intended
                modifier = Modifier.fillMaxWidth() // Keep this as it was intended
            )


        }
    }
}
@Composable
fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelected: (String) -> Unit,
    onRequestOptions: () -> List<String> = { emptyList() } // Optionnel, pas forcément suspend ici
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



fun loadOptionsWithContext(
    scope: CoroutineScope,
    context: Context,
    trainType: String,
    trainCar: String,
    level: String,
    selections: Map<String, String>,
    onSuccess: (List<String>) -> Unit,
    onError: (String) -> Unit = { error -> showErrorDialog(context, "Erreur lors du chargement des options : ${error}") }
) {
    scope.launch {
        try {
            onSuccess(
                RetrofitInstance.getCompletionApiService(context).findCompletion(
                    IncidentCompletionRequest(
                        trainTypes[trainType]!!,
                        trainCar,
                        level,
                        ConservedInformations(
                            location = selections["location"],
                            category = selections["category"],
                            system = selections["system"],
                            precision1 = selections["precision1"],
                            precision2 = selections["precision2"],
                            precision3 = selections["precision3"],
                            subSystem = selections["subSystem"],
                            failure = selections["failure"]
                        )
                    )
                ).options
            )
        } catch (e: retrofit2.HttpException) {
            withContext(Dispatchers.Main) {
                onError("Erreur HTTP ${e.code()}")
            }
        } catch (e: IOException) {
            withContext(Dispatchers.Main) {
                onError("Erreur réseau : ${e.message}")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Erreur inattendue : ${e.message}")
            }
        }
    }
}
suspend fun loadOptionsSuspend(
    context: Context,
    trainType: String,
    trainCar: String,
    level: String,
    selections: Map<String, String>
): List<String> {
    return try {
        RetrofitInstance.getCompletionApiService(context).findCompletion(
            IncidentCompletionRequest(
                trainTypes[trainType]!!,
                trainCar,
                level,
                ConservedInformations(
                    location = selections["location"],
                    category = selections["category"],
                    system = selections["system"],
                    precision1 = selections["precision1"],
                    precision2 = selections["precision2"],
                    precision3 = selections["precision3"],
                    subSystem = selections["subSystem"],
                    failure = selections["failure"]
                )
            )
        ).options
    } catch (e: retrofit2.HttpException) {
        throw Exception("Erreur HTTP ${e.code()}")
    } catch (e: IOException) {
        throw Exception("Erreur réseau : ${e.message}")
    } catch (e: Exception) {
        throw Exception("Erreur inattendue : ${e.message}")
    }
}
