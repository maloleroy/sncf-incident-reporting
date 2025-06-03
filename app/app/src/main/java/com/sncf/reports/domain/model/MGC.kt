package com.sncf.reports.domain.model

enum class MgcMotive {
    AIR_CONDITIONING_FAILURE,
    SEAT_COMFORT,
    FIRST_CLASS_SEAT_INCLINATION,
    NONE, // NA in the Excel file
    PLUG_FAILURE,
    TOILET_FAILURE,
}

enum class ComfortPerimeter {
    I,
    NONE, // NA in the Excel file
    V,
}