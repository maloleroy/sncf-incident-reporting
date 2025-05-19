package com.example.appv1.api

import com.example.appv1.BuildConfig
import retrofit2.http.Body
import retrofit2.http.POST

data class IncidentObjectsListRequest(
    val trainType: String,
    val car: String,
)

data class IncidentObjectsListResponse(
    val objects: List<List<String>>,
)

interface IncidentObjectsListApiService {
    @POST(BuildConfig.BACKEND_OBJECTS_LIST_ROUTE)
    suspend fun getObjectsList(@Body request: IncidentObjectsListRequest): IncidentObjectsListResponse
}