package com.met.atims_reporter.model

import com.met.atims_reporter.ui.add_inspection.step_one.AddInspectionStepOne

data class InspectionFinalRequest(
    var source: String = "MOB",
    var companyId: String = "",
    var date: String = "",
    var time: String = "",
    var userId: String = "",
    var user_id: String = "",
    var inspectedBy: String,
    var vehicleId: String = "",
    var vehicle_id: String = "",
    var odoMeterData: String = "",
    var inspectionComment: String = "",
    var stateInspectionExp: String = "",
    var regExp: String = "",
    var insuranceExp: String = "",
    var inspectionImg: ArrayList<String> = ArrayList(),
    var inspectionsVehicleQuestions: ArrayList<InsectionQuestionLisResponse> = ArrayList(),
    var inspectionsTools: ArrayList<ToolListResponceInsp> = ArrayList(),
    var operatorShiftTimeDetailsId: String,
    var inspectionIns: String = "",
    var registrationImg: String = "",
    var inspectionState: String = "",
    var inspectionPlate: String = "",
    var otherViewOne: String = "",
    var otherViewTwo: String = "",
    var plate_number: String = "",
    var takingPictureFor: AddInspectionStepOne.TakingPictureFor = AddInspectionStepOne.TakingPictureFor.INSURANCE
)

/*
companyId": "2",
"userId": "15",
"source": "WEB",
"inspectedBy": "17",
"vehicleId": "7",
"date": "2020-04-12",
"time": "9:30am",
"odoMeterData": "1250",
"insuranceExp": "2020-04-04",
"regExp": "2020-04-02",
"stateInspectionExp": "2020-04-01",
"inspectionComment": "Test Inspection Comment",
"inspectionImg": "inspection_1588170060.jpg",*/
