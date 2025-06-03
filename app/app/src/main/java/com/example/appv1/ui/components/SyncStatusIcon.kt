package com.example.appv1.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.example.appv1.data.IncidentSynchronizer
import com.example.appv1.data.SynchronizationStatus
import kotlinx.coroutines.launch

@Composable
fun getIconFromStatus(status: SynchronizationStatus): ImageVector {
    return when (status) {
        SynchronizationStatus.PENDING -> Icons.Default.HourglassEmpty
        SynchronizationStatus.IN_PROGRESS -> Icons.Default.Sync
        SynchronizationStatus.COMPLETED -> Icons.Default.CheckCircle
        SynchronizationStatus.FAILED -> Icons.Default.Error
        SynchronizationStatus.WAITING_FOR_VALIDATION -> Icons.AutoMirrored.Filled.Assignment
    }
}

@Composable
fun getIconColorFromStatus(status: SynchronizationStatus): androidx.compose.ui.graphics.Color {
    return when (status) {
        SynchronizationStatus.PENDING -> MaterialTheme.colorScheme.secondary
        SynchronizationStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
        SynchronizationStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
        SynchronizationStatus.FAILED -> MaterialTheme.colorScheme.error
        SynchronizationStatus.WAITING_FOR_VALIDATION -> MaterialTheme.colorScheme.primary
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SyncStatusIcon(synchronizer: IncidentSynchronizer) {
    val syncStatus = synchronizer.getStatus()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    IconButton(onClick = {
        coroutineScope.launch {
            IncidentSynchronizer.getInstance(context).start()
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
            SynchronizationStatus.WAITING_FOR_VALIDATION -> Icon(
                Icons.Default.CloudDone,
                contentDescription = "Attente de validation",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}