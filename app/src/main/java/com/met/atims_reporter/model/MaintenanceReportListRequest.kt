package com.met.atims_reporter.model

data class MaintenanceReportListRequest(
    var source: String = "MOB",
    var user_id: String,
    var companyId: String
)