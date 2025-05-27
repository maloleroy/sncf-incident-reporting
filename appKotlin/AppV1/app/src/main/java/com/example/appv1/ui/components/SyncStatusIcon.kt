package com.example.appv1.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.appv1.data.IncidentSynchronizer
import com.example.appv1.data.SynchronizationStatus

@Composable
fun SyncStatusIcon(synchronizer: IncidentSynchronizer) {
    val syncStatus = synchronizer.getStatus()

    IconButton(onClick = { /* Optionnel : action à exécuter au clic */ }) {
        BadgedBox(
            badge = {
                if (syncStatus != SynchronizationStatus.COMPLETED) {
                    Badge { Text(text = "NO") }
                }
            }
        ) {
            when (syncStatus) {
                SynchronizationStatus.PENDING -> Icon(
                    Icons.Default.Sync,
                    contentDescription = "En attente de synchronisation",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                SynchronizationStatus.IN_PROGRESS -> Icon(
                    Icons.Default.SyncProblem,
                    contentDescription = "Synchronisation en cours",
                    tint = MaterialTheme.colorScheme.primary
                )
                SynchronizationStatus.COMPLETED -> Icon(
                    Icons.Default.CloudDone,
                    contentDescription = "Synchronisation terminée",
                    tint = MaterialTheme.colorScheme.primary
                )
                SynchronizationStatus.FAILED -> Icon(
                    Icons.Default.CloudOff,
                    contentDescription = "Synchronisation échouée",
                    tint = MaterialTheme.colorScheme.error
                )
                SynchronizationStatus.IDLE -> Icon(
                    Icons.Default.CloudOff,
                    contentDescription = "Aucune synchronisation",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}