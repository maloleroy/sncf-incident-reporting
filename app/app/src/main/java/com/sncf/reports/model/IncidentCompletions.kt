package com.sncf.reports.model

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
