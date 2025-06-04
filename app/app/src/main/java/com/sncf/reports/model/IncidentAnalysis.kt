package com.sncf.reports.model

import java.time.Instant
import java.util.UUID

data class IncidentAnalysisRequest(
    val uuid: UUID,
    val timestamp: Instant,
    val trainType : String,
    val trainCar : String,
    val seatNumber : Int,
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
