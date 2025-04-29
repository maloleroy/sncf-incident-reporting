package com.example.appv1.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.appv1.data.IncidentSynchronizer
import com.example.appv1.data.SynchronizationStatus
import com.example.appv1.domain.model.Incident
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.content.edit
import com.example.appv1.data.SynchronizationCallback
import kotlinx.coroutines.flow.Flow

class LocalIncidentSynchronizer(private val context: Context) : IncidentSynchronizer {

    private val gson = Gson()
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private var pendingIncidents: MutableList<Incident> = mutableListOf()
    private var synchronized = true

    init {
        loadIncidents()
    }

    private fun loadIncidents() {
        val json = sharedPreferences.getString(KEY_PENDING_INCIDENTS, null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Incident>>() {}.type
            pendingIncidents = gson.fromJson(json, type) ?: mutableListOf()
            synchronized = pendingIncidents.isEmpty()
        }
    }

    private fun saveIncidents() {
        val json = gson.toJson(pendingIncidents)
        sharedPreferences.edit() { putString(KEY_PENDING_INCIDENTS, json) }
        synchronized = pendingIncidents.isEmpty()
    }

    override fun addIncident(incident: Incident) {
        pendingIncidents.add(incident)
        saveIncidents()
    }

    override suspend fun synchronizeIncidents() {
        // Simulation de l'envoi des incidents au serveur
        withContext(Dispatchers.IO) {
            // Dans une vraie implémentation, on enverrait les incidents au serveur ici
            // En cas de succès, on vide la liste des incidents en attente
            pendingIncidents.clear()
            saveIncidents()
        }
    }

    override fun getPendingIncidents(): List<Incident> {
        return pendingIncidents.toList()
    }


    override fun getStatus(): SynchronizationStatus {
        return if (synchronized) {
            SynchronizationStatus.COMPLETED
        } else {
            SynchronizationStatus.PENDING
        }
    }

    override fun attachCallback(callback: SynchronizationCallback) {
        TODO("Not yet implemented")
    }

    override fun attachSynchronizer(synchronizer: IncidentSynchronizer) {
        TODO("Not yet implemented")
    }

    override fun getStatusFlow(): Flow<SynchronizationStatus> {
        return kotlinx.coroutines.flow.flow {
            while (true) {
                emit(getStatus())
                kotlinx.coroutines.delay(1000) // Update every second
            }
        }
    }

    companion object {
        private const val PREFS_NAME = "incident_prefs"
        private const val KEY_PENDING_INCIDENTS = "pending_incidents"
    }
}