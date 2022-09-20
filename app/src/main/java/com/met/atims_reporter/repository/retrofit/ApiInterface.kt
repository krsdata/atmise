package com.met.atims_reporter.repository.retrofit

import com.met.atims_reporter.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {

    @POST("login")
    fun login(@Body loginRequest: LoginRequest)
            : Observable<Response<ResponseBody>>

    @POST("menuaccess")
    fun dashboardMenus(@Body dashboardMenuAccessRequest: DashboardMenuAccessRequest)
            : Observable<Response<ResponseBody>>

    @POST("stateList")
    fun getStateList(@Body stateListRequest: StateListRequest)
            : Observable<Response<ResponseBody>>

    @POST("get/lane/list")
    fun getLaneList(@Body mLaneListRequest: LaneListRequest)
            : Observable<Response<ResponseBody>>

    @POST("get/maintenance-request-type/list")
    fun getMaintenanceRequestTypeList(@Body mLaneListRequest: LaneListRequest)
            : Observable<Response<ResponseBody>>

    @POST("operationareaList")
    fun getOperationAreaList(@Body operationList: OperationListRequest)
            : Observable<Response<ResponseBody>>

    @POST("getZoneArea")
    fun getZoneList(@Body zoneList: ZoneListRequest)
            : Observable<Response<ResponseBody>>

    @POST("vehicleList")
    fun getVehicleList(@Body vehicleList: VehicleListRequest)
            : Observable<Response<ResponseBody>>

    @POST("get-motorist-type-list")
    fun getMotiristVehicleType(@Body motoristTypeListRequest: MotoristTypeListRequest)
            : Observable<Response<ResponseBody>>

    @POST("get-surface-list")
    fun getRoadSurfaces(@Body roadSurfaceTypeListRequest: RoadSurfaceTypeListRequest)
            : Observable<Response<ResponseBody>>

    @POST("motorists-vehicle-list")
    fun getMotoristVehicles(@Body motoristVehicleListRequest: MotoristVehicleListRequest)
            : Observable<Response<ResponseBody>>

    @POST("operatorShiftTimeStart")
    fun startShift(@Body startShiftRequest: StartShiftRequest)
            : Observable<Response<ResponseBody>>

    @POST("operatorShiftTimeEnd")
    fun endShift(@Body endShiftRequest: EndShiftRequest)
            : Observable<Response<ResponseBody>>

    @POST("fuelReportListAll")
    fun getFuelReportList(@Body fuelListrequest: FuelListRequest)
            : Observable<Response<ResponseBody>>

    @POST("fuelReportAdd")
    @Multipart
    fun addFuelReport(
        @Part("source") source: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("shift_id") shift_id: RequestBody,
        @Part("companyId") companyId: RequestBody,
        @Part("vehicle_id") vehicle_id: RequestBody,
        @Part("cost_per_galon") cost_per_galon: RequestBody,
        @Part("total_cost") total_cost: RequestBody,
        @Part("fuel_quantity") fuel_quantity: RequestBody,
        @Part("refueling_date") refueling_date: RequestBody,
        @Part("refueling_time") refueling_time: RequestBody,
        @Part("fuel_type") fuel_type: RequestBody,
        @Part("odo_meter_reading") odo_meter_reading: RequestBody,
        @Part("fuel_taken_tank") fuel_taken_tank: RequestBody,
        @Part("fuel_taken_can") fuel_taken_can: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("note") note: RequestBody,
        @Part("status") status: RequestBody,
        @Part("report_for") receiptFor: RequestBody,
        @Part receipt: MultipartBody.Part? = null,
        @Part pump: MultipartBody.Part? = null
    ): Observable<Response<ResponseBody>>

    @POST("incidentsDetails")
    fun getIncidentDetailsList(@Body incidentListRequest: IncidentListRequest)
            : Observable<Response<ResponseBody>>

    @POST("incidentStatusUpdate")
    fun changeIncidentStatus(@Body incidentStatusChangeRequest: IncidentStatusChangeRequest)
            : Observable<Response<ResponseBody>>

    @POST("close-incident")
    fun closeIncidentStatus(@Body incidentCloseRequest: IncidentCloseRequest)
            : Observable<Response<ResponseBody>>


    @POST("colorList")
    fun getColorList(@Body colorListRequest: ColorListRequest)
            : Observable<Response<ResponseBody>>

    @POST("firstresponderList")
    fun getFirstResponce(@Body firstResponceList: FirstResponceUnitRequest)
            : Observable<Response<ResponseBody>>


    @POST("secondarycrashList")
    fun getSecendaryCrashInvolvedList(@Body secendaryCrashRequest: SecendaryCrashRequest)
            : Observable<Response<ResponseBody>>

    @POST("propertydamageList")
    fun getPropertyDamage(@Body propertyDamage: PropertyDamageRequest)
            : Observable<Response<ResponseBody>>

    @POST("trafficdirectionList")
    fun getTrafficList(@Body trafficeListRequest: TrafficeListRequest)
            : Observable<Response<ResponseBody>>

    @POST("getRouteList")
    fun getRouteList(@Body propertyDamage: RouteListRequest)
            : Observable<Response<ResponseBody>>

    @POST("vehicletypeList")
    fun getVehicelType(@Body vehicleListRequest: VehicleTypeListRequest)
            : Observable<Response<ResponseBody>>

    @POST("incidenttypeList")
    fun getIncidentType(@Body incidentType: IncidentTypeListRequest)
            : Observable<Response<ResponseBody>>

    @POST("assistList")
    fun getAssistList(@Body incidentType: AssectTypeListRequest)
            : Observable<Response<ResponseBody>>

    @POST("getResponderUnit")
    fun getFirstResponderUnitNo(@Body firstResponderUnitNo: FirstResponceUnitRequest)
            : Observable<Response<ResponseBody>>

    @POST("putIncidents")
    @Multipart
    fun addOrUpdateIncidents(
        @Part("incidentReportId") incidentReportId: RequestBody? = null,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("callAt") callAt: RequestBody,
        @Part("callStarted") callStarted: RequestBody,
        @Part("callComplete") callComplete: RequestBody,
        @Part("incidentTime") incidentTime: RequestBody,
        @Part("incidentType") incidentType: RequestBody,
        @Part("trafficDirection") trafficDirection: RequestBody,
        @Part("mileMaker") mileMaker: RequestBody,
        @Part("propertyDamage") propertyDamage: RequestBody,
        @Part("crashInvolced") crashInvolced: RequestBody,
        @Part("firstResponder") firstResponder: RequestBody,
        @Part("firstResponderUnit") firstResponderUnit: RequestBody,
        @Part("roadSurver") roadSurver: RequestBody,
        @Part("laneLocation") laneLocation: RequestBody,
        @Part("personTransported") personTransported: RequestBody,
        @Part("companyColor") companyColor: RequestBody,
        @Part("vehicleType") vehicleType: RequestBody,
        @Part("vehicle_id") vehicleId: RequestBody,
        @Part("assistType") assistType: RequestBody,
        @Part("comments") comments: RequestBody,
        @Part("actionStatus") actionStatus: RequestBody,
        @Part("userId") userId: RequestBody,
        @Part("companyId") companyId: RequestBody,
        @Part("source") source: RequestBody,
        @Part incedent_photo: MultipartBody.Part? = null,
        @Part incedent_video: MultipartBody.Part? = null,
        @Part("shift_id") shift_id: RequestBody,
        @Part("plate_no") plate_no: RequestBody,
        @Part("note") note: RequestBody,
        @Part("incident_status") incidentStatus: RequestBody,
        @Part("timeUTC") timeUTC: RequestBody,
        @Part("direction") direction: RequestBody,
        @Part("description") description: RequestBody,
        @Part("companyRoute") companyRoute: RequestBody,
        @Part("vehicleQty") vehicleQty: RequestBody,
        @Part("vehicleInformation") vehicleInformation: RequestBody,
        @Part("vendor_id") vendorId: RequestBody? = null,
        @Part("contract_id") contractId: RequestBody? = null,
        @Part("ramp_lane") rampLane: RequestBody,
        @Part("travel_lanes_blocked") travel_lanes_blocked: RequestBody,
        @Part("lane_restoration_time") lane_restoration_time: RequestBody,
        @Part("incident_no") incident_no: RequestBody
    ): Observable<Response<ResponseBody>>

    @POST("questionsList")
    fun getPreOpsQuestions(@Body questionListRequest: QuestionListRequest)
            : Observable<Response<ResponseBody>>

    @POST("getToolsList")
    fun getToolsQuestionsList(@Body toolListRequest: ToolListRequest)
            : Observable<Response<ResponseBody>>

    @POST("preopsUploadimage")
    @Multipart
    fun uploadPreOpsFile(
        @Part photo: MultipartBody.Part,
        @Part("companyId") companyId: RequestBody
    ): Observable<Response<ResponseBody>>

    @POST("uploadInspectionImage")
    @Multipart
    fun uploadInspectionFile(
        @Part photo: ArrayList<MultipartBody.Part>,
        @Part("companyId") companyId: RequestBody
    ): Observable<Response<ResponseBody>>

    @POST("savePreOpsReportData")
    fun postFinalPreOpsData(@Body preOpsFinalRequest: PreOpsFinalRequest)
            : Observable<Response<ResponseBody>>

    @POST("saveInspectionData")
    fun postFinalInspctionData(@Body inspectionFinalRequest: InspectionFinalRequest)
            : Observable<Response<ResponseBody>>

    @POST("crashReportListAll")
    fun getCrashReportList(@Body crashReportListRequest: CrashReportListRequest)
            : Observable<Response<ResponseBody>>

    @POST("crashReportAddEdit")
    fun addCrashReport(@Body crashReport: CrashReport)
            : Observable<Response<ResponseBody>>

    @POST("crashReportUploadImage")
    @Multipart
    fun uploadCrashReportFiles(
        @Part exterior: ArrayList<MultipartBody.Part>,
        @Part interior: ArrayList<MultipartBody.Part>,
        @Part vin: ArrayList<MultipartBody.Part>,
        @Part thirdParty: ArrayList<MultipartBody.Part>,
        @Part("source") source: RequestBody,
        @Part("crash_report_data_id") crash_report_data_id: RequestBody,
        @Part("user_id") user_id: RequestBody
    ): Observable<Response<ResponseBody>>

    @POST("maintenanceListAll")
    fun getMaintenanceReportList(@Body maintenanceReportListRequest: MaintenanceReportListRequest)
            : Observable<Response<ResponseBody>>

    @POST("servicTypeListAll")
    fun getServiceTypes(@Body serviceTypeRequest: ServiceTypeRequest)
            : Observable<Response<ResponseBody>>

    @POST("vendorListAll")
    fun getVendorsList(@Body vendorListRequest: VendorListRequest)
            : Observable<Response<ResponseBody>>

    @POST("get/vendor/list")
    fun getVendorsListForIncidents(@Body vendorListRequest: VendorListRequest)
            : Observable<Response<ResponseBody>>

    @POST("get/contract/list")
    fun getContactList(@Body contractListRequest: ContractListRequest)
            : Observable<Response<ResponseBody>>

    @POST("maintenanceAddEdit")
    @Multipart
    fun addMaintenanceReport(
        @Part reciept: MultipartBody.Part?,
        @Part("source") source: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("companyId") companyId: RequestBody,
        @Part("modifier_name") modifier_name: RequestBody,
        @Part("modifier_email") modifier_email: RequestBody,
        @Part("modifer_role") modifer_role: RequestBody,
        @Part("created_via") created_via: RequestBody,
        @Part("vehicle_id") vehicle_id: RequestBody,
        @Part("vin_number") vin_number: RequestBody,
        @Part("request_type_id") request_type_id: RequestBody,
        @Part("state_id") state_id: RequestBody,
        @Part("state_name") state_name: RequestBody,
        @Part("contract_period") contract_period: RequestBody,
        @Part("report_date") report_date: RequestBody,
        @Part("service_type_id") service_type_id: RequestBody,
        @Part("mileage") mileage: RequestBody,
        @Part("service_cost") service_cost: RequestBody,
        @Part("used_labour_hour") used_labour_hour: RequestBody,
        @Part("used_labour_min") used_labour_min: RequestBody,
        @Part("vendor_id") vendor_id: RequestBody,
        @Part("vendor_name") vendor_name: RequestBody,
        @Part("repair_description") repair_description: RequestBody,
        @Part("note") note: RequestBody
        /*@Part("maintenance_report_data_id") maintenance_report_data_id: RequestBody,
        @Part("report_status") report_status: RequestBody*/
    ): Observable<Response<ResponseBody>>

    @POST("faq")
    fun getFaqList(@Body fagRequest: FaqRequest)
            : Observable<Response<ResponseBody>>

    @POST("putsos")
    @Multipart
    fun sendSOS(
        @Part("companyId") companyId: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("vehicle_id") vehicle_id: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("location_lat") location_lat: RequestBody,
        @Part("location_long") location_long: RequestBody,
        @Part("source") source: RequestBody,
        @Part("status") status: RequestBody,
        @Part("sos_reasons_master_id") reasonid: RequestBody,
        @Part("operator_shift_time_details_id") operator_shift_time_details_id: RequestBody,
        @Part("timeUTC") timeUTC: RequestBody,
        @Part("message") message: RequestBody
    ): Observable<Response<ResponseBody>>

    @POST("logout")
    fun logoutApi(@Body logoutRequest: LogoutRequest)
            : Observable<Response<ResponseBody>>

    @POST("get/help/save")
    @Multipart
    fun adminHelp(
        @Part("companyId") companyId: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("message") message: RequestBody,
        @Part("subject") subject: RequestBody,
        @Part("location_lat") location_lat: RequestBody,
        @Part("location_long") location_long: RequestBody,
        @Part("source") source: RequestBody,
        @Part("vehicle_id") vehicleId: RequestBody,
        @Part("operator_shift_time_details_id") operator_shift_time_details_id: RequestBody
    ): Observable<Response<ResponseBody>>

    @POST("getShiftExtraTimeList")
    fun getExtraTimeList(@Body extraTimeListRequest: ExtraTimeListRequest)
            : Observable<Response<ResponseBody>>

    @POST("extraTimeReasonList")
    fun getExtraTimeCancelReasonList(@Body extraTimeCancelReasonsRequest: ExtraTimeCancelReasonsRequest)
            : Observable<Response<ResponseBody>>

    @POST("updateExtraTimeRequest")
    fun updateExtraTime(@Body extraTimeRequest: ExtraTimeRequest)
            : Observable<Response<ResponseBody>>

    @POST("userprofileEdit")
    @Multipart
    fun updateProfile(
        @Part("source") source: RequestBody,
        @Part("companyId") companyId: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("first_name") first_name: RequestBody,
        @Part("last_name") last_name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("address") address: RequestBody,
        @Part("city") city: RequestBody,
        @Part("zip") zip: RequestBody,
        @Part profile_image: MultipartBody.Part? = null
    ): Observable<Response<ResponseBody>>

    @POST("inspectionList")
    fun getInspectionList(@Body inspectionListRequest: InspectionListRequest)
            : Observable<Response<ResponseBody>>

    @POST("inspectionReport")
    fun getInspectionDetails(@Body inspectionDetailsRequest: InspectionDetailsRequest)
            : Observable<Response<ResponseBody>>

    @POST("inspectionQuestionsList")
    fun getInspectionPreOpsQuestions(@Body questionListRequest: QuestionListRequest)
            : Observable<Response<ResponseBody>>

    @POST("notificationSendListAll")
    fun getNotificationList(@Body notificatonRequest: NotificationRequest)
            : Observable<Response<ResponseBody>>

    @POST("getShiftReportData")
    fun getShiftReportList(@Body getShiftRequest: GetShiftReportRequest)
            : Observable<Response<ResponseBody>>

    @POST("resetPasswordOtpSend")
    fun requestOTPToResetPassword(@Body otpRequest: ForgotPasswordOTPRequest)
            : Observable<Response<ResponseBody>>

    @POST("resetPassword")
    fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest)
            : Observable<Response<ResponseBody>>

    @POST("sosReasonListAll")
    fun getSOSReasonList(@Body sosReasonList: SOSReasonRequest)
            : Observable<Response<ResponseBody>>

    @POST("getUserList")
    fun getUserList(@Body getUserList: GetUserListRequest)
            : Observable<Response<ResponseBody>>

    @POST("changeNotificationReadStatus")
    fun notificationReadUnRead(@Body getUserList: NotificationReadUnReadRequest)
            : Observable<Response<ResponseBody>>

    @POST("gpsData/put")
    fun sendLocationToServer(@Body locationUpdateRequest: LocationUpdateRequest)
            : Observable<Response<ResponseBody>>

    @POST("statusIndicator/put")
    fun changePatrollingStatus(@Body patrollingStatusChangeRequest: PatrollingStatusChangeRequest)
            : Observable<Response<ResponseBody>>

    @POST("saveBreakTime")
    fun startBreak(@Body startBreakTimeRequest: StartBreakTimeRequest)
            : Observable<Response<ResponseBody>>

    @POST("updateBreakTime")
    fun endBreak(@Body endBreakTimeRequest: EndBreakTimeRequest)
            : Observable<Response<ResponseBody>>

    @POST("getOperationShift")
    fun getShiftType(@Body shiftTypeRequest: ShiftTypeRequest)
            : Observable<Response<ResponseBody>>

    @POST("sendSurvey")
    fun sendSurvey(@Body sendSurveyRequest: SendSurveyRequest)
            : Observable<Response<ResponseBody>>

    @POST("changePassword")
    fun changePassword(@Body changePasswordRequest: ChangePasswordRequest)
            : Observable<Response<ResponseBody>>

    @POST("isLoggedIn")
    fun isLoggedIn(@Body isLoggedInRequest: IsLoggedInRequest)
            : Observable<Response<ResponseBody>>

    @POST("validate-version")
    fun checkIfUpdateRequired(
        @Body checkUpdateRequest: CheckUpdateRequest
    ): Observable<Response<ResponseBody>>

    @POST("v2/get-incident-form-fields")
    fun getIncidentFields(
        @Body incidentFieldListRequest: IncidentFieldListRequest
    ): Observable<Response<ResponseBody>>

    @POST("save-waze-info")
    fun updateWazeInformation(
        @Body updateWazeInformationRequest: UpdateWazeInformationRequest
    ): Observable<Response<ResponseBody>>
}
