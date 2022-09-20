package com.met.atims_reporter.model

import java.util.*

data class LocationUpdate(
    val id: String = UUID.randomUUID().toString(),
    var latitude: String,
    var longitude: String
)