package com.sncf.reports.data

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
import com.sncf.reports.model.IncidentAnalysisRequest
import com.sncf.reports.model.IncidentAnalysisResponse
import com.sncf.reports.api.RetrofitInstance
import com.sncf.reports.api.IncidentAnalysisApiService
import com.sncf.reports.ui.components.showErrorDialog
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

    private var incidentAnalysisRequests: MutableList<WithStatus<IncidentAnalysisRequest>> = mutableListOf()
    private var incidentAnalysisResponses: MutableList<WithStatus<IncidentAnalysisResponse>> = mutableListOf()
    
    // Navigation callback for when an IncidentAnalysisResponse is received
    private var onNavigationToConfirmation: ((IncidentAnalysisResponse, String, String, Int?) -> Unit)? = null

    // Navigation callback for going back to home after successful incident submission
    private var onNavigateToHome: (() -> Unit)? = null

    init {
        // If the loading fails, we initialize the lists to avoid null references
        // and we clear the shared preferences to avoid stale data
        try {
            loadIncidents()
        } catch (_: Exception) {
            incidentAnalysisRequests = mutableListOf()
            incidentAnalysisResponses = mutableListOf()
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
                incidentAnalysisRequests = gson.fromJson(requestsJson, type) ?: mutableListOf()
            } catch (e: Exception) {
                // Log the error and reset to empty list
                incidentAnalysisRequests = mutableListOf()
            }
        }
        
        val responseJson = sharedPreferences.getString(KEY_INCIDENT_ANALYSIS_RESPONSES, null)
        if (responseJson != null) {
            try {
                // Use the correct type with WithStatus wrapper
                val type = object : TypeToken<MutableList<WithStatus<IncidentAnalysisResponse>>>() {}.type
                incidentAnalysisResponses = gson.fromJson(responseJson, type) ?: mutableListOf()
            } catch (_: Exception) {
                // Log the error and reset to empty list
                incidentAnalysisResponses = mutableListOf()
            }
        }

        // We set all IN_PROGRESS requests and responses to PENDING
        incidentAnalysisRequests.filter { predicate ->
            predicate.status == SynchronizationStatus.IN_PROGRESS
        }.forEach { request ->
            request.status = SynchronizationStatus.PENDING
        }
        incidentAnalysisResponses.filter { predicate ->
            predicate.status == SynchronizationStatus.IN_PROGRESS
        }.forEach { response ->
            response.status = SynchronizationStatus.PENDING
        }
    }

    private fun saveIncidents() {
        val requestsJson = gson.toJson(incidentAnalysisRequests)
        val responsesJson = gson.toJson(incidentAnalysisResponses)
        sharedPreferences.edit {
            putString(KEY_INCIDENT_ANALYSIS_REQUESTS, requestsJson)
            putString(KEY_INCIDENT_ANALYSIS_RESPONSES, responsesJson)
        }
    }

    fun addIncidentAnalysisRequest(
        incidentAnalysisRequest: IncidentAnalysisRequest,
    ) {
        val withStatus = WithStatus(incidentAnalysisRequest, SynchronizationStatus.PENDING)
        incidentAnalysisRequests.add(withStatus)
        saveIncidents()
    }
    
    fun setNavigationCallback(
        onNavigationToConfirmation: (IncidentAnalysisResponse, String, String, Int?) -> Unit
    ) {
        this.onNavigationToConfirmation = onNavigationToConfirmation
    }

    fun setHomeNavigationCallback(onNavigateToHome: () -> Unit) {
        this.onNavigateToHome = onNavigateToHome
    }

    // Set the IncidentAnalysisResponse as PENDING given their UUID
    // and save the changes
    fun submitIncidentAnalysisResponse(
        incidentAnalysisResponse: IncidentAnalysisResponse,
    ) {
        // Remove any existing response with the same UUID
        incidentAnalysisResponses.removeAll { it.value.uuid == incidentAnalysisResponse.uuid }
        val withStatus = WithStatus(incidentAnalysisResponse, SynchronizationStatus.PENDING)
        incidentAnalysisResponses.add(withStatus)
        saveIncidents()
    }

    fun start() {
        if (isActive.compareAndSet(false, true)) {
            syncScope.launch {
                while (isActive.get()) {
                    val prq = incidentAnalysisRequests.filter {
                        it.status == SynchronizationStatus.PENDING
                    }.size
                    val frq = incidentAnalysisRequests.filter {
                        it.status == SynchronizationStatus.FAILED
                    }.size
                    val prs = incidentAnalysisResponses.filter {
                        it.status == SynchronizationStatus.PENDING
                    }.size
                    val frs = incidentAnalysisResponses.filter {
                        it.status == SynchronizationStatus.FAILED
                    }.size
                    // Log the start of synchronization
                    if (prq != 0 || frq != 0 || prs != 0 || frs != 0) {
                        println("Syncing $prq PRq, $frq FRq, $prs PRs, and $frs FRs")
                        synchronizeOnce()
                        delay(500) // Brief pause between cycles
                    } else {
                        delay(100)
                    }
                }
            }
        }
    }

    private fun setIncidentAnalysisRequestStatus(
        uuid: java.util.UUID,
        status: SynchronizationStatus
    ) {
        // Find the request by UUID and update its status
        val request = incidentAnalysisRequests.find { it.value.uuid == uuid }
        if (request != null) {
            request.status = status
            saveIncidents()
        }
    }

    private fun setIncidentAnalysisResponseStatus(
        uuid: java.util.UUID,
        status: SynchronizationStatus
    ) {
        val response = incidentAnalysisResponses.find { it.value.uuid == uuid }
        if (response != null) {
            response.status = status
            saveIncidents()
        }
    }

    private suspend fun synchronizeOnce() {
        withContext(dispatcher) {
            incidentAnalysisRequests.filter {
                it.status == SynchronizationStatus.FAILED || 
                it.status == SynchronizationStatus.PENDING 
            }.forEach { request ->
                setIncidentAnalysisRequestStatus(request.value.uuid, SynchronizationStatus.IN_PROGRESS)
                syncIncidentAnalysisRequest(
                    request,
                    onResult = { response ->
                        setIncidentAnalysisRequestStatus(request.value.uuid, SynchronizationStatus.COMPLETED)
                        incidentAnalysisResponses.add(WithStatus(response, SynchronizationStatus.WAITING_FOR_VALIDATION))
                        saveIncidents()
                        
                        // Navigate to ConfirmationScreen with the received response on main thread
                        syncScope.launch {
                            withContext(Dispatchers.Main) {
                                onNavigationToConfirmation?.invoke(
                                    response,
                                    request.value.trainType,
                                    request.value.trainCar,
                                    request.value.seatNumber
                                )
                            }
                        }
                    },
                    onError = { errorMessage ->
                        setIncidentAnalysisRequestStatus(request.value.uuid, SynchronizationStatus.FAILED)
                        // showErrorDialog(context, "Erreur lors de l'analyse de l'incident : $errorMessage")
                    }
                )
            }

            incidentAnalysisResponses.filter {
                it.status == SynchronizationStatus.FAILED || 
                it.status == SynchronizationStatus.PENDING 
            }.forEach { response ->
                setIncidentAnalysisResponseStatus(response.value.uuid, SynchronizationStatus.IN_PROGRESS)
                syncIncidentAnalysisResponse(response,
                    onResult = { status ->
                        setIncidentAnalysisResponseStatus(response.value.uuid, SynchronizationStatus.COMPLETED)
                        Toast.makeText(context, "Incident soumis avec succès", Toast.LENGTH_SHORT).show()
                        syncScope.launch {
                            withContext(Dispatchers.Main) {
                                onNavigateToHome?.invoke()
                            }
                        }
                    },
                    onError = { errorMessage ->
                        setIncidentAnalysisResponseStatus(response.value.uuid, SynchronizationStatus.FAILED)
                        // showErrorDialog(context, "Erreur lors de la soumission de l'incident : $errorMessage")
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

    fun getIncidentAnalysisRequests(): List<WithStatus<IncidentAnalysisRequest>> {
        return incidentAnalysisRequests.toList()
    }

    fun getIncidentAnalysisResponses(): List<WithStatus<IncidentAnalysisResponse>> {
        return incidentAnalysisResponses.toList()
    }

    fun getStatus(): SynchronizationStatus {
        return if (incidentAnalysisRequests.isEmpty() && incidentAnalysisResponses.isEmpty()) {
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