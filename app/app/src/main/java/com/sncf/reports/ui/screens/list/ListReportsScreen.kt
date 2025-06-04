package com.sncf.reports.ui.screens.list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sncf.reports.data.IncidentSynchronizer
import com.sncf.reports.ui.components.IncidentAnalysisRequestCard
import com.sncf.reports.ui.components.IncidentAnalysisResponseCard
import com.sncf.reports.model.ReportSharedViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListReportsScreen(
    onBack: () -> Unit,
    navController: NavController,
    sharedViewModel: ReportSharedViewModel
)
{
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Liste des incidents") },
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
        ) {
            Text(
                text = "Analyses d'incidents",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            for (incidents in IncidentSynchronizer.getInstance(context).getIncidentAnalysisRequests()) {
                IncidentAnalysisRequestCard(
                    incidents,
                    scope = coroutineScope,
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = "Incidents",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            for (incidents in IncidentSynchronizer.getInstance(context).getIncidentAnalysisResponses()) {
                IncidentAnalysisResponseCard(
                    incidents,
                    onClick = {
                        // Set the data in the shared view model
                        sharedViewModel.lastIncidentAnalysisResponse = incidents.value

                        // Get request with the same UUID to access train info
                        val matchingRequest = IncidentSynchronizer.getInstance(context)
                            .getIncidentAnalysisRequests()
                            .find { it.value.uuid == incidents.value.uuid }

                        // Default values if no matching request is found
                        sharedViewModel.trainType = ""
                        sharedViewModel.trainCar = ""
                        sharedViewModel.seatNumber = null

                        // Navigate to confirmation screen
                        navController.navigate("confirm_report")
                    }
                )
            }
        }
    }
}
