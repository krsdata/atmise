package com.met.atims_reporter.model

data class FuelListDetails(
    var fuel_master_id: String,
    var vehicle_id: String = "",
    var vehicleId: String = "",
    var cost_per_galon: String = "",
    var total_cost: String = "",
    var fuel_quantity: String = "",
    var refueling_date: String = "",
    var refueling_time: String = "",
    var fuel_type: String = "",
    var odo_meter_reading: String = "",
    var fuel_taken_tank: String = "",
    var fuel_taken_can: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var reciept: String = "",
    var pumping: String = "",
    var report_for: String,
    var note: String = "",
    var status: String
)
