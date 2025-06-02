package com.example.appv1.data

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

enum class SynchronizationStatus {
    WAITING_FOR_VALIDATION,
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

class WithStatus<T>(
    val value: T,
    var status: SynchronizationStatus
)

class IncidentSynchronizer private constructor(private val context: Context) {

    private val isActive = AtomicBoolean(false)
    private val dispatcher = Dispatchers.IO
    private val syncScope = CoroutineScope(SupervisorJob() + dispatcher)

    private val gson = Gson()
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private var pendingIncidentAnalysisRequests: MutableList<WithStatus<IncidentAnalysisRequest>> = mutableListOf()
    private var pendingIncidentAnalysisResponses: MutableList<WithStatus<IncidentAnalysisResponse>> = mutableListOf()

    init {
        // If the loading fails, we initialize the lists to avoid null references
        // and we clear the shared preferences to avoid stale data
        try {
            loadIncidents()
        } catch (_: Exception) {
            pendingIncidentAnalysisRequests = mutableListOf()
            pendingIncidentAnalysisResponses = mutableListOf()
            sharedPreferences.edit {
                clear()
            }
        }
    }

    private fun loadIncidents() {
        val requestsJson = sharedPreferences.getString(KEY_INCIDENT_ANALYSIS_REQUESTS, null)
        if (requestsJson != null) {
            try {
                // Use the correct type with WithStatus wrapper
                val type = object : TypeToken<MutableList<WithStatus<IncidentAnalysisRequest>>>() {}.type
                pendingIncidentAnalysisRequests = gson.fromJson(requestsJson, type) ?: mutableListOf()
            } catch (e: Exception) {
                // Log the error and reset to empty list
                pendingIncidentAnalysisRequests = mutableListOf()
            }
        }
        
        val responseJson = sharedPreferences.getString(KEY_INCIDENT_ANALYSIS_RESPONSES, null)
        if (responseJson != null) {
            try {
                // Use the correct type with WithStatus wrapper
                val type = object : TypeToken<MutableList<WithStatus<IncidentAnalysisResponse>>>() {}.type
                pendingIncidentAnalysisResponses = gson.fromJson(responseJson, type) ?: mutableListOf()
            } catch (e: Exception) {
                // Log the error and reset to empty list
                pendingIncidentAnalysisResponses = mutableListOf()
            }
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
        val withStatus = WithStatus(incidentAnalysisRequest, SynchronizationStatus.PENDING)
        pendingIncidentAnalysisRequests.add(withStatus)
        saveIncidents()
    }

    fun addIncidentAnalysisResponse(
        incidentAnalysisResponse: IncidentAnalysisResponse,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val withStatus = WithStatus(incidentAnalysisResponse, SynchronizationStatus.WAITING_FOR_VALIDATION)
        pendingIncidentAnalysisResponses.add(withStatus)
        saveIncidents()
    }

    fun start() {
        if (isActive.compareAndSet(false, true)) {
            syncScope.launch {
                while (isActive.get()) {
                    synchronizeOnce()
                    delay(500) // Brief pause between cycles
                }
            }
        }
    }

    private fun setIncidentAnalysisRequestStatus(
        incidentAnalysisRequest: WithStatus<IncidentAnalysisRequest>,
        status: SynchronizationStatus
    ) {
        // Update the status of the request
        incidentAnalysisRequest.status = status
        saveIncidents()
    }

    private fun setIncidentAnalysisResponseStatus(
        incidentAnalysisResponse: WithStatus<IncidentAnalysisResponse>,
        status: SynchronizationStatus
    ) {
        // Update the status of the response
        incidentAnalysisResponse.status = status
        saveIncidents()
    }

    private suspend fun synchronizeOnce() {
        withContext(dispatcher) {
            pendingIncidentAnalysisRequests.filter { 
                it.status == SynchronizationStatus.FAILED || 
                it.status == SynchronizationStatus.PENDING 
            }.forEach { request ->
                setIncidentAnalysisRequestStatus(request, SynchronizationStatus.IN_PROGRESS)
                syncIncidentAnalysisRequest(
                    request,
                    onResult = { response ->
                        setIncidentAnalysisRequestStatus(request, SynchronizationStatus.COMPLETED)
                        pendingIncidentAnalysisResponses.add(WithStatus(response, SynchronizationStatus.WAITING_FOR_VALIDATION))
                        saveIncidents()
                    },
                    onError = { errorMessage ->
                        setIncidentAnalysisRequestStatus(request, SynchronizationStatus.FAILED)
                        showErrorDialog(context, "Erreur lors de l'analyse de l'incident : $errorMessage")
                    }
                )
            }

            pendingIncidentAnalysisResponses.filter {
                it.status == SynchronizationStatus.FAILED || 
                it.status == SynchronizationStatus.PENDING 
            }.forEach { response ->
                setIncidentAnalysisResponseStatus(response, SynchronizationStatus.IN_PROGRESS)
                syncIncidentAnalysisResponse(response,
                    onResult = { status ->
                        setIncidentAnalysisResponseStatus(response, SynchronizationStatus.COMPLETED)
                        Toast.makeText(context, "Incident soumis avec succès", Toast.LENGTH_SHORT).show()
                    },
                    onError = { errorMessage ->
                        setIncidentAnalysisResponseStatus(response, SynchronizationStatus.FAILED)
                        showErrorDialog(context, "Erreur lors de la soumission de l'incident : $errorMessage")
                    }
                )
            }
            saveIncidents()
        }
    }

    fun stop() {
        isActive.set(false)
        syncScope.coroutineContext.cancelChildren()
    }

    private fun syncIncidentAnalysisRequest(
        incidentAnalysisRequest: WithStatus<IncidentAnalysisRequest>,
        onResult: (IncidentAnalysisResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        syncScope.launch {
            try {
                val response = API_SERVICE_INSTANCE!!.generateIncidentAnalysis(incidentAnalysisRequest.value)
                withContext(Dispatchers.Main) {
                    onResult(response)
                }
            } catch (e: HttpException) { // Handle HTTP errors (4xx, 5xx status codes)
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        404 -> onError("Aucun incident correspondant trouvé (404)")
                        503 -> onError("Le serveur d'IA est indisponible (503)")
                        else -> onError("Erreur HTTP ${e.code()}: ${e.message()}")
                    }
                }
            } catch (e: IOException) { // Catch network errors (IOException)
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
        syncScope.launch {
            try {
                val response = API_SERVICE_INSTANCE!!.submitIncident(incidentAnalysisResponse.value)
                withContext(Dispatchers.Main) {
                    onResult(response.status)
                }
            } catch (e: HttpException) { // Handle HTTP errors (4xx, 5xx status codes)
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        404 -> onError("Le serveur n'a pas pu traiter l'incident (404)")
                        503 -> onError("Le serveur est actuellement indisponible (503)")
                        else -> onError("Erreur HTTP ${e.code()}: ${e.message()}")
                    }
                }
            } catch (e: IOException) { // Catch network errors (IOException)
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

    companion object {
        @Volatile
        private var INSTANCE: IncidentSynchronizer? = null

        private var API_SERVICE_INSTANCE: IncidentAnalysisApiService? = null
        
        @RequiresApi(Build.VERSION_CODES.O)
        fun getInstance(context: Context): IncidentSynchronizer {
            if (API_SERVICE_INSTANCE == null) {
                API_SERVICE_INSTANCE = RetrofitInstance.getIncidentApiService(context)
            }
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: IncidentSynchronizer(context.applicationContext).also { INSTANCE = it }
            }
        }

        private const val PREFS_NAME = "incident_prefs"
        private const val KEY_INCIDENT_ANALYSIS_REQUESTS = "incident_analysis_requests"
        private const val KEY_INCIDENT_ANALYSIS_RESPONSES = "incident_analysis_responses"
    }
}