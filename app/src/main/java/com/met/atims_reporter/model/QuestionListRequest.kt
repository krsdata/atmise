package com.met.atims_reporter.model

data class QuestionListRequest(
    var source: String = "MOB",
    var companyId: String
)