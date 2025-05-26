package com.example.appv1.ui.components

import android.content.Context
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
import com.example.appv1.api.IncidentAnalysisResponse
import com.example.appv1.api.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SubmitIncidentButton(
    scope: CoroutineScope,
    context: Context,
    incident: IncidentAnalysisResponse
) {
    Spacer(Modifier.height(56.dp))
    Button(
        onClick = {
            scope.launch {
                RetrofitInstance.getIncidentApiService(context).submitIncident(incident)
            }
        },
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp),
    ) {
        Text("Envoyer", fontSize = 16.sp)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Envoyer",
            modifier = Modifier.padding(start = 8.dp).size(16.dp)
        )
    }
}