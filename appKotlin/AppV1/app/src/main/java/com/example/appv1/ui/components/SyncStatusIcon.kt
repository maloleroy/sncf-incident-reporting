package com.example.appv1.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.appv1.data.IncidentSynchronizer
import com.example.appv1.data.SynchronizationStatus
import com.example.appv1.data.local.LocalIncidentSynchronizer
import kotlinx.coroutines.launch

@Composable
fun SyncStatusIcon(synchronizer: IncidentSynchronizer) {
    val syncStatus = synchronizer.getStatus()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    IconButton(onClick = {
        coroutineScope.launch {
            LocalIncidentSynchronizer.getInstance(context).synchronize()
        }
    }) {
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