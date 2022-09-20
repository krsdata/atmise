package com.met.atims_reporter.model

data class DashboardMenuResponse(
    val getMenuList: ArrayList<HomeGridItem>,
    val shift_id: String,
    val vehicle_id: String,
    var vehicle_color: String,
    var vehicle_vin_no: String,
    var state_id: String,
    var state_name: String,
    var operation_area_id: String,
    var operation_area_name: String,
    var indicator_status: String,
    var vehicleId: String,
    var roleId: String,
    var role_name: String,
    var countUnreadNotification: Int,
    var break_time_id: String = "",
    var share_link: String = ""
)