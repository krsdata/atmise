package com.met.atims_reporter.model

data class CrashReportListRequest(
    var source: String = "MOB",
    var user_id: String,
    var companyId: String
)