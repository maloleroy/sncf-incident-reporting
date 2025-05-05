package com.example.appv1.data

import com.example.appv1.domain.model.Incident

/**
 * Enum representing the possible synchronization statuses.
 */
enum class SynchronizationStatus {
    IDLE,

    /** Indicates that synchronization is pending. */
    PENDING,

    /** Indicates that synchronization is currently in progress. */
    IN_PROGRESS,

    /** Indicates that synchronization has been completed successfully. */
    COMPLETED,

    /** Indicates that synchronization has failed. */
    FAILED
}

/**
 * Functional interface for handling synchronization status changes.
 */
fun interface SynchronizationCallback {
    /**
     * Called when the synchronization status changes.
     *
     * @param status The new synchronization status.
     */
    fun onStatusChanged(status: SynchronizationStatus)
}

/**
 * Interface for managing and synchronizing incidents.
 */
interface IncidentSynchronizer {
    /**
     * Adds a new incident to the list of incidents to be synchronized.
     *
     * @param incident The incident to add.
     */
    fun addIncident(incident: Incident)

    /**
     * Synchronizes the incidents with the server.
     * This is a suspend function and should be called from a coroutine.
     */
    suspend fun synchronizeIncidents()

    /**
     * Retrieves the list of incidents that are pending synchronization.
     *
     * @return A list of pending incidents.
     */
    fun getPendingIncidents(): List<Incident>

    /**
     * Retrieves the current synchronization status.
     *
     * @return The current synchronization status.
     */
    fun getStatus(): SynchronizationStatus

    /**
     * Attaches a callback to listen for synchronization status changes.
     *
     * @param callback The callback to attach.
     */
    fun attachCallback(callback: SynchronizationCallback)

    /**
     * Attaches another synchronizer to delegate or chain synchronization operations.
     *
     * @param synchronizer The synchronizer to attach.
     */
    fun attachSynchronizer(synchronizer: IncidentSynchronizer)

    /**
     *  Provides a Flow to observe changes in synchronization status.
     *
     * @return A Flow emitting the current synchronization statuses.
     */
    fun getStatusFlow(): kotlinx.coroutines.flow.Flow<SynchronizationStatus>
}