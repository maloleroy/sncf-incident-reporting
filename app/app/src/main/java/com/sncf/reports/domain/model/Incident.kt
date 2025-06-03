package com.sncf.reports.domain.model

data class IncidentLocation(
    val main: IncidentLocationMain,
    val precision1: IncidentLocationPrecision1 = IncidentLocationPrecision1.NONE,
    val precision2: IncidentLocationPrecision2 = IncidentLocationPrecision2.NONE,
    val precision3: IncidentLocationPrecision3 = IncidentLocationPrecision3.NONE,
    val id: Int = 0,
) {
    override fun toString(): String {
        return "IncidentLocation(main=$main, precision1=$precision1, precision2=$precision2, precision3=$precision3, id=$id)"
    }

    fun precise1(precision1: IncidentLocationPrecision1): IncidentLocation {
        return this.copy(precision1 = precision1)
    }

    fun precise2(precision2: IncidentLocationPrecision2): IncidentLocation {
        return this.copy(precision2 = precision2)
    }

    fun precise3(precision3: IncidentLocationPrecision3): IncidentLocation {
        return this.copy(precision3 = precision3)
    }

    fun preciseId(id: Int): IncidentLocation {
        return this.copy(id = id)
    }

    fun isDone(): Boolean {
        return precision1 != IncidentLocationPrecision1.NONE && precision2 != IncidentLocationPrecision2.NONE && precision3 != IncidentLocationPrecision3.NONE
    }
}

enum class IncidentLocationMain {
    ASCT_LOCAL,
    SERVICE_LOCAL,
    NURSERY,
    OFFICE_BAR,
    PLACE,
    ACCESS_PLATFORM,
    INTER_CIRCULATION_PLATFORM,
    ROOM,
    BAR_ROOM,
    TOILET,
}

enum class IncidentLocationPrecision1 {
    NONE,
    CORRIDOR,
    CUSTOMS,
    RIGHT,
    LEFT,
    OFFICE_SPACE,
    FAMILY,
    KIOSK,
    RIGHT_RESTORATION_SPACE,
    PLATFORM,
    RECEPTION_INFO_PLACE,
    MAIN,
    PSH,
    BICYCLE,
    TRAVELERS,
}

enum class IncidentLocationPrecision2 { NONE }

enum class IncidentLocationPrecision3 { NONE }

enum class SubSystem {
    NONE,
}

enum class IncidentFailure {
    NONE,
}

data class Incident(
    val lastUpdate: Long = System.currentTimeMillis(),
    val location: IncidentLocation,
    val subSystem: SubSystem = SubSystem.NONE,
    val failure: IncidentFailure = IncidentFailure.NONE,
    val comment: String = "",
    val sealed: Boolean = false,
    val t4Call: Boolean = false,
) {
    override fun toString(): String {
        return "Incident(location=$location, subSystem=$subSystem, failure=$failure, comment='$comment', sealed=$sealed, t4Call=$t4Call)"
    }

    fun uid(): String {
        return "${lastUpdate}_${location.main.name}_${location.precision1.name}_${location.precision2.name}_${location.precision3.name}"
    }
}
