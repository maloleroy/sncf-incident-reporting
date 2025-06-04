package com.sncf.reports.api

import com.sncf.reports.BuildConfig
import com.sncf.reports.model.IncidentAnalysisRequest
import com.sncf.reports.model.IncidentAnalysisResponse
import com.sncf.reports.model.IncidentCompletionRequest
import com.sncf.reports.model.IncidentCompletionResponse
import com.sncf.reports.model.IncidentSubmittingResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


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