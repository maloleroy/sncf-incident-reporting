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
import kotlin.random.Random

/**
 * Debug class simulating a remote incident synchronizer.
 * Simulates network operations with random delays and failures.
 */
class DebugRemoteIncidentSynchronizer : IncidentSynchronizer {

    private val pendingIncidents = mutableListOf<Incident>()
    private val _status = MutableStateFlow(SynchronizationStatus.COMPLETED)
    private var callback: SynchronizationCallback? = null
    private var attachedSynchronizer: IncidentSynchronizer? = null

    private val TAG = "DebugRemoteSynchronizer"

    /**
     * Adds a new incident to the list of incidents to be synchronized.
     * Also updates the status if necessary.
     *
     * @param incident The incident to add.
     */
    override fun addIncident(incident: Incident) {
        pendingIncidents.add(incident)
        Log.d(TAG, "Incident added: ${incident.uid()}, total: ${pendingIncidents.size}")

        if (_status.value != SynchronizationStatus.PENDING && _status.value != SynchronizationStatus.IN_PROGRESS) {
            updateStatus(SynchronizationStatus.PENDING)
        }

        // Propagate the addition to the attached synchronizer
        attachedSynchronizer?.addIncident(incident)
    }

    /**
     * Simulates the synchronization of incidents with a remote server.
     * Introduces random delays and can randomly fail.
     */
    override suspend fun synchronizeIncidents() {
        if (pendingIncidents.isEmpty()) {
            Log.d(TAG, "No incidents to synchronize")
            updateStatus(SynchronizationStatus.COMPLETED)
            return
        }

        updateStatus(SynchronizationStatus.IN_PROGRESS)
        Log.d(TAG, "Starting synchronization of ${pendingIncidents.size} incidents")

        // Simulating a network operation with delay
        delay(2000 + Random.nextLong(3000))

        // Random failure simulation (20% chance)
        if (Random.nextInt(100) < 20) {
            Log.w(TAG, "Synchronization failed")
            updateStatus(SynchronizationStatus.FAILED)
            return
        }

        // Success simulation
        pendingIncidents.clear()
        Log.d(TAG, "Synchronization successful")
        updateStatus(SynchronizationStatus.COMPLETED)

        // Propagate the synchronization to the attached synchronizer
        attachedSynchronizer?.let {
            Log.d(TAG, "Propagating synchronization to attached synchronizer")
            it.synchronizeIncidents()
        }
    }

    /**
     * Returns a copy of the list of incidents waiting for synchronization.
     *
     * @return A list of pending incidents.
     */
    override fun getPendingIncidents(): List<Incident> {
        return pendingIncidents.toList()
    }

    /**
     * Returns the current synchronization status.
     *
     * @return The current synchronization status.
     */
    override fun getStatus(): SynchronizationStatus {
        return _status.value
    }

    /**
     * Attaches a callback to be notified of status changes.
     *
     * @param callback The callback to attach.
     */
    override fun attachCallback(callback: SynchronizationCallback) {
        this.callback = callback
        Log.d(TAG, "Callback attached")
    }

    /**
     * Attaches another synchronizer to chain operations.
     *
     * @param synchronizer The synchronizer to attach.
     */
    override fun attachSynchronizer(synchronizer: IncidentSynchronizer) {
        this.attachedSynchronizer = synchronizer
        Log.d(TAG, "Synchronizer attached")
    }

    /**
     * Provides a Flow to observe synchronization status changes.
     *
     * @return A Flow emitting current synchronization statuses.
     */
    override fun getStatusFlow(): Flow<SynchronizationStatus> {
        return _status.asStateFlow()
    }

    /**
     * Updates the synchronization status and notifies the callback if necessary.
     *
     * @param newStatus The new synchronization status.
     */
    private fun updateStatus(newStatus: SynchronizationStatus) {
        _status.value = newStatus
        callback?.onStatusChanged(newStatus)
        Log.d(TAG, "Status updated: $newStatus")
    }
}