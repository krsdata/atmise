package com.met.atims_reporter.model

data class NotificationReadUnReadRequest(
    var source: String = "MOB",
    var companyId: String,
    var user_id: String,
    var notification_master_id:String,
    var is_read:String
)