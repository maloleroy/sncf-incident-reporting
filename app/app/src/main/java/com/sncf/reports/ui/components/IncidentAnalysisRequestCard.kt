package com.sncf.reports.ui.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sncf.reports.model.IncidentAnalysisRequest
import com.sncf.reports.data.WithStatus
import com.sncf.reports.model.getTrainTypeByCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun IncidentAnalysisRequestCard(
    incident: WithStatus<IncidentAnalysisRequest>,
    scope: CoroutineScope,
) {
    val context = LocalContext.current
    var inProgress by remember { mutableStateOf(false) }

    Card(
        onClick = {
            inProgress = true
            scope.launch {
                inProgress = false
            }
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getIconFromStatus(incident.status),
                contentDescription = "Status",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 16.dp),
                tint = getIconColorFromStatus(incident.status)
            )

            Column(
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text(
                    text = "Train ${getTrainTypeByCode(incident.value.trainType)} - Rame ${incident.value.trainCar}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Description preview
                Text(
                    text = incident.value.transcription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            if (inProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}