package com.met.atims_reporter.model

data class StartShiftRequest(
    val source: String = "MOB",
    var user_id: String = "",
    var companyId: String = "",
    var beats_zone_id: String = "",
    val status: String = "1",
    var state_id: String = "",
    var operationarea_id: String = "",
    var vehicle_id: String = "",
    var operationShifttimeId: String = ""
)