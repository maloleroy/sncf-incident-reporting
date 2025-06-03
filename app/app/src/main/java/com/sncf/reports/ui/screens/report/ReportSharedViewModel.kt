package com.sncf.reports.ui.screens.report

import androidx.lifecycle.ViewModel
import com.sncf.reports.api.IncidentAnalysisResponse

class ReportSharedViewModel : ViewModel() {
    var lastIncidentAnalysisResponse: IncidentAnalysisResponse? = null
    var trainType: String? = null
    var trainCar: String? = null
    var seatNumber: Int? = null
}