package com.met.atims_reporter.model

data class PropertyDamageRequest(
    var source: String = "MOB",
    var companyId: String,
    var user_id: String
)