package com.met.atims_reporter.model

import com.met.atims_reporter.enums.SendSurveyVia

data class SendSurveyRequest(
    var companyId: String,
    var userName: String? = null,
    var userEmail: String,
    var userId: String,
    var via: SendSurveyVia
)