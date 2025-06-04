@file:OptIn(ExperimentalMaterial3Api::class)
package com.sncf.reports.ui.screens.report

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
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
import com.sncf.reports.model.ConservedInformations
import com.sncf.reports.model.IncidentAnalysisResponse
import com.sncf.reports.model.IncidentCompletionRequest
import com.sncf.reports.api.RetrofitInstance
import com.sncf.reports.data.IncidentSynchronizer
import com.sncf.reports.model.trainTypes
import com.sncf.reports.ui.components.showErrorDialog
import com.sncf.reports.ui.components.SubmitIncidentButton
import kotlinx.coroutines.launch
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    response: IncidentAnalysisResponse?,
    onBack: () -> Unit,
    trainType: String,
    trainCar: String,
    seatNumber: Int?,
    navigateToNewReport: () -> Unit // Added this parameter
) {
    val context = LocalContext.current
    if (response == null) {
        Text("Aucune donnée à afficher.")
        return
    }
    val scope = rememberCoroutineScope()

    var location by remember { mutableStateOf(response.location) }
    var locationOptions by remember { mutableStateOf(listOf(response.location)) }
    var precision1 by remember { mutableStateOf(response.precision1) }
    var precision1Options by remember { mutableStateOf(listOf(response.precision1)) }
    var category by remember { mutableStateOf(response.category) }
    var categoryOptions by remember { mutableStateOf(listOf(response.category)) }
    var precision2 by remember { mutableStateOf(response.precision2) }
    var precision2Options by remember { mutableStateOf(listOf(response.precision2)) }
    var system by remember { mutableStateOf(response.system) }
    var systemOptions by remember { mutableStateOf(listOf(response.system)) }
    var precision3 by remember { mutableStateOf(response.precision3) }
    var precision3Options by remember { mutableStateOf(listOf(response.precision3)) }
    var subSystem by remember { mutableStateOf(response.subSystem) }
    var subSystemOptions by remember { mutableStateOf(listOf(response.subSystem)) }
    var failure by remember { mutableStateOf(response.failure) }
    var failureOptions by remember { mutableStateOf(listOf(response.failure)) }

    var expandedLocation by remember { mutableStateOf(false) }
    var expandedPrecision1 by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedPrecision3 by remember { mutableStateOf(false) }
    var expandedSystem by remember { mutableStateOf(false) }
    var expandedPrecision2 by remember { mutableStateOf(false) }
    var expandedSubSystem by remember { mutableStateOf(false) }
    var expandedFailure by remember { mutableStateOf(false) }

    fun resetBelow(level: String) {
        when (level) {
            "location" -> {
                precision1 = ""; precision1Options = listOf()
                category = ""; categoryOptions = listOf()
                precision2 = ""; precision2Options = listOf()
                system = ""; systemOptions = listOf()
                precision3 = ""; precision3Options = listOf()
                subSystem = ""; subSystemOptions = listOf()
                failure = ""; failureOptions = listOf()
            }
            "precision1" -> {
                category = ""; categoryOptions = listOf()
                precision2 = ""; precision2Options = listOf()
                system = ""; systemOptions = listOf()
                precision3 = ""; precision3Options = listOf()
                subSystem = ""; subSystemOptions = listOf()
                failure = ""; failureOptions = listOf()
            }
            "category" -> {
                precision2 = ""; precision2Options = listOf()
                system = ""; systemOptions = listOf()
                precision3 = ""; precision3Options = listOf()
                subSystem = ""; subSystemOptions = listOf()
                failure = ""; failureOptions = listOf()
            }
            "precision2" -> {
                system = ""; systemOptions = listOf()
                precision3 = ""; precision3Options = listOf()
                subSystem = ""; subSystemOptions = listOf()
                failure = ""; failureOptions = listOf()
            }
            "system" -> {
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
                onExpandedChange = { expanded ->
                    expandedLocation = expanded
                    if (expanded) {
                        scope.launch {
                            locationOptions = loadOptionsSuspend(
                                context,
                                trainType,
                                trainCar,
                                seatNumber,
                                "location",
                                mapOf()
                            )
                        }
                    }
                },
                onSelected = {
                    location = it
                    resetBelow("location")
                }
                // ou null si paramètre nullable
            )
            
            DropdownField(
                label = "Précision 1",
                value = precision1,
                options = precision1Options,
                expanded = expandedPrecision1,
                onExpandedChange = { expanded ->
                    expandedPrecision1 = expanded
                    if (expanded) {
                        scope.launch {
                            precision1Options = loadOptionsSuspend(
                                context,
                                trainType,
                                trainCar,
                                seatNumber,
                                "precision1",
                                mapOf(
                                    "location" to location,
                                )
                            )
                        }
                    }
                },
                onSelected = {
                    precision1 = it
                    resetBelow("precision1")
                }
                // ou null si paramètre nullable
            )

            DropdownField(
                label = "Catégorie",
                value = category,
                options = categoryOptions,
                expanded = expandedCategory,
                onExpandedChange = { isExpanded ->
                    expandedCategory = isExpanded
                    if (isExpanded && location.isNotBlank()) {
                        scope.launch {
                            try {
                                val result = loadOptionsSuspend(context, trainType, trainCar, seatNumber, "category",
                                    mapOf(
                                        "location" to location,
                                        "precision1" to precision1,
                                    )
                                )
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
                label = "Précision 2",
                value = precision2,
                options = precision2Options,
                expanded = expandedPrecision2,
                onExpandedChange = { expanded ->
                    expandedPrecision2 = expanded
                    if (expanded) {
                        scope.launch {
                            precision2Options = loadOptionsSuspend(
                                context,
                                trainType,
                                trainCar,
                                seatNumber,
                                "precision2",
                                mapOf(
                                    "location" to location,
                                    "precision1" to precision1,
                                    "category" to category,
                                )
                            )
                        }
                    }
                },
                onSelected = {
                    precision2 = it
                    resetBelow("precision2")
                }
            )

            DropdownField(
                label = "Système",
                value = system,
                options = systemOptions,
                expanded = expandedSystem,
                onExpandedChange = { expanded ->
                    expandedSystem = expanded
                    if (expanded) {
                        scope.launch {
                            systemOptions = loadOptionsSuspend(context, trainType, trainCar, seatNumber, "system",
                                mapOf(
                                    "location" to location,
                                    "precision1" to precision1,
                                    "category" to category,
                                    "precision2" to precision2,
                                )
                            )
                        }
                    }
                },
                onSelected = {
                    system = it
                    resetBelow("system")
                }
                // ou null si paramètre nullable, car on charge via onExpandedChange
            )

            DropdownField(
                label = "Précision 3",
                value = precision3,
                options = precision3Options,
                expanded = expandedPrecision3,
                onExpandedChange = { expanded ->
                    expandedPrecision3 = expanded
                    if (expanded) {
                        scope.launch {
                            precision3Options = loadOptionsSuspend(
                                context,
                                trainType,
                                trainCar,
                                seatNumber,
                                "precision3",
                                mapOf(
                                    "location" to location,
                                    "precision1" to precision1,
                                    "category" to category,
                                    "precision2" to precision2,
                                    "system" to system,
                                )
                            )
                        }
                    }
                },
                onSelected = {
                    precision3 = it
                    resetBelow("precision3")
                }
            )

            DropdownField(
                label = "Sous-système",
                value = subSystem,
                options = subSystemOptions,
                expanded = expandedSubSystem,
                onExpandedChange = { expanded ->
                    expandedSubSystem = expanded
                    if (expanded) {
                        scope.launch {
                            subSystemOptions = loadOptionsSuspend(
                                context,
                                trainType,
                                trainCar,
                                seatNumber,
                                "subSystem",
                                mapOf(
                                    "location" to location,
                                    "precision1" to precision1,
                                    "category" to category,
                                    "precision2" to precision2,
                                    "system" to system,
                                    "precision3" to precision3,
                                )
                            )
                        }
                    }
                },
                onSelected = {
                    subSystem = it
                    resetBelow("subSystem")
                }
            )

            DropdownField(
                label = "Défaillance",
                value = failure,
                options = failureOptions,
                expanded = expandedFailure,
                onExpandedChange = { expanded ->
                    expandedFailure = expanded
                    if (expanded) {
                        scope.launch {
                            failureOptions = loadOptionsSuspend(
                                context,
                                trainType,
                                trainCar,
                                seatNumber,
                                "failure",
                                mapOf(
                                    "location" to location,
                                    "precision1" to precision1,
                                    "category" to category,
                                    "precision2" to precision2,
                                    "system" to system,
                                    "precision3" to precision3,
                                    "subSystem" to subSystem,
                                )
                            )
                        }
                    }
                },
                onSelected = {
                    failure = it
                    resetBelow("failure")
                }
            )

            // Pass navigateToNewReport to SubmitIncidentButton
            val incidentToSubmit = IncidentAnalysisResponse(
                uuid = response.uuid,
                timestamp = response.timestamp,
                location = location,
                precision1 = precision1,
                category = category,
                precision2 = precision2,
                system = system,
                precision3 = precision3,
                subSystem = subSystem,
                failure = failure,
            )

            SubmitIncidentButton(
                scope = scope,
                context = context,
                incident = incidentToSubmit,
                onSubmissionSuccess = {
                    IncidentSynchronizer.getInstance(context).submitIncidentAnalysisResponse(incidentToSubmit)
                },
                modifier = Modifier.fillMaxWidth()
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
                .menuAnchor(MenuAnchorType.PrimaryEditable, !options.isEmpty())
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


@RequiresApi(Build.VERSION_CODES.O)
suspend fun loadOptionsSuspend(
    context: Context,
    trainType: String,
    trainCar: String,
    seatNumber: Int?,
    level: String,
    selections: Map<String, String>
): List<String> {
    return try {
        RetrofitInstance.getCompletionApiService(context).findCompletion(
            IncidentCompletionRequest(
                trainTypes[trainType]!!,
                trainCar,
                seatNumber,
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
