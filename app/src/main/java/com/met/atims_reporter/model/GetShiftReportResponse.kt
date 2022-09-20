package com.met.atims_reporter.model

import com.met.atims_reporter.util.model.PatrollingOnSceneStatus

data class GetShiftReportResponse(
    val status: PatrollingOnSceneStatus,
    val operator_shift_time_details_id: String,
    val shiftStart: String,
    val shiftEnd: String,
    val totalDuration: String
)