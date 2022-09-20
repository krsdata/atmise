package com.met.atims_reporter.model

data class NotificationObject(
    var created_ts: String = "",
    var message: String,
    var notification_master_id:String,
    var title:String=""

)