package com.met.atims_reporter.model

import com.met.atims_reporter.ui.add_crash_report.step_three.adapter.PassengerInformationAdapter

data class CrashReportDriverInfo(
    var dirver_name: String = "",
    var driver_phone: String = "",
    var driver_address: String = "",
    var driver_licence_number: String = "",
    var vehicle_insurance_info: String = "",
    var insurance_expriary_date: String = "",
    var is_injury: String = "",
    var passenger_info: ArrayList<CrashReportPassengerInfo> = ArrayList(),
    var passengerAdapter: PassengerInformationAdapter? = null
)