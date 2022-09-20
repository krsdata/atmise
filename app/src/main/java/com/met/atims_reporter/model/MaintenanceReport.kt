package com.met.atims_reporter.model

data class MaintenanceReport(
    var state_id: String,
    var state_name: String,
    var vehicle_id: String,
    var vehicleId: String,
    var contract_period: String,
    var report_date: String,
    var service_type_id: String,
    var service_type: String,
    var mileage: String,
    var service_cost: String,
    var used_labour_hour: String,
    var used_labour_min: String,
    var vendor_id: String,
    var repair_description: String,
    var note: String,
    var reciept: String,
    var vendor_name: String
)