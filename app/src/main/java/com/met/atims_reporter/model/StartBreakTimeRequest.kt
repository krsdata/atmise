package com.met.atims_reporter.model

import com.met.atims_reporter.util.DateUtil

data class StartBreakTimeRequest(
    var source: String = "MOB",
    var companyId: String,
    var userId: String,
    var user_id: String,
    var shiftId: String = "",
    var vehicle_id: String,
    var timeUTC:String? = DateUtil.getUTCTimeStringForServer()
)