package com.sncf.reports.ui.components

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sncf.reports.data.IncidentSynchronizer
import com.sncf.reports.model.IncidentAnalysisResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SubmitIncidentButton(
    scope: CoroutineScope,
    context: Context,
    incident: IncidentAnalysisResponse,
    modifier: Modifier = Modifier // Added parameter with default
) {
    Spacer(Modifier.height(56.dp))
    Button(
        onClick = {
            scope.launch {
                try {
                    IncidentSynchronizer.getInstance(context).submitIncidentAnalysisResponse(incident)
                } catch (e: Exception) {
                    showErrorDialog(context, "Unexpected Error: Could not submit incident. ${e.message}")
                }
            }
        },
        shape = RoundedCornerShape(15.dp),
        modifier = modifier.fillMaxWidth().height(48.dp), // Use the modifier parameter
    ) {
        Text("Envoyer", fontSize = 16.sp)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Envoyer",
            modifier = Modifier.padding(start = 8.dp).size(16.dp)
        )
    }
}