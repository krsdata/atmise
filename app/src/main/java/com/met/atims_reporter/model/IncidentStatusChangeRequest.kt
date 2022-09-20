package com.met.atims_reporter.model

data class IncidentStatusChangeRequest(
    var companyId: String,
    var userId: String,
    var source: String = "MOB",
    var incidentsReportId: String,
    var incident_status: String,
    var deny_reason: String = "",
    var timeUTC: String
)