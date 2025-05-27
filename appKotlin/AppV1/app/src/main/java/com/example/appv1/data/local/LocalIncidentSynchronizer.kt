package com.example.appv1.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.appv1.data.IncidentSynchronizer
import com.example.appv1.data.SynchronizationStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.content.edit
import com.example.appv1.api.IncidentAnalysisRequest
import com.example.appv1.api.IncidentAnalysisResponse
import com.example.appv1.data.SynchronizationCallback
import kotlinx.coroutines.flow.Flow

class LocalIncidentSynchronizer private constructor(private val context: Context)
    : IncidentSynchronizer {

    private val gson = Gson()
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private var pendingIncidentAnalysisRequests: MutableList<IncidentAnalysisRequest> = mutableListOf()
    private var pendingIncidentAnalysisResponses: MutableList<IncidentAnalysisResponse> = mutableListOf()

    init {
        loadIncidents()
    }

    private fun loadIncidents() {
        val requestsJson = sharedPreferences.getString(KEY_INCIDENT_ANALYSIS_REQUESTS, null)
        if (requestsJson != null) {
            val type = object : TypeToken<MutableList<IncidentAnalysisRequest>>() {}.type
            pendingIncidentAnalysisRequests = gson.fromJson(requestsJson, type) ?: mutableListOf()
        }
        val responseJson = sharedPreferences.getString(KEY_INCIDENT_ANALYSIS_RESPONSES, null)
        if (responseJson != null) {
            val type = object : TypeToken<MutableList<IncidentAnalysisResponse>>() {}.type
            pendingIncidentAnalysisResponses = gson.fromJson(responseJson, type) ?: mutableListOf()
        }
    }

    private fun saveIncidents() {
        val json = gson.toJson(pendingIncidentAnalysisRequests)
        sharedPreferences.edit { putString(KEY_INCIDENT_ANALYSIS_REQUESTS, json) }
    }

    override fun addIncidentAnalysisRequest(incident: IncidentAnalysisRequest) {
        pendingIncidentAnalysisRequests.add(incident)
        saveIncidents()
    }

    override fun addIncidentAnalysisResponse(incident: IncidentAnalysisResponse) {
        pendingIncidentAnalysisResponses.add(incident)
        saveIncidents()
    }

    override suspend fun synchronize() {
        withContext(Dispatchers.IO) {
            // Dans une vraie implémentation, on enverrait les incidents au serveur ici
            // En cas de succès, on vide la liste des incidents en attente
            TODO()
            saveIncidents()
        }
    }

    override fun getPendingIncidentAnalysisRequests(): List<IncidentAnalysisRequest> {
        return pendingIncidentAnalysisRequests.toList()
    }

    override fun getPendingIncidentAnalysisResponses(): List<IncidentAnalysisResponse> {
        return pendingIncidentAnalysisResponses.toList()
    }

    override fun getStatus(): SynchronizationStatus {
        return if (pendingIncidentAnalysisRequests.isEmpty() && pendingIncidentAnalysisResponses.isEmpty()) {
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
                kotlinx.coroutines.delay(500) // Update every half second
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LocalIncidentSynchronizer? = null
        
        fun getInstance(context: Context): LocalIncidentSynchronizer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocalIncidentSynchronizer(context.applicationContext).also { INSTANCE = it }
            }
        }
        private const val PREFS_NAME = "incident_prefs"
        private const val KEY_INCIDENT_ANALYSIS_REQUESTS = "incident_analysis_requests"
        private const val KEY_INCIDENT_ANALYSIS_RESPONSES = "incident_analysis_responses"
    }
}