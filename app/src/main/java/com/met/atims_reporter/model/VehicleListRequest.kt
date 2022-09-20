package com.met.atims_reporter.model

data class VehicleListRequest(
    var source: String = "MOB",
    var companyId: String,
    var user_id: String,
    var stateId: String? = null,
    var operation_area_id: String? = null
)