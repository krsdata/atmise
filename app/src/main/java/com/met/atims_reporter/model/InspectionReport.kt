package com.met.atims_reporter.model

data class InspectionReport(
    var inspection_reports_id: String,
    var company_id: String,
    var company_user_id: String,
    var vehicle_id: String,
    var odometer_reading: String,
    var inspection_reports_comment: String,
    var inspection_reports_image: String,
    var insurance_expiary_date: String,
    var registration_expiry_date: String,
    var state_inspection_expiry_date: String,
    var plate_number: String,
    var insurance_image: String,
    var registration_image: String,
    var inspection_image: String,
    var plate_image: String,
    var inspection_date: String,
    var inspection_time: String,
    var status: String,
    var operator_shift_time_details_id: String,
    var created_ts: String,
    var vehicleId: String,
    var first_name: String,
    var last_name: String
)