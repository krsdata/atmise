package com.met.atims_reporter.model

data class EndShiftRequest(
    val source: String = "MOB",
    var user_id: String = "",
    var companyId: String = "",
    var operator_shift_time_details_id: String = "",
    val status: String = "1"
)