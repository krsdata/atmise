package com.met.atims_reporter.model

data class ResetPasswordRequest(
    var source: String = "MOB",
    var email: String,
    var password: String,
    var reset_password_otp: String
)