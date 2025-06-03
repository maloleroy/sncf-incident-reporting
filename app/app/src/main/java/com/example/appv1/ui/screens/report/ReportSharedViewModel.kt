package com.example.appv1.ui.screens.report

import androidx.lifecycle.ViewModel
import com.example.appv1.api.IncidentAnalysisResponse

class ReportSharedViewModel : ViewModel() {
    var lastIncidentAnalysisResponse: IncidentAnalysisResponse? = null
    var trainType: String? = null
    var trainCar: String? = null
    var seatNumber: Int? = null
}