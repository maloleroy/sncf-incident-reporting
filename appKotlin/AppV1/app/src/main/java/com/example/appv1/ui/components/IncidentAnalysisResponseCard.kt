package com.example.appv1.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appv1.api.IncidentAnalysisResponse
import com.example.appv1.data.WithStatus

@Composable
fun IncidentAnalysisResponseCard(
    incident: WithStatus<IncidentAnalysisResponse>,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Location section
            Text(
                text = "Lieu",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${incident.value.location} ${incident.value.precision1.takeIf { it.isNotBlank() }?.let { "- $it" } ?: ""}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Category section
            Text(
                text = "Catégorie",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${incident.value.category} ${incident.value.precision2.takeIf { it.isNotBlank() }?.let { "- $it" } ?: ""}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // System section
            Text(
                text = "Système",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${incident.value.system} ${incident.value.precision3.takeIf { it.isNotBlank() }?.let { "- $it" } ?: ""}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Subsystem and failure
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sous-système",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = incident.value.subSystem,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Défaillance",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = incident.value.failure,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}