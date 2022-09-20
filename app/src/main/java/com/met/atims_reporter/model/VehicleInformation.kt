package com.met.atims_reporter.model

data class VehicleInformation(
    var plate_no: String = "",
    var color: String = "",
    var companyColor: String = "",
    var vehicle_type: String = "",
    var vehicleType: String = "",
    var vehicle_type_name: String = "",
    var colorResponce: ColorResponce = ColorResponce(),
    var motoristModel: VehicleMotoristModel = VehicleMotoristModel()
)