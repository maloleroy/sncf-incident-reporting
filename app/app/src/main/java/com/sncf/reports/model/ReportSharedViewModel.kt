package com.sncf.reports.model

import androidx.lifecycle.ViewModel

class ReportSharedViewModel : ViewModel() {
    var lastIncidentAnalysisResponse: IncidentAnalysisResponse? = null
    var trainType: String? = null
    var trainCar: String? = null
    var seatNumber: Int? = null
}