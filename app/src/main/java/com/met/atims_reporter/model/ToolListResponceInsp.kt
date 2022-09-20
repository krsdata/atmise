package com.met.atims_reporter.model

data class ToolListResponceInsp(
    var tool_id: String = "",
    var tool_name: String = "",
    var status: String? = null,
    var tool_status: String? = null,
    var tool_quantity: String? = null
)