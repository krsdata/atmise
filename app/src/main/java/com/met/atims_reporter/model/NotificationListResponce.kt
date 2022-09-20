package com.met.atims_reporter.model

import java.util.ArrayList

data class NotificationListResponce(
    var read:ArrayList<NotificationObject>,
    var unread:ArrayList<NotificationObject>
)