package com.met.atims_reporter.model

data class ToolListResponce(
    var tool_id: String = "",
    var tool_name: String = "",
    var status: String? = null,
    var answer: String? = null,
    var tool_quantity: String? = null
)