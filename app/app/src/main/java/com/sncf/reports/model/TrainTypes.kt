package com.sncf.reports.model

val trainTypes = mapOf(
    "Dasye" to "DASYE_eau_incidents",
    "DUPLEX WC chimique" to "DUPLEX_WC_chimique_incidents",
    "DUPLEX WC EAU" to "DUPLEX_WC_EAU_incidents",
    "NEODUPLEX chimique" to "NEODUPLEX_chimique_incidents",
    "OCEANE LIKE" to "OCEANE_LIKE_incidents",
    "OUIGO1" to "OUIGO1_incidents",
    "OUIGO2" to "OUIGO2_incidents",
    "PLT" to "PLT_incidents",
    "POS" to "POS_incidents",
    "P DUPLEX" to "P_DUPLEX_incidents",
    "RDOM" to "RDOM_incidents",
    "RITA" to "RITA_incidents",
    "TANGO" to "TANGO_incidents",
    "TGV R TRI FO" to "TGV_R_TRI_FO_incidents",
    "TGV R TRI" to "TGV_R_TRI_incidents",
    "TRAIN 2N2 3UA LYRIA" to "TRAIN_2N2_3UA_LYRIA_incidents",
    "TRAIN 2N2 3UA" to "TRAIN_2N2_3UA_incidents",
    "TRAIN 2N2 3UFC" to "TRAIN_2N2_3UFC_incidents",
    "TRAIN 2N2 3UF" to "TRAIN_2N2_3UF_incidents",
    "TRAIN 2N2 3UH" to "TRAIN_2N2_3UH_incidents"
)

fun getTrainTypeByCode(code: String): String? {
    return trainTypes.entries.find { it.value == code }?.key
}
