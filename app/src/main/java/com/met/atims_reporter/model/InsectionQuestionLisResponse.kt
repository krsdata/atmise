package com.met.atims_reporter.model

data class InsectionQuestionLisResponse(
    var inspection_vehicle_question_id: String = "",
    var inspection_vehicle_question: String = "",
    var name: String = "",
    var comments: String = "",
    var imagePath: String? = null,
    var image: String = "",
    var answer: String? = null
)