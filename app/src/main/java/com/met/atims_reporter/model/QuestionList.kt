package com.met.atims_reporter.model

data class QuestionList(
    var inspection_vehicle_answer_id: String,
    var company_id: String,
    var inspection_vehicle_question_id: String,
    var inspection_vehicle_answer: String,
    var inspection_vehicle_answer_comments: String,
    var inspection_vehicle_answer_image: String,
    var inspection_reports_id: String,
    var status: String,
    var reports_q_image: String,
    var created_by: String,
    var created_ts: String,
    var updated_by: String,
    var updated_ts: String,
    var inspection_vehicle_question: String
)