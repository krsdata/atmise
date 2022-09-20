package com.met.atims_reporter.model

data class RouteListRequest(
    var source: String = "MOB",
    var companyId: String,
    var user_id: String
)