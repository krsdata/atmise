package com.met.atims_reporter.model

data class AssectTypeListRequest(
    var source: String = "MOB",
    var companyId: String,
    var user_id: String
)