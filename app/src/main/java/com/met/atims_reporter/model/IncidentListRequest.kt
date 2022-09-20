package com.met.atims_reporter.model

data class IncidentListRequest(
    var source: String = "MOB",
    var companyId: String,
    var user_id: String
    //var operationId: String
)