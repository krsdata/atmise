package com.met.atims_reporter.model

data class OperationListRequest(
    var source: String = "MOB",
    var companyId: String,
    var user_id: String,
    var stateId: String
)