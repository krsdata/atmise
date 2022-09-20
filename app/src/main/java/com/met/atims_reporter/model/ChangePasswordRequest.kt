package com.met.atims_reporter.model

data class ChangePasswordRequest(
    var user_id: String,
    var companyId: String,
    var password: String
)