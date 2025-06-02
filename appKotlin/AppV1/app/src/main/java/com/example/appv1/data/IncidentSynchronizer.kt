package com.example.appv1.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.content.edit
import com.example.appv1.api.IncidentAnalysisRequest
import com.example.appv1.api.IncidentAnalysisResponse
import com.example.appv1.api.RetrofitInstance
import com.example.appv1.api.IncidentAnalysisApiService
import com.example.appv1.ui.components.showErrorDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.IOException

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

class WithStatus<T>(t: T) {
    val value = t
    val status: SynchronizationStatus = SynchronizationStatus.PENDING
}

class LocalIncidentSynchronizer private constructor(private val context: Context) {

    private val gson = Gson()
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private var pendingIncidentAnalysisRequests: MutableList<WithStatus<IncidentAnalysisRequest>> = mutableListOf()
    private var pendingIncidentAnalysisResponses: MutableList<WithStatus<IncidentAnalysisResponse>> = mutableListOf()

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
        val requestsJson = gson.toJson(pendingIncidentAnalysisRequests)
        val responsesJson = gson.toJson(pendingIncidentAnalysisResponses)
        sharedPreferences.edit {
            putString(KEY_INCIDENT_ANALYSIS_REQUESTS, requestsJson)
            putString(KEY_INCIDENT_ANALYSIS_RESPONSES, responsesJson)
        }
    }

    fun addIncidentAnalysisRequest(
        incidentAnalysisRequest: IncidentAnalysisRequest,
        onResult: (IncidentAnalysisResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val withStatus = WithStatus(incidentAnalysisRequest)
        pendingIncidentAnalysisRequests.add(withStatus)
        saveIncidents()
        syncIncidentAnalysisRequest(
            withStatus,
            onResult = { response ->
                saveIncidents()
                onResult(response)
            },
            onError
        )
    }

    fun addIncidentAnalysisResponse(
        incidentAnalysisResponse: IncidentAnalysisResponse,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val withStatus = WithStatus(incidentAnalysisResponse)
        pendingIncidentAnalysisResponses.add(withStatus)
        saveIncidents()
        syncIncidentAnalysisResponse(
            withStatus,
            onResult = { response ->
                saveIncidents()
                onResult(response)
            },
            onError
        )
    }

    suspend fun synchronize() {
        withContext(Dispatchers.IO) {
            for (request in pendingIncidentAnalysisRequests) {
                syncIncidentAnalysisRequest(
                    request,
                    onResult = { response ->
                        pendingIncidentAnalysisResponses.add(WithStatus(response))
                        pendingIncidentAnalysisRequests.remove(request)
                        saveIncidents()
                    },
                    onError = { errorMessage ->
                        showErrorDialog(context, "Erreur lors de l'analyse de l'incident : $errorMessage")
                    }
                )
            }

            for (response in pendingIncidentAnalysisResponses) {
                syncIncidentAnalysisResponse(response,
                    onResult = { status ->
                        // Log or handle the status
                        println("Statut de l'incident soumis : $status")                    
                        pendingIncidentAnalysisResponses.remove(response)
                    },
                    onError = { errorMessage ->
                        // Log or handle the error message
                        showErrorDialog(context, "Erreur lors de la soumission de l'incident : $errorMessage")
                    }
                )
            }
            saveIncidents()
        }
    }

    private fun syncIncidentAnalysisRequest(
        incidentAnalysisRequest: WithStatus<IncidentAnalysisRequest>,
        onResult: (IncidentAnalysisResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = API_SERVICE_INSTANCE!!.generateIncidentAnalysis(incidentAnalysisRequest.value)
                withContext(Dispatchers.Main) {
                    onResult(response)
                }
            } catch (e: IOException) { // Catch only network errors (IOException)
                withContext(Dispatchers.Main) {
                    onError("Erreur de réseau lors de l'appel API : ${e.message}")
                }
            } catch (e: Exception) { // Catch other exceptions with a generic message
                withContext(Dispatchers.Main) {
                    onError("Erreur inattendue lors de l'appel API : ${e.message}")
                }
            }
        }
    }

    private fun syncIncidentAnalysisResponse(
        incidentAnalysisResponse: WithStatus<IncidentAnalysisResponse>,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = API_SERVICE_INSTANCE!!.submitIncident(incidentAnalysisResponse.value)
                withContext(Dispatchers.Main) {
                    onResult(response.status)
                }
            } catch (e: IOException) { // Catch only network errors (IOException)
                withContext(Dispatchers.Main) {
                    onError("Erreur de réseau lors de l'appel API : ${e.message}")
                }
            } catch (e: Exception) { // Catch other exceptions with a generic message
                withContext(Dispatchers.Main) {
                    onError("Erreur inattendue lors de l'appel API : ${e.message}")
                }
            }
        }
    }

    fun getPendingIncidentAnalysisRequests(): List<WithStatus<IncidentAnalysisRequest>> {
        return pendingIncidentAnalysisRequests.toList()
    }

    fun getPendingIncidentAnalysisResponses(): List<WithStatus<IncidentAnalysisResponse>> {
        return pendingIncidentAnalysisResponses.toList()
    }

    fun getStatus(): SynchronizationStatus {
        return if (pendingIncidentAnalysisRequests.isEmpty() && pendingIncidentAnalysisResponses.isEmpty()) {
            SynchronizationStatus.COMPLETED
        } else {
            SynchronizationStatus.PENDING
        }
    }

    fun attachCallback(callback: SynchronizationCallback) {
        TODO("Not yet implemented")
    }

    fun getStatusFlow(): Flow<SynchronizationStatus> {
        return flow {
            while (true) {
                emit(getStatus())
                delay(500) // Update every half second
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LocalIncidentSynchronizer? = null

        private var API_SERVICE_INSTANCE: IncidentAnalysisApiService? = null
        
        fun getInstance(context: Context): LocalIncidentSynchronizer {
            if (API_SERVICE_INSTANCE == null) {
                API_SERVICE_INSTANCE = RetrofitInstance.getIncidentApiService(context)
            }
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocalIncidentSynchronizer(context.applicationContext).also { INSTANCE = it }
            }
        }

        private const val PREFS_NAME = "incident_prefs"
        private const val KEY_INCIDENT_ANALYSIS_REQUESTS = "incident_analysis_requests"
        private const val KEY_INCIDENT_ANALYSIS_RESPONSES = "incident_analysis_responses"
    }
}