package com.met.atims_reporter.model

data class IsLoggedInRequest(
    var deviceId: String,
    var userId: String
)