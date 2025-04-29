package com.example.appv1.data.remote

import com.example.appv1.data.IncidentSynchronizer
import com.example.appv1.data.SynchronizationCallback
import com.example.appv1.data.SynchronizationStatus
import com.example.appv1.domain.model.Incident
import kotlinx.coroutines.flow.Flow

class DebugRemoteIncidentSynchronizer: IncidentSynchronizer {
    override fun addIncident(incident: Incident) {
        TODO("Not yet implemented")
    }

    override suspend fun synchronizeIncidents() {
        TODO("Not yet implemented")
    }

    override fun getPendingIncidents(): List<Incident> {
        TODO("Not yet implemented")
    }

    override fun getStatus(): SynchronizationStatus {
        TODO("Not yet implemented")
    }

    override fun attachCallback(callback: SynchronizationCallback) {
        TODO("Not yet implemented")
    }

    override fun attachSynchronizer(synchronizer: IncidentSynchronizer) {
        TODO("Not yet implemented")
    }

    override fun getStatusFlow(): Flow<SynchronizationStatus> {
        TODO("Not yet implemented")
    }
}