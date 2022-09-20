package com.met.atims_reporter.model

data class UpdateWazeInformationRequest(
    var source: String = "MOB",
    var user_id: String,
    var companyId: String,
    var timeUTC: String,
    var lat: String,
    var lng: String,
    var direction: String,
    var description: String
)