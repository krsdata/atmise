package com.met.atims_reporter.model

data class ExtraTimeRequest(
    var userId: String,
    var user_id: String,
    var companyId: String,
    var incident_id: String,
    var shiftId: String,
    var eventId: String,
    var shift_start:String,
    var shift_end:String,
    var total_extra_time:String,
    var tmcAuthorisation: String,
    var selectedReasonId: String,
    var userDefinedReason: String,
    var vehicle_id: String,
    var source: String = "MOB"
)

