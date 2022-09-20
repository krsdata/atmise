package com.met.atims_reporter.model

data class IncidentFieldListRequest(
    var source: String = "MOB",
    var companyId: String,
    var user_id: String,
    var incident_type_id: String
)