package com.met.atims_reporter.model

data class InspectionDetailsRequest(
    var inspectionReportId: String ,
    var companyId: String,
    var userId: String
)