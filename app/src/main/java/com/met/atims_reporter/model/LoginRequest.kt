package com.met.atims_reporter.model

data class LoginRequest(
    var source: String = "MOB",
    var device_type: String = "1",
    var device_token: String,
    var email: String,
    var password: String,
    var deviceId: String,
    var latitude: String,
    var longitude: String
)