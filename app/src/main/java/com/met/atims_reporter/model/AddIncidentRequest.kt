package com.met.atims_reporter.model

import com.met.atims_reporter.util.DateUtil.getUTCTimeStringForServer
import java.io.File

data class AddIncidentRequest(
    var id: String? = null,
    var latitude: String = "",
    var longitude: String = "",
    var callAt: String = "",
    var callStarted: String = "",
    var callComplete: String = "",
    var incidentTime: String = "",
    var incidentType: String,
    var trafficDirection: String,
    var mileMaker: String = "",
    var propertyDamage: String = "",
    var crashInvolced: String = "",
    var firstResponder: String = "",
    var firstResponderUnit: String = "",
    var roadSurver: String = "",
    var laneLocation: String = "",
    var personTransported: String = "",
    var companyColor: String = "",
    var vehicleType: String = "",
    var assistType: String = "",
    var comments: String = "",
    var actionStatus: String = "",
    var userId: String = "",
    var companyId: String = "",
    var source: String = "",
    var incedent_photo: File?,
    var incedent_video: File?,
    var shift_id: String,
    var plate_no: String,
    var incident_status: String = "On Scene",
    var note: String,
    var direction: String,
    var description: String,
    var companyRoute: String,
    var vehicleQty: String,
    var vehicleInformation: ArrayList<VehicleInformation> = ArrayList(),
    var vehicle_id: String = "",
    var vendor: Vendor? = null,
    var contract: Contract? = null,
    var timeUTC:String? = getUTCTimeStringForServer(),
    var ramp_lane:String? = null,
    var travel_lanes_blocked:String = "",
    var lane_restoration_time:String = "",
    var incident_no:String = ""
)