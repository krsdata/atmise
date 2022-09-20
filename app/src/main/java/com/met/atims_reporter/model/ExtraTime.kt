package com.met.atims_reporter.model

data class ExtraTime(
    var extraTimeReportId: String,
    var shiftId: String,
    var date: String,
    var startShiftTime: String,
    var endShiftTime: String,
    var shiftDuration: String,
    var overTimeStatus: String,
    var overTime: String,
    var extratimeStatus: String?,
    var statusId: String,
    var total_extra_time:String,
    var approved_extra_time:String
)