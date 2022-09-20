package com.met.atims_reporter.model

data class RoadSurface(
    var road_survey_id: String,
    var company_id: String,
    var road_survey: String,
    var status: String,
    var checked: Boolean = false
)