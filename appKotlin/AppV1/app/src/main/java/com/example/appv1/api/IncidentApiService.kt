package com.example.appv1.api

import com.example.appv1.BuildConfig
import retrofit2.http.Body
import retrofit2.http.POST

data class IncidentAnalysisRequest(
    val trainType : String,
    val trainCar : String,
    val transcription : String
)

data class IncidentResponse(
    val message : String
)

interface IncidentApiService {
    @POST(BuildConfig.BACKEND_AI_ROUTE)
    suspend fun generateInterfaceAnalyse(@Body request: IncidentAnalysisRequest): IncidentResponse
}