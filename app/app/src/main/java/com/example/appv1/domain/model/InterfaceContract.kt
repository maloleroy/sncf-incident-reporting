package com.example.appv1.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class VehicleType {
    R, M
}

enum class Level {
    H, B
}

enum class SignalementClass(val value: Int) {
    SECURITE(1),
    CONFORT(2),
    PROPRETE(3)
}

data class Signalement(
    // Required fields
    val vehicleType: VehicleType,
    val vehicleRank: Int,
    val level: Level?,
    val trainsetNumber: String,
    val courseId: String,
    val signalementClass: SignalementClass,
    val codePanne: String,
    val lcnCode: String,
    val reportDate: LocalDateTime,
    val location: String,
    val categoryLabel: String,
    val organLabel: String,
    val physicalAndFunctionalFailure: String,

    // Optional fields
    val svsiComment: String? = null,
    val precisionN1: String? = null,
    val precisionN2: String? = null,
    val precisionN3: String? = null,
    val subOrganLabel: String? = null
) {
    // You can add validation logic in the init block if needed
    init {
        require(vehicleRank > 0) { "Vehicle rank must be positive" }
        // Add other validations as needed
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toRequest(baseUrl: String): String {
        val params = listOf(
            "vehicleType=${vehicleType.name}",
            "vehicleRank=$vehicleRank",
            "level=${level?.name ?: ""}",
            "trainsetNumber=${trainsetNumber.encodeURL()}",
            "courseId=${courseId.encodeURL()}",
            "signalementClass=${signalementClass.value}",
            "codePanne=${codePanne.encodeURL()}",
            "lcnCode=${lcnCode.encodeURL()}",
            "reportDate=${reportDate.format(DateTimeFormatter.ISO_DATE_TIME).encodeURL()}",
            "location=${location.encodeURL()}",
            "categoryLabel=${categoryLabel.encodeURL()}",
            "organLabel=${organLabel.encodeURL()}",
            "physicalAndFunctionalFailure=${physicalAndFunctionalFailure.encodeURL()}",
            "svsiComment=${svsiComment?.encodeURL() ?: ""}",
            "precisionN1=${precisionN1?.encodeURL() ?: ""}",
            "precisionN2=${precisionN2?.encodeURL() ?: ""}",
            "precisionN3=${precisionN3?.encodeURL() ?: ""}",
            "subOrganLabel=${subOrganLabel?.encodeURL() ?: ""}"
        ).filter { !it.endsWith("=") } // Remove empty parameters

        return if (params.isEmpty()) baseUrl else "$baseUrl?${params.joinToString("&")}"
    }

    // Helper extension function for URL encoding
    fun String.encodeURL(): String = java.net.URLEncoder.encode(this, "UTF-8")
}