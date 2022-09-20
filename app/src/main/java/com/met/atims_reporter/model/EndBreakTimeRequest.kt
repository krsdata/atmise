package com.met.atims_reporter.model

import com.met.atims_reporter.util.DateUtil

data class EndBreakTimeRequest(
    var companyId: String,
    var userId: String,
    var user_id: String,
    var shiftId: String,
    var breakTimeId: String,
    var vehicle_id: String,
    var source: String = "MOB",
    var timeUTC:String? = DateUtil.getUTCTimeStringForServer()
)