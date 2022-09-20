package com.met.atims_reporter.model

data class ForgotPasswordOTPRequest(
    var source: String = "MOB",
    var email: String
)