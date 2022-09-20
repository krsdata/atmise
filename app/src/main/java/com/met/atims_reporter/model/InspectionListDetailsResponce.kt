package com.met.atims_reporter.model

data class InspectionListDetailsResponce(
    var reportUserData: InspectionReport,
    var reportImages : ArrayList<InspectionListDetailsResponceImage>,
    var questionsResult: ArrayList<QuestionList>,
    var toolsReport: ArrayList<ToolsList>
)

data class InspectionListDetailsResponceImage(
    var image: String
)