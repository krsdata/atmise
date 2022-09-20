package com.met.atims_reporter.model

data class ToolListRequest(
    var source: String = "MOB",
    var companyId: String
)