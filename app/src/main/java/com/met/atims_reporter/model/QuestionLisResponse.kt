package com.met.atims_reporter.model

data class QuestionLisResponse(
    var preops_vehicle_question_id: String = "",
    var preops_vehicle_question: String = "",
    var comments: String = "",
    var imagePath: String? = null,
    var image: String = "",
    var answer: String? = null,
    var isCommentVisible:Boolean=false
)