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

data class IncidentSubmittingResponse(
    val status: String,
)

data class ConservedInformations(
    location: Optional[str] = None 
    category: Optional[str] = None 
    system: Optional[str] = None   
    precision1: Optional[str] = None 
    precision2: Optional[str] = None 
    precision3: Optional[str] = None 
    subSystem: Optional[str] = None
    failure: Optional[str] = None
)

class IncidentCompletionRequest(BaseModel):
    level: String
    selections: ConservedInformations


data class IncidentCompletionResponse(BaseModel):
    options: List[String]

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
    suspend fun completeIncident(@Body request: IncidentCompletionRequest): IncidentCompletionResponse
}