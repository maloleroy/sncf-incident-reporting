package com.sncf.reports.api

import com.sncf.reports.BuildConfig
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import java.time.Instant
import java.util.UUID

data class IncidentAnalysisRequest(
    val uuid: UUID,
    val timestamp: Instant,
    val trainType : String,
    val trainCar : String,
    val transcription : String
)

data class IncidentAnalysisResponse(
    val uuid: UUID,
    val timestamp: Instant,
    val location : String,
    val precision1 : String,
    val category : String,
    val precision2 : String,
    val system : String,
    val precision3 : String,
    val subSystem : String,
    val failure : String
)

data class IncidentSubmittingResponse(
    val status: String
)

data class ConservedInformations(
    val location: String? = null,
    val precision1: String? = null,
    val category: String? = null,
    val precision2: String? = null,
    val system: String? = null,
    val precision3: String? = null,
    val subSystem: String? = null,
    val failure: String? = null
)

data class IncidentCompletionRequest(
    val trainType: String,
    val trainCar: String,
    val seatNumber: Int?,
    val level: String,
    val selections: ConservedInformations
)


data class IncidentCompletionResponse(
    val options: List<String>
)

interface IncidentAnalysisApiService {
    @POST(BuildConfig.BACKEND_AI_ROUTE)
    suspend fun generateIncidentAnalysis(@Body request: IncidentAnalysisRequest): IncidentAnalysisResponse

    @GET("/health")
    suspend fun healthCheck(): String

    @POST("/incidents")
    suspend fun submitIncident(@Body incident: IncidentAnalysisResponse): IncidentSubmittingResponse
}

interface IncidentCompletionApiService {
    @POST(BuildConfig.BACKEND_COMPLETION_ROUTE)
    suspend fun findCompletion(@Body request: IncidentCompletionRequest): IncidentCompletionResponse
}