package com.example.appv1.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.appv1.api.IncidentAnalysisResponse
import com.example.appv1.data.SynchronizationStatus
import com.example.appv1.data.WithStatus
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun IncidentAnalysisResponseCard(
    incident: WithStatus<IncidentAnalysisResponse>,
    onClick: () -> Unit
) {
    // Format the timestamp for display
    val formattedDate = remember(incident.value.timestamp) {
        val formatter = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.SHORT)
            .withZone(ZoneId.systemDefault())
        formatter.format(incident.value.timestamp)
    }

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
            // Header with status icon and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status icon
                Icon(
                    imageVector = getIconFromStatus(incident.status),
                    contentDescription = "Status",
                    tint = getIconColorFromStatus(incident.status),
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                )
                
                // Timestamp
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Date",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            // Title - Failure as main title
            Text(
                text = incident.value.failure,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Location and Category in a row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Location column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lieu",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = buildString {
                            append(incident.value.location)
                            if (incident.value.precision1.isNotBlank()) {
                                append(" - ")
                                append(incident.value.precision1)
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Category column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Catégorie",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = buildString {
                            append(incident.value.category)
                            if (incident.value.precision2.isNotBlank()) {
                                append(" - ")
                                append(incident.value.precision2)
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // System and Subsystem in a row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // System column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Système",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = buildString {
                            append(incident.value.system)
                            if (incident.value.precision3.isNotBlank()) {
                                append(" - ")
                                append(incident.value.precision3)
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Subsystem column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sous-système",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = incident.value.subSystem,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Status chip at the bottom
            if (incident.status != SynchronizationStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(8.dp))
                SuggestionChip(
                    onClick = { /* No action needed */ },
                    label = { 
                        Text(
                            when (incident.status) {
                                SynchronizationStatus.PENDING -> "En attente d'envoi"
                                SynchronizationStatus.IN_PROGRESS -> "Envoi en cours..."
                                SynchronizationStatus.FAILED -> "Échec de l'envoi"
                                SynchronizationStatus.WAITING_FOR_VALIDATION -> "Validation en attente"
                                SynchronizationStatus.COMPLETED -> "Envoyé"
                            }
                        ) 
                    },
                    icon = {
                        Icon(
                            imageVector = getIconFromStatus(incident.status),
                            contentDescription = null,
                            tint = getIconColorFromStatus(incident.status)
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = getIconColorFromStatus(incident.status).copy(alpha = 0.1f)
                    )
                )
            }
        }
    }
}