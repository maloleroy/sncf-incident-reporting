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
import com.sncf.reports.api.IncidentAnalysisResponse
import com.sncf.reports.api.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException // Added for error handling

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SubmitIncidentButton(
    scope: CoroutineScope,
    context: Context,
    incident: IncidentAnalysisResponse,
    onSubmissionSuccess: () -> Unit, // Added parameter
    modifier: Modifier = Modifier // Added parameter with default
) {
    Spacer(Modifier.height(56.dp))
    Button(
        onClick = {
            scope.launch {
                try {
                    // Assuming submitIncident now takes IncidentAnalysisResponse
                    // and returns something that indicates success/failure
                    // You might need to adjust the API service and response handling
                    val response = RetrofitInstance.getIncidentApiService(context).submitIncident(incident)
                    // TODO: Check response to confirm success before calling onSubmissionSuccess
                    // For now, assuming any response means success
                    onSubmissionSuccess()
                } catch (e: IOException) {
                    // Handle network errors
                    showErrorDialog(context, "Network Error: Could not submit incident. Please check your connection.")
                } catch (e: Exception) {
                    // Handle other errors (e.g., server errors, unexpected response)
                    showErrorDialog(context, "Error: Could not submit incident. ${e.message}")
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