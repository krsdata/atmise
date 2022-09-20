package com.met.atims_reporter.model

data class IncidentCloseRequest(
    var companyId: String,
    var userId: String,
    var source: String = "MOB",
    var incidentReportId: String,
    var incidentStatus: String = "Closed",
    var is_force_logout:String="",
    var timeUTC: String
)