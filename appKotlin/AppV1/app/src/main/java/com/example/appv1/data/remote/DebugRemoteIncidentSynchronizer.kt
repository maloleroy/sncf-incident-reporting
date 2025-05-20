package com.example.appv1.data.remote

import android.util.Log
import com.example.appv1.data.IncidentSynchronizer
import com.example.appv1.data.SynchronizationCallback
import com.example.appv1.data.SynchronizationStatus
import com.example.appv1.domain.model.Incident
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A debug implementation of [IncidentSynchronizer] that does nothing substantial.
 * It immediately considers any added incident as "synchronized" by logging it.
 * It simulates a brief synchronization process when synchronizeIncidents is called.
 * It never holds pending incidents.
 */
class DebugRemoteIncidentSynchronizer : IncidentSynchronizer {

    private val TAG = "DebugSynchronizer"
    private var callback: SynchronizationCallback? = null
    private val _statusFlow = MutableStateFlow(SynchronizationStatus.IDLE)

    // Helper to update status both internally and via callback
    private fun updateStatus(newStatus: SynchronizationStatus) {
        if (_statusFlow.value != newStatus) {
            _statusFlow.value = newStatus
            callback?.onStatusChanged(newStatus) // Notify callback of status change
            Log.d(TAG, "Status changed to: $newStatus")
        }
    }

    override fun addIncident(incident: Incident) {
        // Log the action but don't store it as pending.
        // Using toString() as Incident has no 'id' property defined in Incident.kt
        Log.d(TAG, "addIncident called (debug): Incident added [$incident], considered synchronized immediately.")
        // Status remains IDLE as nothing is pending
    }

    override suspend fun synchronizeIncidents() {
        // No pending incidents to synchronize in this debug implementation
        Log.d(TAG, "synchronizeIncidents called (debug): Simulating synchronization (no-op).")
        if (getStatus() == SynchronizationStatus.IDLE) { // Only sync if idle
            updateStatus(SynchronizationStatus.IN_PROGRESS)
            try {
                // Simulate network delay or processing time
                delay(500) // Simulate 0.5 seconds of work
                Log.d(TAG, "Debug synchronization simulation complete.")
                updateStatus(SynchronizationStatus.COMPLETED) // Indicate completion
                // Optionally return to IDLE after a short period
                delay(200)
                updateStatus(SynchronizationStatus.IDLE)
            } catch (e: Exception) {
                Log.e(TAG, "Debug synchronization simulation failed", e)
                updateStatus(SynchronizationStatus.FAILED)
                // Optionally return to IDLE after showing failure
                delay(200)
                updateStatus(SynchronizationStatus.IDLE)
            }
        } else {
            Log.w(TAG, "synchronizeIncidents called while not IDLE, ignoring.")
        }
    }

    override fun getPendingIncidents(): List<Incident> {
        // Always return an empty list as nothing is ever pending
        Log.d(TAG, "getPendingIncidents called (debug): Returning empty list.")
        return emptyList()
    }

    override fun getStatus(): SynchronizationStatus {
        // Return the current status from the flow
        val currentStatus = _statusFlow.value
        Log.d(TAG, "getStatus called (debug): Returning $currentStatus.")
        return currentStatus
    }

    override fun attachCallback(callback: SynchronizationCallback) {
        Log.d(TAG, "attachCallback called.")
        this.callback = callback
        // Immediately inform the new callback about the current status
        callback.onStatusChanged(_statusFlow.value)
    }

    override fun attachSynchronizer(synchronizer: IncidentSynchronizer) {
        // This method might be for a decorator pattern, not applicable here.
        Log.w(TAG, "attachSynchronizer called but not implemented in DebugRemoteIncidentSynchronizer.")
        // No operation needed for this debug implementation.
    }

    override fun getStatusFlow(): Flow<SynchronizationStatus> {
        Log.d(TAG, "getStatusFlow called.")
        return _statusFlow.asStateFlow()
    }
}