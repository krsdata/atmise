package com.met.atims_reporter.model

data class ExtraTimeListRequest(
    var source: String = "MOB",
    var companyId: String,
    var userId: String,
    var startDate: String? = null,
    var endDate: String? = null
)