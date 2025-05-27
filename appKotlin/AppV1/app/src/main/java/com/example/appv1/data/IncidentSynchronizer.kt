package com.example.appv1.data

import com.example.appv1.api.IncidentAnalysisRequest
import com.example.appv1.api.IncidentAnalysisResponse

enum class SynchronizationStatus {
    IDLE,
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

fun interface SynchronizationCallback {
    fun onStatusChanged(status: SynchronizationStatus)
}

interface IncidentSynchronizer {
    fun addIncidentAnalysisRequest(
        incidentAnalysisRequest: IncidentAnalysisRequest,
        onResult: (IncidentAnalysisResponse) -> Unit,
        onError: (String) -> Unit
    )

    fun getPendingIncidentAnalysisRequests(): List<IncidentAnalysisRequest>

    fun addIncidentAnalysisResponse(
        incidentAnalysisResponse: IncidentAnalysisResponse,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    )

    fun getPendingIncidentAnalysisResponses(): List<IncidentAnalysisResponse>

    suspend fun synchronize()

    fun getStatus(): SynchronizationStatus

    fun attachCallback(callback: SynchronizationCallback)

    fun attachSynchronizer(synchronizer: IncidentSynchronizer)

    fun getStatusFlow(): kotlinx.coroutines.flow.Flow<SynchronizationStatus>
}