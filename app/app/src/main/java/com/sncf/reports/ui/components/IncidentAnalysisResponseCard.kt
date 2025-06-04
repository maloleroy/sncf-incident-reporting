package com.sncf.reports.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sncf.reports.data.SynchronizationStatus
import com.sncf.reports.data.WithStatus
import com.sncf.reports.model.IncidentAnalysisResponse

fun getTitle(incident: WithStatus<IncidentAnalysisResponse>): String {
    return if (incident.value.failure.isNotBlank()) {
        if (incident.value.failure.split(',').isEmpty()) {
            incident.value.system
        } else {
            "${incident.value.system} - ${incident.value.failure.split(',').first()}"
        }
    } else {
        "Incident"
    }
}

@Composable
fun IncidentDetail(
    title: String,
    content: String,
    modifier: Modifier
) {
    if (content.isBlank()) {
        Spacer(modifier) // Empty space if no subsystem
    } else {
        Column(modifier) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun IncidentAnalysisResponseCardHeader(
    incident: WithStatus<IncidentAnalysisResponse>,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status icon
        Icon(
            imageVector = getIconFromStatus(incident.status),
            contentDescription = "Statut",
            tint = getIconColorFromStatus(incident.status),
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = getTitle(incident),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
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
            IncidentAnalysisResponseCardHeader(incident)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Location and Category in a row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IncidentDetail(
                    title = "Lieu",
                    content = buildString {
                        append(incident.value.location)
                        if (incident.value.precision1.isNotBlank()) {
                            append(" - ")
                            append(incident.value.precision1)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                IncidentDetail(
                    title = "Catégorie",
                    content = buildString {
                        append(incident.value.category)
                        if (incident.value.precision2.isNotBlank()) {
                            append(" - ")
                            append(incident.value.precision2)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // System and Subsystem in a row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IncidentDetail(
                    title = "Précision",
                    content = incident.value.precision3,
                    modifier = Modifier.weight(1f)
                )
                IncidentDetail(
                    title = "Sous-système",
                    content = incident.value.subSystem,
                    modifier = Modifier.weight(1f)
                )

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
}