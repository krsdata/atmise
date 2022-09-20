package com.met.atims_reporter.model

data class ShiftTypeRequest(
    val source: String = "MOB",
    val user_id: String,
    val companyId: String
)