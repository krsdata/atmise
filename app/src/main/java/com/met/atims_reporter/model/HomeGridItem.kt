package com.met.atims_reporter.model

import android.graphics.drawable.Drawable
import com.met.atims_reporter.enums.HomeGridItems

data class HomeGridItem(
    val id: Int,
    val permissiontype_id: Int,
    val company_id: Int,
    val status: Int,
    val company_user_id: Int,
    val permission_label: String,
    var icon: Drawable,
    var title: String,
    var tag: HomeGridItems,
    var shareUrl: String = ""
)