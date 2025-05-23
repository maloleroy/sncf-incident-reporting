package com.example.appv1.api

import com.example.appv1.BuildConfig
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

data class IncidentAnalysisRequest(
    val trainType : String,
    val trainCar : String,
    val transcription : String
)

data class IncidentAnalysisResponse(
    val location : String,
    val category : String,
    val system : String,
    val precision1 : String,
    val precision2 : String,
    val precision3 : String,
    val subSystem : String,
    val failure : String,
)

interface IncidentAnalysisApiService {
    @POST(BuildConfig.BACKEND_AI_ROUTE)
    suspend fun generateIncidentAnalysis(@Body request: IncidentAnalysisRequest): IncidentAnalysisResponse

    @GET("/health")
    suspend fun healthCheck(): String
}