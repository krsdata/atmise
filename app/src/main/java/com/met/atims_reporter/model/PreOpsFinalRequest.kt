package com.met.atims_reporter.model

data class PreOpsFinalRequest(
    var source: String = "MOB",
    var companyId: String = "",
    var date: String = "",
    var time: String = "",
    var operatorShiftTimeDetailsId: String = "",
    var userId: String = "",
    var user_id: String = "",
    var vehicleId: String = "",
    var vehicle_id: String = "",
    var odometerReading: String = "",
    var reportsComment: String = "",
    var reportsImage: String = "",
    var preOpsVehicleQuestions: ArrayList<QuestionLisResponse> = ArrayList(),
    var preOpsTools: ArrayList<ToolListResponce> = ArrayList()
)