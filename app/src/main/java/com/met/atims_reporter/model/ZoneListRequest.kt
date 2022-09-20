package com.met.atims_reporter.model

data class ZoneListRequest(
    var source: String = "MOB",
    var companyId: String,
    var user_id: String,
    var stateId: String,
    var operationAreaId: String
)