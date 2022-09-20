package com.met.atims_reporter.model

data class DashboardMenuAccessRequest(
    var source: String = "MOB",
    var user_id: Int
)