package com.met.atims_reporter.model

data class LocationUpdateRequest(
    var source: String = "MOB",
    var operator_shift_time_details_id: String,
    var vehicle_id: String,
    var user_id: String,
    var coordinates: ArrayList<LocationUpdate> = ArrayList()
)