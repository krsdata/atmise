package com.met.atims_reporter.model

data class PatrollingStatusChangeRequest(
    var source: String = "MOB",
    var operator_shift_time_details_id: String,
    var vehicle_id: String,
    var indicator_status: String,
    var indicator_direction: String,
    var indicator_description: String,
    var lat: String,
    var lng: String,
    var timeUTC: String,
    var user_id: String
)