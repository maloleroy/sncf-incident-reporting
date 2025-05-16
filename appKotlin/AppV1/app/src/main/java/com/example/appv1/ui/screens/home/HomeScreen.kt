package com.example.appv1.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color // Import Color
import androidx.compose.foundation.BorderStroke // <-- Ajoutez cet import
import androidx.compose.material3.ButtonDefaults // <-- Ajoutez cet import

import com.example.appv1.data.remote.DebugRemoteIncidentSynchronizer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNewReport: () -> Unit,
    onViewReports: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SNCF Signalements", fontSize = 20.sp) },
                actions = {
                    SyncStatusIcon(DebugRemoteIncidentSynchronizer())
                }
            )
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
                Text("Nouveau signalement - Mode vocal")
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onViewReports,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Nouveau signalement - Mode arborescence")
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onViewReports,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Red,
                    contentColor = Color.Transparent,
                    disabledContainerColor = Color.Gray,
                ),
                border = BorderStroke(1.dp, Color.Red)
            ) {
                Text("Consulter les signalements")
            }
        }
    }
}
