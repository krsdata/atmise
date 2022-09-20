package com.met.atims_reporter.repository

import android.app.AlarmManager
import android.app.Application
import android.app.Notification
import android.app.PendingIntent
import android.content.*
import android.graphics.Bitmap
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.met.atims_reporter.BuildConfig
import com.met.atims_reporter.R
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.core.KeyWordsAndConstants.BREAK_TIME_ID
import com.met.atims_reporter.core.KeyWordsAndConstants.CURRENT_LAT
import com.met.atims_reporter.core.KeyWordsAndConstants.CURRENT_LONG
import com.met.atims_reporter.core.KeyWordsAndConstants.DEVICE_FCM_TOKEN
import com.met.atims_reporter.core.KeyWordsAndConstants.EMAIL
import com.met.atims_reporter.core.KeyWordsAndConstants.INDICATOR_STATUS
import com.met.atims_reporter.core.KeyWordsAndConstants.LANE_ID
import com.met.atims_reporter.core.KeyWordsAndConstants.LANE_NAME
import com.met.atims_reporter.core.KeyWordsAndConstants.MANAGER_WANTS_TO_GO_ON_PATROLLING
import com.met.atims_reporter.core.KeyWordsAndConstants.OPERATION_AREA_ID
import com.met.atims_reporter.core.KeyWordsAndConstants.OPERATION_AREA_NAME
import com.met.atims_reporter.core.KeyWordsAndConstants.PASSWORD
import com.met.atims_reporter.core.KeyWordsAndConstants.PATROLLING_MILE_LIMIT
import com.met.atims_reporter.core.KeyWordsAndConstants.PRE_OPS_STARTED
import com.met.atims_reporter.core.KeyWordsAndConstants.SAVED_INSPECTION_DATA
import com.met.atims_reporter.core.KeyWordsAndConstants.SHIFT_ID
import com.met.atims_reporter.core.KeyWordsAndConstants.STATE_ID
import com.met.atims_reporter.core.KeyWordsAndConstants.STATE_NAME
import com.met.atims_reporter.core.KeyWordsAndConstants.UNREAD_NOTIFICATION_COUNT
import com.met.atims_reporter.core.KeyWordsAndConstants.USER_LOCATIONS
import com.met.atims_reporter.core.KeyWordsAndConstants.VEHICLE_COLOR
import com.met.atims_reporter.core.KeyWordsAndConstants.VEHICLE_ID
import com.met.atims_reporter.core.KeyWordsAndConstants.VEHICLE_ID_TO_SHOW
import com.met.atims_reporter.core.KeyWordsAndConstants.VIN_NUMBER
import com.met.atims_reporter.enums.HomeGridItems
import com.met.atims_reporter.enums.SendSurveyVia
import com.met.atims_reporter.model.*
import com.met.atims_reporter.notification.NotificationMaster
import com.met.atims_reporter.repository.retrofit.ApiInterface
import com.met.atims_reporter.service.LocationMonitorForegroundService
import com.met.atims_reporter.ui.login.LoginActivity
import com.met.atims_reporter.util.DateUtil
import com.met.atims_reporter.util.VideoThumbnailUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import com.met.atims_reporter.util.repository.ResultType
import com.met.atims_reporter.util.repository.SuperRepository
import com.met.atims_reporter.util.repository.SuperRepositoryCallback
import com.sagar.android.logutilmaster.LogUtil
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.lang.NumberFormatException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


@Suppress("MemberVisibilityCanBePrivate")
class Repository(
    private val context: Application,
    private val logUtil: LogUtil,
    private val apiInterface: ApiInterface,
    private val notificationMaster: NotificationMaster
) : SuperRepository() {

    private val broadcastReceiverGetHomeGridItems = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            getHomeGridItems()
        }
    }

    init {
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        getHomeGridItems()

        context.registerReceiver(
            broadcastReceiverGetHomeGridItems,
            IntentFilter(
                "getHomeGrid"
            )
        )
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //private vars
    private val locationUpdatesSendToServerForUpdate: ArrayList<LocationUpdate> = ArrayList()
    public lateinit var role: String
    public lateinit var role_name: String
    public lateinit var vehicleSelectedByManager: VehicleList
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Mutable Live data
    val mutableLiveDataIncidentDetailsVideoThumb: MutableLiveData<Event<Bitmap>> = MutableLiveData()

    val mutableLiveDataLogin: MutableLiveData<Event<UserDetails>> = MutableLiveData()
    val mutableLiveDataLoginError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataDashboardMenus: MutableLiveData<Event<ArrayList<HomeGridItem>>> =
        MutableLiveData()
    val mutableLiveDataDashboardMenusError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataStateList: MutableLiveData<Event<ArrayList<StateList>>> =
        MutableLiveData()
    val mutableLiveDataStateListError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataLaneList: MutableLiveData<Event<ArrayList<LaneList>>> =
        MutableLiveData()
    val mutableLiveDataLaneListError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataMaintenanceRequestTypeList: MutableLiveData<Event<ArrayList<MaintenanceRequestTypeList>>> =
        MutableLiveData()
    val mutableLiveDataMaintenanceRequestTypeListError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataOperationList: MutableLiveData<Event<ArrayList<OperationList>>> =
        MutableLiveData()
    val mutableLiveDataOperationError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataZoneList: MutableLiveData<Event<ArrayList<ZoneList>>> =
        MutableLiveData()
    val mutableLiveDataZoneError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataVehicleList: MutableLiveData<Event<ArrayList<VehicleList>>> =
        MutableLiveData()
    val mutableLiveDataVehicleError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataStartShift: MutableLiveData<Event<StartShiftResponse>> =
        MutableLiveData()
    val mutableLiveDataStartShiftError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataEndShift: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataEndShiftError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataFuelList: MutableLiveData<Event<ArrayList<FuelListDetails>>> =
        MutableLiveData()
    val mutableLiveDataFuelError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataFuelTypeList: MutableLiveData<Event<ArrayList<FuelType>>> =
        MutableLiveData()
    val mutableLiveDataFuelTypeError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataAddFuelReort: MutableLiveData<Event<Result>> =
        MutableLiveData()
    val mutableLiveDataAddFuelReortError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataIncidentListList: MutableLiveData<Event<ArrayList<IncidentDetails>>> =
        MutableLiveData()
    val mutableLiveDataIncidentListError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataRouteList: MutableLiveData<Event<ArrayList<RouteListResponce>>> =
        MutableLiveData()
    val mutableLiveDataRouteListError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataTrafficeDriectionList: MutableLiveData<Event<ArrayList<TraficeDirectionListResponce>>> =
        MutableLiveData()
    val mutableLiveDataTrafficeDirectionError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataPropertyDamagerList: MutableLiveData<Event<ArrayList<PropertyDamageResponce>>> =
        MutableLiveData()
    val mutableLiveDataPopertyDamageError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataSecondaryCrashList: MutableLiveData<Event<ArrayList<SecondaryCrashResponce>>> =
        MutableLiveData()
    val mutableLiveDataSecondaryError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataFirstResponceList: MutableLiveData<Event<ArrayList<FirstResponce>>> =
        MutableLiveData()
    val mutableLiveDataFirstResponceError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataColorList: MutableLiveData<Event<ArrayList<ColorResponce>>> =
        MutableLiveData()
    val mutableLiveDataColorError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataIncidentStatusUpdate: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataIncidentStatusUpdateError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveVehicleTypeList: MutableLiveData<Event<ArrayList<VehicleTypeResponce>>> =
        MutableLiveData()
    val mutableLiveVehicelTypeError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveIncidentTypeList: MutableLiveData<Event<ArrayList<IncidentTypeResponce>>> =
        MutableLiveData()
    val mutableLiveIncidentTypeError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveAssectList: MutableLiveData<Event<ArrayList<AssectTypeResponce>>> =
        MutableLiveData()
    val mutableLiveAssectListError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataCallList: MutableLiveData<Event<ArrayList<CallAtTimeSpinnerData>>> =
        MutableLiveData()
    val mutableLiveDataCallError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveFirstRespnderUnitList: MutableLiveData<Event<ArrayList<FirstReponderUnitNoResponce>>> =
        MutableLiveData()
    val mutableLiveFirstRespnderUnitError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveQuestionList: MutableLiveData<Event<ArrayList<QuestionLisResponse>>> =
        MutableLiveData()
    val mutableLiveQuestionError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataToolList: MutableLiveData<Event<ArrayList<ToolListResponce>>> =
        MutableLiveData()
    val mutableLiveDataToolListError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataToolListInsp: MutableLiveData<Event<ArrayList<ToolListResponceInsp>>> =
        MutableLiveData()
    val mutableLiveDataToolListErrorInsp: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataAddOrUpdateIncident: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataAddOrUpdateIncidentError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataCloseIncident: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataCloseIncidentError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataPreOpsImageUpload: MutableLiveData<Event<PreOpsFileUploadResponse>> =
        MutableLiveData()
    val mutableLiveDataPreOpsImageUploadError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataInspectinImageUpload: MutableLiveData<Event<ArrayList<String>>> =
        MutableLiveData()
    val mutableLiveDataInspectinImageUploadError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataFinalPreOps: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataFinalPreOpsError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataFinalInspection: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataFinalInspectionError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataCrashReportList: MutableLiveData<Event<ArrayList<CrashReport>>> =
        MutableLiveData()
    val mutableLiveDataCrashReportListError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataAddCrashReport: MutableLiveData<Event<AddCrashReportResponse>> =
        MutableLiveData()
    val mutableLiveDataAddCrashReportError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataCrashReportFilesUpload: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataCrashReportFilesUploadError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataMaintenanceReportList: MutableLiveData<Event<ArrayList<MaintenanceReport>>> =
        MutableLiveData()
    val mutableLiveDataMaintenanceReportListError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataServiceTypes: MutableLiveData<Event<ArrayList<ServiceType>>> =
        MutableLiveData()
    val mutableLiveDataServiceTypesError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataVendors: MutableLiveData<Event<ArrayList<Vendor>>> =
        MutableLiveData()
    val mutableLiveDataVendorsError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataAddMaintenanceReport: MutableLiveData<Event<AddMaintenanceReportResponse>> =
        MutableLiveData()
    val mutableLiveDataAddMaintenanceReportError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataFaqList: MutableLiveData<Event<ArrayList<FaqResponce>>> =
        MutableLiveData()
    val mutableLiveDataFaqError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataSendSOS: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataSendSOSError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataLogout: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataLogoutError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataAdminHelpSuccess: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataAdminHelpError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataExtraTimeList: MutableLiveData<Event<ArrayList<ExtraTime>>> =
        MutableLiveData()
    val mutableLiveDataExtraTimeListError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataUpdateProfile: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataUpdateProfileError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataExtraTimeCancelReasons: MutableLiveData<Event<ArrayList<ExtraTimeCancelReason>>> =
        MutableLiveData()
    val mutableLiveDataExtraTimeCancelReasonsError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataFirebaseMessagingToken: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataFirebaseMessagingTokenError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataInspectionList: MutableLiveData<Event<ArrayList<InspectionListResponce>>> =
        MutableLiveData()
    val mutableLiveDataInspectionListError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataInspectionDeatils: MutableLiveData<Event<InspectionListDetailsResponce>> =
        MutableLiveData()
    val mutableLiveDataInspectionDeatilsError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveInsQuestionList: MutableLiveData<Event<ArrayList<InsectionQuestionLisResponse>>> =
        MutableLiveData()
    val mutableLiveInsQuestionListError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataExtraTimeRequest: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataExtraTimeRequestError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataNotificationList: MutableLiveData<Event<NotificationListResponce>> =
        MutableLiveData()
    val mutableLiveDataNotificationListError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataShiftReportList: MutableLiveData<Event<ArrayList<GetShiftReportResponse>>> =
        MutableLiveData()
    val mutableLiveDataShiftReportListError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataOTPRequestForResetPassword: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataOTPRequestForResetPasswordError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataResetPassword: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataResetPasswordError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataSOSReasonList: MutableLiveData<Event<ArrayList<SOSReasonListResponce>>> =
        MutableLiveData()
    val mutableLiveDataSOSReasonListError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataGetUserList: MutableLiveData<Event<ArrayList<GetUserListResponce>>> =
        MutableLiveData()
    val mutableLiveDataGetUserListError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataNotificationReadUnReadRequest: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataNotificationReadUnReadRequestError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataPermissionToEndShift: MutableLiveData<Event<String>> = MutableLiveData()
    val mutableLiveDataPermissionToEndShiftError: MutableLiveData<Event<Result>> = MutableLiveData()

    val mutableLiveDataPatrollingStatusChange: MutableLiveData<Event<String>> = MutableLiveData()
    val mutableLiveDataPatrollingStatusChangeError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataStartBreak: MutableLiveData<Event<StartBreakTimeResponse>> =
        MutableLiveData()
    val mutableLiveDataStartBreakError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataEndBreak: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataEndBreakError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataShiftType: MutableLiveData<Event<ArrayList<ShiftType>>> =
        MutableLiveData()
    val mutableLiveDataShiftTypeError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataSendSurvey: MutableLiveData<Event<SendSurveyResponse>> =
        MutableLiveData()
    val mutableLiveDataSendSurveyError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataChangePaassword: MutableLiveData<Event<String>> =
        MutableLiveData()
    val mutableLiveDataChangePaasswordError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataCheckVersion: MutableLiveData<Event<CheckVersionResponse>> =
        MutableLiveData()
    val mutableLiveDataCheckVersionError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataMotoristType: MutableLiveData<Event<ArrayList<VehicleMotoristModel>>> =
        MutableLiveData()
    val mutableLiveDataMotoristTypeError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataRoadSurfaces: MutableLiveData<Event<ArrayList<RoadSurface>>> =
        MutableLiveData()
    val mutableLiveDataRoadSurfacesError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataMotoristVehiclesList: MutableLiveData<Event<ArrayList<MotoristVehicleModel>>> =
        MutableLiveData()
    val mutableLiveDataMotoristVehiclesListError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataVendorsForIncidents: MutableLiveData<Event<ArrayList<Vendor>>> =
        MutableLiveData()
    val mutableLiveDataVendorsForIncidentsError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataContactList: MutableLiveData<Event<ArrayList<Contract>>> =
        MutableLiveData()
    val mutableLiveDataContactListError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataIncidentFieldsList: MutableLiveData<Event<ArrayList<IncidentFieldItem>>> =
        MutableLiveData()
    val mutableLiveDataIncidentFieldsListError: MutableLiveData<Event<Result>> =
        MutableLiveData()

    val mutableLiveDataUpdateWazeInformation: MutableLiveData<Event<String>> = MutableLiveData()
    val mutableLiveDataUpdateWazeInformationError: MutableLiveData<Event<Result>> =
        MutableLiveData()
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Api Calls
    fun getFirebaseToken() {
        FirebaseInstallations.getInstance().id
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    logUtil.logV(
                        """
                            Failed to get firebase instance id:
                            ${task.exception}
                        """.trimIndent()
                    )
                    mutableLiveDataFirebaseMessagingTokenError.postValue(
                        Event(
                            Result(
                                error_code = 400,
                                message = "Can not fetch firebase token. Please try again after sometime.",
                                result = ResultType.FAIL
                            )
                        )
                    )
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                //val token = task.result?.token
                val token = FirebaseInstallations.getInstance().getToken(false).result.token

                // Log and toast
                logUtil.logV(
                    """
                        Got new firebase messaging token:
                        $token
                    """.trimIndent()
                )

                getSharedPref()
                    .edit()
                    .putString(
                        DEVICE_FCM_TOKEN,
                        token
                    )
                    .apply()

                mutableLiveDataFirebaseMessagingToken.postValue(
                    Event(
                        token!!
                    )
                )
            })
            .addOnFailureListener {
                logUtil.logV(
                    """
                        error in getting firebase token :
                        ${it.printStackTrace()}
                    """.trimIndent()
                )
            }
    }

    fun login(loginRequest: LoginRequest) {
        getSharedPref()
            .edit()
            .putString(
                EMAIL,
                loginRequest.email
            )
            .apply()
        getSharedPref()
            .edit()
            .putString(
                PASSWORD,
                loginRequest.password
            )
            .apply()
        makeApiCall(
            observable = apiInterface.login(loginRequest),
            responseJsonKeyword = "userdetails",
            successMutableLiveData = mutableLiveDataLogin,
            errorMutableLiveData = mutableLiveDataLoginError,
            callback = object : SuperRepositoryCallback<UserDetails> {
                override fun success(result: UserDetails) {
                    super.success(result)

                    saveUserDataToPref(result)

                    getHomeGridItems()
                }
            }
        )
    }

    fun getStateList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getStateList(
                    StateListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "stateList",
                successMutableLiveData = mutableLiveDataStateList,
                errorMutableLiveData = mutableLiveDataStateListError
            )
        }
    }

    fun getLaneList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getLaneList(
                    LaneListRequest(
                        companyId = it.company_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                //canReadOnlyOnce = false,
                successMutableLiveData = mutableLiveDataLaneList,
                errorMutableLiveData = mutableLiveDataLaneListError
            )
        }
    }

    fun getMaintenanceRequestTypeList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getMaintenanceRequestTypeList(
                    LaneListRequest(
                        companyId = it.company_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                //canReadOnlyOnce = false,
                successMutableLiveData = mutableLiveDataMaintenanceRequestTypeList,
                errorMutableLiveData = mutableLiveDataMaintenanceRequestTypeListError
            )
        }
    }

    fun getVehicleList(
        stateId: String? = null,
        operationAreaId: String? = null,
        zoneId: String? = null
    ) {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getVehicleList(
                    VehicleListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString(),
                        stateId = stateId,
                        operation_area_id = operationAreaId
                    )
                ),
                responseJsonKeyword = "data",
                canReadOnlyOnce = false,
                successMutableLiveData = mutableLiveDataVehicleList,
                errorMutableLiveData = mutableLiveDataVehicleError
            )
        }
    }

    fun getOperationList(stateId: String) {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getOperationAreaList(
                    OperationListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString(),
                        stateId = stateId
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataOperationList,
                errorMutableLiveData = mutableLiveDataOperationError
            )
        }
    }

    fun getZoneList(stateId: String, operationAreaId: String) {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getZoneList(
                    ZoneListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString(),
                        stateId = stateId,
                        operationAreaId = operationAreaId
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataZoneList,
                errorMutableLiveData = mutableLiveDataZoneError
            )
        }
    }


    fun getHomeGridItems(doneCall: (() -> Unit)? = null) {
        getUserData()?.let {
            checkIfLoggedIn()
            makeApiCall(
                observable = apiInterface.dashboardMenus(
                    DashboardMenuAccessRequest(
                        user_id = it.user_id
                    )
                ),
                responseJsonKeyword = "dashboardData",
                errorMutableLiveData = mutableLiveDataDashboardMenusError,
                callback = object : SuperRepositoryCallback<DashboardMenuResponse> {
                    override fun success(result: DashboardMenuResponse) {
                        super.success(result)

                        this@Repository.role = result.roleId
                        this@Repository.role_name = result.role_name

                        getSharedPref()
                            .edit()
                            .putString(
                                SHIFT_ID,
                                result.shift_id
                            )
                            .apply()

                        getSharedPref()
                            .edit()
                            .putString(
                                VEHICLE_ID,
                                result.vehicle_id
                            )
                            .apply()

                        getSharedPref()
                            .edit()
                            .putString(
                                VEHICLE_ID_TO_SHOW,
                                result.vehicleId
                            )
                            .apply()


                        getSharedPref()
                            .edit()
                            .putString(
                                VEHICLE_COLOR,
                                result.vehicle_color
                            )
                            .apply()

                        getSharedPref()
                            .edit()
                            .putString(
                                VIN_NUMBER,
                                result.vehicle_vin_no
                            )
                            .apply()

                        getSharedPref()
                            .edit()
                            .putString(
                                STATE_ID,
                                result.state_id
                            )
                            .apply()

                        getSharedPref()
                            .edit()
                            .putString(
                                STATE_NAME,
                                result.state_name
                            )
                            .apply()

                        getSharedPref()
                            .edit()
                            .putString(
                                OPERATION_AREA_ID,
                                result.operation_area_id
                            )
                            .apply()

                        getSharedPref()
                            .edit()
                            .putString(
                                OPERATION_AREA_NAME,
                                result.operation_area_name
                            )
                            .apply()

                        getSharedPref()
                            .edit()
                            .putString(
                                INDICATOR_STATUS,
                                if (result.indicator_status == "") "1" else result.indicator_status
                            )
                            .apply()

                        getSharedPref()
                            .edit()
                            .putInt(
                                UNREAD_NOTIFICATION_COUNT,
                                result.countUnreadNotification
                            )
                            .apply()

                        getSharedPref()
                            .edit()
                            .putString(
                                BREAK_TIME_ID,
                                result.break_time_id
                            )
                            .apply()

                        result.getMenuList.forEach { item ->
                            when (item.permissiontype_id) {
                                HomeGridItems.START_SHIFT.getPermissionTypeId() -> {
                                    item.apply {
                                        icon = ResourcesCompat.getDrawable(
                                            context.resources,
                                            R.drawable.start_shift,
                                            null
                                        )!!
                                        title = HomeGridItems.START_SHIFT.getTitle()
                                        tag = HomeGridItems.START_SHIFT
                                    }
                                }
                                HomeGridItems.INCIDENTS.getPermissionTypeId() -> {
                                    item.apply {
                                        icon = ResourcesCompat.getDrawable(
                                            context.resources,
                                            R.drawable.incidents,
                                            null
                                        )!!
                                        title = "Incident"
                                        tag = HomeGridItems.INCIDENTS
                                    }
                                }
                                HomeGridItems.FUEL.getPermissionTypeId() -> {
                                    item.apply {
                                        icon = ResourcesCompat.getDrawable(
                                            context.resources,
                                            R.drawable.fuel,
                                            null
                                        )!!
                                        title = HomeGridItems.FUEL.getTitle()
                                        tag = HomeGridItems.FUEL
                                    }
                                }
                                HomeGridItems.PRE_OPS.getPermissionTypeId() -> {
                                    item.apply {
                                        icon = ResourcesCompat.getDrawable(
                                            context.resources,
                                            R.drawable.pre_ops,
                                            null
                                        )!!
                                        title = HomeGridItems.PRE_OPS.getTitle()
                                        tag = HomeGridItems.PRE_OPS
                                    }
                                }
                                HomeGridItems.EXTRA_TIME.getPermissionTypeId() -> {
                                    item.apply {
                                        icon = ResourcesCompat.getDrawable(
                                            context.resources,
                                            R.drawable.extra_time,
                                            null
                                        )!!
                                        title = HomeGridItems.EXTRA_TIME.getTitle()
                                        tag = HomeGridItems.EXTRA_TIME
                                    }
                                }
                                HomeGridItems.CRASH_REPORT.getPermissionTypeId() -> {
                                    item.apply {
                                        icon = ResourcesCompat.getDrawable(
                                            context.resources,
                                            R.drawable.crash_report,
                                            null
                                        )!!
                                        title = HomeGridItems.CRASH_REPORT.getTitle()
                                        tag = HomeGridItems.CRASH_REPORT
                                    }
                                }
                                HomeGridItems.SOS.getPermissionTypeId() -> {
                                    item.apply {
                                        icon = ResourcesCompat.getDrawable(
                                            context.resources,
                                            R.drawable.sos,
                                            null
                                        )!!
                                        title = HomeGridItems.SOS.getTitle()
                                        tag = HomeGridItems.SOS
                                    }
                                }
                                HomeGridItems.HELP.getPermissionTypeId() -> {
                                    item.apply {
                                        icon = ResourcesCompat.getDrawable(
                                            context.resources,
                                            R.drawable.help,
                                            null
                                        )!!
                                        title = HomeGridItems.HELP.getTitle()
                                        tag = HomeGridItems.HELP
                                    }
                                }
                                HomeGridItems.MAINTENANCE_REPORT.getPermissionTypeId() -> {
                                    item.apply {
                                        icon = ResourcesCompat.getDrawable(
                                            context.resources,
                                            R.drawable.maintenance_report,
                                            null
                                        )!!
                                        title = HomeGridItems.MAINTENANCE_REPORT.getTitle()
                                        tag = HomeGridItems.MAINTENANCE_REPORT
                                    }
                                }
                                HomeGridItems.INSPECTION.getPermissionTypeId() -> {
                                    item.apply {
                                        icon = ResourcesCompat.getDrawable(
                                            context.resources,
                                            R.drawable.inspection,
                                            null
                                        )!!
                                        title = HomeGridItems.INSPECTION.getTitle()
                                        tag = HomeGridItems.INSPECTION
                                    }
                                }
                                HomeGridItems.SEND_SURVEY.getPermissionTypeId() -> {
                                    item.apply {
                                        icon = ResourcesCompat.getDrawable(
                                            context.resources,
                                            R.drawable.survey_icon,
                                            null
                                        )!!
                                        title = HomeGridItems.SEND_SURVEY.getTitle()
                                        tag = HomeGridItems.SEND_SURVEY
                                    }
                                }
                            }
                        }

                        if (result.share_link.isNotEmpty())
                            result.getMenuList.add(
                                HomeGridItem(
                                    100,
                                    0,
                                    0,
                                    0,
                                    0,
                                    HomeGridItems.ADMIN.getTitle(),
                                    ResourcesCompat.getDrawable(
                                        context.resources,
                                        R.drawable.admin,
                                        null
                                    )!!,
                                    HomeGridItems.ADMIN.getTitle(),
                                    HomeGridItems.ADMIN,
                                    shareUrl = result.share_link
                                )
                            )

                        mutableLiveDataDashboardMenus.postValue(
                            Event(result.getMenuList)
                        )

                        doneCall?.let {
                            it()
                        }
                    }
                }
            )
        }
    }

    fun getFuelListing() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getFuelReportList(
                    FuelListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataFuelList,
                errorMutableLiveData = mutableLiveDataFuelError
            )
        }
    }

    fun startShift(startShiftRequest: StartShiftRequest) {
        makeApiCall(
            observable = apiInterface.startShift(startShiftRequest),
            responseJsonKeyword = "details",
            errorMutableLiveData = mutableLiveDataStartShiftError,
            callback = object : SuperRepositoryCallback<StartShiftResponse> {
                override fun success(result: StartShiftResponse) {
                    super.success(result)

                    getSharedPref()
                        .edit()
                        .putString(
                            SHIFT_ID,
                            result.operator_shift_time_details_id
                        )
                        .apply()

                    getHomeGridItems {
                        mutableLiveDataStartShift.postValue(
                            Event(
                                result
                            )
                        )
                    }
                }
            }
        )
    }

    fun endShift() {
        makeApiCall(
            doNotLookForResponseBody = true,
            observable = apiInterface.endShift(
                EndShiftRequest(
                    user_id = getUserData()!!.user_id.toString(),
                    companyId = getUserData()!!.company_id.toString(),
                    operator_shift_time_details_id = getSharedPref().getString(SHIFT_ID, "")!!
                )
            ),
            successMutableLiveData = mutableLiveDataEndShift,
            errorMutableLiveData = mutableLiveDataEndShiftError,
            callback = object : SuperRepositoryCallback<String> {
                override fun success(result: String) {
                    super.success(result)

                    getSharedPref()
                        .edit()
                        .remove(SHIFT_ID)
                        .apply()

                    getSharedPref()
                        .edit()
                        .remove(VEHICLE_ID)
                        .apply()

                    getSharedPref()
                        .edit()
                        .remove(VEHICLE_ID_TO_SHOW)
                        .apply()

                    getSharedPref()
                        .edit()
                        .remove(PRE_OPS_STARTED)
                        .apply()

                    getSharedPref()
                        .edit()
                        .remove(MANAGER_WANTS_TO_GO_ON_PATROLLING)
                        .apply()

                    getHomeGridItems()
                }
            }
        )
    }

    fun addFuelReport(
        vehicle_id: String,
        cost_per_galon: String,
        total_cost: String,
        fuel_quantity: String,
        refueling_date: String,
        refueling_time: String,
        fuel_type: String,
        odo_meter_reading: String,
        fuel_taken_tank: String,
        fuel_taken_can: String,
        latitude: String,
        longitude: String,
        note: String,
        status: String,
        isReceipt: Boolean,
        image: File?
    ) {
        makeApiCall(
            observable = apiInterface.addFuelReport(
                source = "MOB".toRequestBody(MultipartBody.FORM),
                user_id = getUserData()?.user_id.toString().toRequestBody(MultipartBody.FORM),
                shift_id = giveShiftId().toRequestBody(MultipartBody.FORM),
                companyId = getUserData()?.company_id.toString().toRequestBody(MultipartBody.FORM),
                vehicle_id = vehicle_id.toRequestBody(MultipartBody.FORM),
                cost_per_galon = cost_per_galon.toRequestBody(MultipartBody.FORM),
                total_cost = total_cost.toRequestBody(MultipartBody.FORM),
                fuel_quantity = fuel_quantity.toRequestBody(MultipartBody.FORM),
                refueling_date = refueling_date.toRequestBody(MultipartBody.FORM),
                refueling_time = refueling_time.toRequestBody(MultipartBody.FORM),
                fuel_type = fuel_type.toRequestBody(MultipartBody.FORM),
                odo_meter_reading = odo_meter_reading.toRequestBody(MultipartBody.FORM),
                fuel_taken_tank = fuel_taken_tank.toRequestBody(MultipartBody.FORM),
                fuel_taken_can = fuel_taken_can.toRequestBody(MultipartBody.FORM),
                latitude = latitude.toRequestBody(MultipartBody.FORM),
                longitude = longitude.toRequestBody(MultipartBody.FORM),
                note = note.toRequestBody(MultipartBody.FORM),
                status = status.toRequestBody(MultipartBody.FORM),
                receipt = if (isReceipt) {
                    image?.let {
                        val ext = it.name.split(".")
                        MultipartBody.Part.createFormData(
                            "reciept",
                            it.name,
                            it.asRequestBody("image/jpeg".toMediaType())
                        )
                    } ?: run {
                        null
                    }
                } else {
                    null
                },
                pump = if (!isReceipt) {
                    image?.let {
                        val ext = it.name.split(".")
                        MultipartBody.Part.createFormData(
                            "pumping",
                            it.name,
                            it.asRequestBody("image/jpeg".toMediaType())
                        )
                    } ?: run {
                        null
                    }
                } else {
                    null
                },
                receiptFor = (if (isReceipt) "receipt" else "pumping").toRequestBody(MultipartBody.FORM)
            ),
            successMutableLiveData = mutableLiveDataAddFuelReort,
            errorMutableLiveData = mutableLiveDataAddFuelReortError
        )
    }

    fun sendSOS(
        location_lat: String,
        location_long: String,
        reasonId: String,
        message: String
    ) {
        checkIfLoggedIn()
        makeApiCall(
            observable = apiInterface.sendSOS(
                source = "MOB".toRequestBody(MultipartBody.FORM),
                user_id = getUserData()?.user_id.toString().toRequestBody(MultipartBody.FORM),
                companyId = getUserData()?.company_id.toString().toRequestBody(MultipartBody.FORM),
                vehicle_id = giveVehicleId().toRequestBody(MultipartBody.FORM),
                status = "1".toRequestBody(MultipartBody.FORM),
                location_lat = location_lat.toRequestBody(MultipartBody.FORM),
                location_long = location_long.toRequestBody(MultipartBody.FORM),
                operator_shift_time_details_id = giveShiftId().toRequestBody(
                    MultipartBody.FORM
                ),
                reasonid = reasonId.toRequestBody(
                    MultipartBody.FORM
                ),
                message = message.toRequestBody(
                    MultipartBody.FORM
                ),
                phone = getUserData()?.contact_mobile.toString().toRequestBody(MultipartBody.FORM),
                timeUTC = DateUtil.getUTCTimeStringForServer().toRequestBody(MultipartBody.FORM)
            ),
            isResponseAString = true,
            successMutableLiveData = mutableLiveDataSendSOS,
            errorMutableLiveData = mutableLiveDataSendSOSError
        )
    }

    fun getIncidentList(propagate: Boolean = true) {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getIncidentDetailsList(
                    IncidentListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                        //operationId = giveOperationAreaId()
                    )
                ),
                responseJsonKeyword = "data",
                callback = object : SuperRepositoryCallback<ArrayList<IncidentDetails>> {
                    override fun success(result: ArrayList<IncidentDetails>) {
                        super.success(result)

                        gotListOfIncidents(result)

                        if (propagate) {
                            mutableLiveDataIncidentListList.postValue(
                                Event(
                                    result
                                )
                            )
                        }
                    }

                    override fun error(result: Result) {
                        super.error(result)

                        gotListOfIncidents(arrayListOf())

                        if (propagate) {
                            mutableLiveDataIncidentListError.postValue(
                                Event(
                                    result
                                )
                            )
                        }
                    }
                }
            )
        }
    }

    fun getFuelTypes() {
        mutableLiveDataFuelTypeList.postValue(
            Event(
                arrayListOf(
                    FuelType("Gas"),
                    FuelType("Diesel")
                )
            )
        )
    }

    fun pushCallSpinnerData() {
        mutableLiveDataCallList.postValue(
            Event(
                getCallSpinnerData()
            )
        )
    }

    fun getCallSpinnerData(
        min: String? = null
    ): ArrayList<CallAtTimeSpinnerData> {
        val list: ArrayList<CallAtTimeSpinnerData> = ArrayList()

        var shouldInsert = true
        min?.let {
            shouldInsert = false
        }

        for (a in listOf("am", "pm"))
            for (i in listOf("12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"))
                for (j in 0..59) {
                    val jString = if (j < 10) "0$j" else j.toString()
                    min?.let {
                        if ("$i:$j $a" == min && !shouldInsert)
                            shouldInsert = true
                        if (shouldInsert)
                            list.add(
                                CallAtTimeSpinnerData(
                                    "$i:$jString $a"
                                )
                            )
                    } ?: run {
                        list.add(
                            CallAtTimeSpinnerData(
                                "$i:$jString $a"
                            )
                        )
                    }
                }
        return list
    }

    fun getNearestFutureTimeForCallSpinner(timeToParse: String? = null): CallAtTimeSpinnerData {
        var res = ""
        timeToParse?.let {
            res = timeToParse.trim().toLowerCase(Locale.getDefault())
        } ?: run {
            res =
                "${Calendar.getInstance().get(Calendar.HOUR)}:${Calendar.getInstance()
                    .get(Calendar.MINUTE)} ${if (Calendar.getInstance().get(
                        Calendar.AM_PM
                    ) == Calendar.AM
                ) "am" else "pm"}"
        }
        if (res.contains(" "))
            res = res.replace(" ", "")
        val amOrPm = res.substring(res.length - 2)
        val time = res.replace(amOrPm, "")
        val hour = res.replace(amOrPm, "").split(":")[0].toInt()
        val min = res.replace(amOrPm, "").split(":")[1]
        res = "$time $amOrPm"
        var foundMatch = ""

        getCallSpinnerData().forEach { data ->
            if (data.data == res) {
                foundMatch = res

            }
        }

       /* if (foundMatch == "") {
            if (
                hour == 11 &&
                min > 45
            ) {
                foundMatch = "12:00 ${if (amOrPm == "am") "pm" else "am"}"
            } else {
                foundMatch = when {
                    min > 45 -> {
                        "${hour + 1}:00 $amOrPm"
                    }
                    min > 30 -> {
                        "${hour}:45 $amOrPm"
                    }
                    min > 15 -> {
                        "${hour}:30 $amOrPm"
                    }
                    else -> {
                        "${hour}:15 $amOrPm"
                    }
                }
            }
        }*/
        if (foundMatch == "") {
            foundMatch ="${hour}:${min} $amOrPm"
           /* if (
                hour == 11 &&
                min > 45
            ) {
                foundMatch = "12:00 ${if (amOrPm == "am") "pm" else "am"}"
            } else {
                foundMatch ="${hour}:${min} $amOrPm"
            }*/
        }

        return CallAtTimeSpinnerData(foundMatch)
    }

    fun getNextFutureValuesListForCallSpinner(inputTime: String): ArrayList<CallAtTimeSpinnerData> {
        return getCallSpinnerData(
            getNearestFutureTimeForCallSpinner(inputTime).data
        )
    }
    fun getNearestFutureTimeForCallEndSpinner(timeToParse: String? = null): ArrayList<CallAtTimeSpinnerData>? {
        var res = ""
        timeToParse?.let {
            res = timeToParse.trim().toLowerCase(Locale.getDefault())
        } ?: run {
            res =
                "${Calendar.getInstance().get(Calendar.HOUR)}:${Calendar.getInstance()
                    .get(Calendar.MINUTE)} ${if (Calendar.getInstance().get(
                        Calendar.AM_PM
                    ) == Calendar.AM
                ) "am" else "pm"}"
        }
        if (res.contains(" "))
            res = res.replace(" ", "")
        val amOrPm = res.substring(res.length - 2)
        val time = res.replace(amOrPm, "")
        val hour = res.replace(amOrPm, "").split(":")[0].toInt()
        val min = res.replace(amOrPm, "").split(":")[1]
        //res = "$time $amOrPm"
        res= "${hour}:${min} $amOrPm"
        val mCallEndList= ArrayList<CallAtTimeSpinnerData>()
        val list = getCallSpinnerData()
        val mCallAtTimeSpinnerData = CallAtTimeSpinnerData(res)
        val position =list.indexOf(mCallAtTimeSpinnerData)

        for(i in 0 until list.size){
            if(i>position){
                mCallEndList.add(
                    list[i])
            }
        }
        return mCallEndList
    }
    fun getNextFutureValuesListForCallEndSpinner(inputTime: String): ArrayList<CallAtTimeSpinnerData>? {
        return getNearestFutureTimeForCallEndSpinner(inputTime)
    }
    fun getPastTimeForCallAtSpinner(timeToParse: String? = null): ArrayList<CallAtTimeSpinnerData>? {
        var res = ""
        timeToParse?.let {
            res = timeToParse.trim().toLowerCase(Locale.getDefault())
        } ?: run {
            res =
                "${Calendar.getInstance().get(Calendar.HOUR)}:${Calendar.getInstance()
                    .get(Calendar.MINUTE)} ${if (Calendar.getInstance().get(
                        Calendar.AM_PM
                    ) == Calendar.AM
                ) "am" else "pm"}"
        }
        if (res.contains(" "))
            res = res.replace(" ", "")
        val amOrPm = res.substring(res.length - 2)
        val time = res.replace(amOrPm, "")
        val hour = res.replace(amOrPm, "").split(":")[0].toInt()
        val min = res.replace(amOrPm, "").split(":")[1]
        //res = "$time $amOrPm"
        res= "${hour}:${min} $amOrPm"
        var foundMatch = ""



        /*if (foundMatch == "") {
            if (
                hour == 11 &&
                min > 45
            ) {
                foundMatch = "12:00 ${if (amOrPm == "am") "pm" else "am"}"
            } else {
                foundMatch ="${hour}:${min} $amOrPm"
            }
        }*/

        var mCallAtList:ArrayList<CallAtTimeSpinnerData>? = null
       run loop@{
             mCallAtList = ArrayList<CallAtTimeSpinnerData>()
            getCallSpinnerData().forEach { data ->
                if (data.data.equals(res,true)) {
                    return@loop
                }
                mCallAtList?.add(CallAtTimeSpinnerData(data.data))

            }

        }
        return mCallAtList
    }
    fun getPastValuesListForCallAtSpinner(inputTime: String?): ArrayList<CallAtTimeSpinnerData>? {
        return  getPastTimeForCallAtSpinner(inputTime)
    }

    fun updateIncidentStatus(incidentStatusChangeRequest: IncidentStatusChangeRequest) {
        makeApiCall(
            apiInterface.changeIncidentStatus(
                incidentStatusChangeRequest
            ),
            doNotLookForResponseBody = true,
            successMutableLiveData = mutableLiveDataIncidentStatusUpdate,
            errorMutableLiveData = mutableLiveDataIncidentStatusUpdateError
        )
    }

    fun getRouteList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getRouteList(
                    RouteListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataRouteList,
                errorMutableLiveData = mutableLiveDataRouteListError
            )
        }
    }

    fun getTraficeDirectionList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getTrafficList(
                    TrafficeListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataTrafficeDriectionList,
                errorMutableLiveData = mutableLiveDataTrafficeDirectionError
            )
        }
    }

    fun getPropertyDamageList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getPropertyDamage(
                    PropertyDamageRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataPropertyDamagerList,
                errorMutableLiveData = mutableLiveDataPopertyDamageError
            )
        }
    }

    fun getSecendaryCrashList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getSecendaryCrashInvolvedList(
                    SecendaryCrashRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataSecondaryCrashList,
                errorMutableLiveData = mutableLiveDataSecondaryError
            )
        }
    }

    fun getFirstResponceList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getFirstResponce(
                    FirstResponceUnitRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataFirstResponceList,
                errorMutableLiveData = mutableLiveDataFirstResponceError
            )
        }
    }

    fun getColorList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getColorList(
                    ColorListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataColorList,
                errorMutableLiveData = mutableLiveDataColorError
            )
        }
    }

    fun getVehicelTypeList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getVehicelType(
                    VehicleTypeListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveVehicleTypeList,
                errorMutableLiveData = mutableLiveVehicelTypeError
            )
        }
    }

    fun getIncidentTypeList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getIncidentType(
                    IncidentTypeListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveIncidentTypeList,
                errorMutableLiveData = mutableLiveIncidentTypeError
            )
        }
    }


    fun getAssistList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getAssistList(
                    AssectTypeListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveAssectList,
                errorMutableLiveData = mutableLiveAssectListError
            )
        }
    }

    fun getFirstResponderUnitNo() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getFirstResponderUnitNo(
                    FirstResponceUnitRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveFirstRespnderUnitList,
                errorMutableLiveData = mutableLiveFirstRespnderUnitError
            )
        }
    }

    fun addOrUpdateIncident(
        incidentReportId: String? = null,
        latitude: String = "",
        longitude: String = "",
        callAt: String = "",
        callStarted: String = "",
        callComplete: String = "",
        incidentTime: String = "",
        incidentType: String,
        trafficDirection: String,
        mileMaker: String = "",
        propertyDamage: String = "",
        crashInvolced: String = "",
        firstResponder: String = "",
        firstResponderUnit: String = "",
        roadSurver: String = "",
        laneLocation: String = "",
        personTransported: String = "",
        companyColor: String = "",
        vehicleType: String = "",
        assistType: String = "",
        comments: String = "",
        actionStatus: String = "",
        userId: String = "",
        companyId: String = "",
        source: String = "",
        incedent_photo: File?,
        incedent_video: File?,
        shift_id: String,
        plate_no: String,
        note: String,
        incident_status: String,
        direction: String,
        description: String,
        companyRoute: String,
        vehicleQty: String,
        vehicleId: String,
        vehicleInformation: ArrayList<VehicleInformation>,
        vendor: Vendor? = null,
        contract: Contract? = null,
        ramp_lane:String,
        travel_lanes_blocked:String,
        lane_restoration_time:String,
        incident_no:String,
    ) {
        val vehicleInformationRequestBodyList: ArrayList<RequestBody> = ArrayList()
        vehicleInformation.forEach {
            if (it.color.isEmpty()) {
                it.color = it.colorResponce.color_name
            }
            if (it.companyColor.isEmpty()) {
                it.companyColor = it.colorResponce.color_name
            }
            if (it.vehicle_type.isEmpty()) {
                it.vehicle_type = it.motoristModel.motorist_vehicle_type_id
            }
            if (it.vehicleType.isEmpty()) {
                it.vehicleType = it.motoristModel.motorist_vehicle_type_id
            }
            if (it.vehicle_type_name.isEmpty()) {
                it.vehicle_type_name = it.motoristModel.type_name
            }
            vehicleInformationRequestBodyList.add(
                toJson(it).toRequestBody(MultipartBody.FORM)
            )
        }

        makeApiCall(
            apiInterface.addOrUpdateIncidents(
                incidentReportId?.toRequestBody(MultipartBody.FORM) ?: run { null },
                latitude.toRequestBody(MultipartBody.FORM),
                longitude.toRequestBody(MultipartBody.FORM),
                callAt.toRequestBody(MultipartBody.FORM),
                callStarted.toRequestBody(MultipartBody.FORM),
                callComplete.toRequestBody(MultipartBody.FORM),
                incidentTime.toRequestBody(MultipartBody.FORM),
                incidentType.toRequestBody(MultipartBody.FORM),
                trafficDirection.toRequestBody(MultipartBody.FORM),
                mileMaker.toRequestBody(MultipartBody.FORM),
                propertyDamage.toRequestBody(MultipartBody.FORM),
                crashInvolced.toRequestBody(MultipartBody.FORM),
                firstResponder.toRequestBody(MultipartBody.FORM),
                firstResponderUnit.toRequestBody(MultipartBody.FORM),
                roadSurver.toRequestBody(MultipartBody.FORM),
                laneLocation.toRequestBody(MultipartBody.FORM),
                personTransported.toRequestBody(MultipartBody.FORM),
                companyColor.toRequestBody(MultipartBody.FORM),
                vehicleType.toRequestBody(MultipartBody.FORM),
                vehicleId.toRequestBody(MultipartBody.FORM),
                assistType.toRequestBody(MultipartBody.FORM),
                comments.toRequestBody(MultipartBody.FORM),
                actionStatus.toRequestBody(MultipartBody.FORM),
                userId.toRequestBody(MultipartBody.FORM),
                companyId.toRequestBody(MultipartBody.FORM),
                source.toRequestBody(MultipartBody.FORM),
                incedent_photo?.let {
                    val ext = it.name.split(".")
                    MultipartBody.Part.createFormData(
                        "incedent_photo",
                        it.name,
                        it.asRequestBody("image/jpeg".toMediaType())
                    )
                } ?: run {
                    null
                },
                incedent_video?.let {
                    val ext = it.name.split(".")
                    MultipartBody.Part.createFormData(
                        "incedent_video",
                        it.name,
                        it.asRequestBody("video/*".toMediaType())
                    )
                } ?: run {
                    null
                },
                shift_id.toRequestBody(MultipartBody.FORM),
                plate_no.toRequestBody(MultipartBody.FORM),
                note.toRequestBody(MultipartBody.FORM),
                incident_status.toRequestBody(MultipartBody.FORM),
                DateUtil.getUTCTimeStringForServer().toRequestBody(MultipartBody.FORM),
                direction.toRequestBody(MultipartBody.FORM),
                description.toRequestBody(MultipartBody.FORM),
                companyRoute.toRequestBody(MultipartBody.FORM),
                vehicleQty.toRequestBody(MultipartBody.FORM),
                toJson(vehicleInformation).toRequestBody(MultipartBody.FORM),
                vendor?.let { vendor.vendor_id.toRequestBody(MultipartBody.FORM) } ?: run { null },
                contract?.let { contract.company_id.toRequestBody(MultipartBody.FORM) }
                    ?: run { null },
                ramp_lane.toRequestBody(MultipartBody.FORM),
                travel_lanes_blocked.toRequestBody(MultipartBody.FORM),
                lane_restoration_time.toRequestBody(MultipartBody.FORM),
                incident_no.toRequestBody(MultipartBody.FORM)
            ),
            successMutableLiveData = mutableLiveDataAddOrUpdateIncident,
            errorMutableLiveData = mutableLiveDataAddOrUpdateIncidentError,
            lookForOnlySuccessCode = true
        )
    }


    fun closeIncident(incidentCloseRequest: IncidentCloseRequest, propagate: Boolean = true) {
        makeApiCall(
            apiInterface.closeIncidentStatus(
                incidentCloseRequest
            ),
            lookForOnlySuccessCode = true,
            errorMutableLiveData = mutableLiveDataCloseIncidentError,
            callback = object : SuperRepositoryCallback<String> {
                override fun success(result: String) {
                    super.success(result)

                    getIncidentList(propagate)
                }
            }
        )
    }

    fun getQuestionList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getPreOpsQuestions(
                    QuestionListRequest(
                        companyId = it.company_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveQuestionList,
                errorMutableLiveData = mutableLiveQuestionError
            )
        }
    }

    fun getToolList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getToolsQuestionsList(
                    ToolListRequest(
                        companyId = it.company_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataToolList,
                errorMutableLiveData = mutableLiveDataToolListError
            )
        }
    }

    fun getToolListInsp() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getToolsQuestionsList(
                    ToolListRequest(
                        companyId = it.company_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataToolListInsp,
                errorMutableLiveData = mutableLiveDataToolListErrorInsp
            )
        }
    }

    fun uploadPreOpsFile(
        imagePath: String
    ) {

        val file = File(imagePath)
        val ext = file.name.split(".")[1]

        makeApiCall(
            apiInterface.uploadPreOpsFile(
                MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    file.asRequestBody("image/$ext".toMediaType())
                ),
                getUserData()!!.company_id.toString().toRequestBody(MultipartBody.FORM)
            ),
            responseJsonKeyword = "data",
            successMutableLiveData = mutableLiveDataPreOpsImageUpload,
            errorMutableLiveData = mutableLiveDataPreOpsImageUploadError
        )
    }

    fun uploadInsectionFile(
        imagePath: String
    ) {

        val file = File(imagePath)
        val ext = file.name.split(".")[1]

        makeApiCall(
            apiInterface.uploadInspectionFile(
                arrayListOf(
                    MultipartBody.Part.createFormData(
                        "file[]",
                        file.name,
                        file.asRequestBody("image/$ext".toMediaType())
                    )
                ),
                getUserData()!!.company_id.toString().toRequestBody(MultipartBody.FORM)
            ),
            responseJsonKeyword = "data",
            successMutableLiveData = mutableLiveDataInspectinImageUpload,
            errorMutableLiveData = mutableLiveDataInspectinImageUploadError
        )
    }

    fun uploadFinalPreOpsData(preOpsFinalRequest: PreOpsFinalRequest) {
        makeApiCall(
            apiInterface.postFinalPreOpsData(preOpsFinalRequest),
            doNotLookForResponseBody = true,
            successMutableLiveData = mutableLiveDataFinalPreOps,
            errorMutableLiveData = mutableLiveDataFinalPreOpsError
        )
    }

    fun uploadFinalInspectionData(inspectionFinalRequest: InspectionFinalRequest) {
        makeApiCall(
            apiInterface.postFinalInspctionData(inspectionFinalRequest),
            doNotLookForResponseBody = true,
            successMutableLiveData = mutableLiveDataFinalInspection,
            errorMutableLiveData = mutableLiveDataFinalInspectionError
        )
    }

    fun getCrashReportList() {
        makeApiCall(
            apiInterface.getCrashReportList(
                CrashReportListRequest(
                    user_id = getUserData()!!.user_id.toString(),
                    companyId = getUserData()!!.company_id.toString()
                )
            ),
            responseJsonKeyword = "data",
            successMutableLiveData = mutableLiveDataCrashReportList,
            errorMutableLiveData = mutableLiveDataCrashReportListError
        )
    }

    fun addCrashReport(crashReport: CrashReport) {
        crashReport.operator_shift_time_details_id = giveShiftId()
        makeApiCall(
            apiInterface.addCrashReport(
                crashReport
            ),
            responseJsonKeyword = "details",
            successMutableLiveData = mutableLiveDataAddCrashReport,
            errorMutableLiveData = mutableLiveDataAddCrashReportError
        )
    }

    fun uploadCrashReportFiles(
        images: ArrayList<ArrayList<String>>,
        reportId: String
    ) {

        if (images.size == 0) {
            mutableLiveDataCrashReportFilesUpload.postValue(
                Event(
                    "done"
                )
            )
            return
        }

        val exterior: ArrayList<MultipartBody.Part> = ArrayList()
        val interior: ArrayList<MultipartBody.Part> = ArrayList()
        val vin: ArrayList<MultipartBody.Part> = ArrayList()
        val thirdParty: ArrayList<MultipartBody.Part> = ArrayList()

        images.forEachIndexed { index, group ->
            var keyword = ""
            when (index) {
                0 -> {
                    keyword = "exterior_vehicle_photo[]"
                }
                1 -> {
                    keyword = "interior_vehicle_photo[]"
                }
                2 -> {
                    keyword = "autotag_vin_photo[]"
                }
                3 -> {
                    keyword = "third_party_vehicle_photo[]"
                }
            }
            group.forEach { image ->
                val file = File(image)
                val ext = file.name.split(".")[1]
                when (index) {
                    0 -> {
                        exterior.add(
                            MultipartBody.Part.createFormData(
                                keyword,
                                file.name,
                                file.asRequestBody("image/$ext".toMediaType())
                            )
                        )
                    }
                    1 -> {
                        interior.add(
                            MultipartBody.Part.createFormData(
                                keyword,
                                file.name,
                                file.asRequestBody("image/$ext".toMediaType())
                            )
                        )
                    }
                    2 -> {
                        vin.add(
                            MultipartBody.Part.createFormData(
                                keyword,
                                file.name,
                                file.asRequestBody("image/$ext".toMediaType())
                            )
                        )
                    }
                    3 -> {
                        thirdParty.add(
                            MultipartBody.Part.createFormData(
                                keyword,
                                file.name,
                                file.asRequestBody("image/$ext".toMediaType())
                            )
                        )
                    }
                }
            }
        }

        makeApiCall(
            apiInterface.uploadCrashReportFiles(
                exterior,
                interior,
                vin,
                thirdParty,
                "MOB".toRequestBody(MultipartBody.FORM),
                reportId.toRequestBody(MultipartBody.FORM),
                getUserData()!!.user_id.toString().toRequestBody(MultipartBody.FORM)
            ),
            doNotLookForResponseBody = true,
            successMutableLiveData = mutableLiveDataCrashReportFilesUpload,
            errorMutableLiveData = mutableLiveDataCrashReportFilesUploadError
        )
    }

    fun getFaqList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getFaqList(
                    FaqRequest(
                        companyId = it.company_id.toString(),
                        source = "MOB"
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataFaqList,
                errorMutableLiveData = mutableLiveDataFaqError
            )
        }
    }

    fun getMaintenanceReport() {
        makeApiCall(
            apiInterface.getMaintenanceReportList(
                MaintenanceReportListRequest(
                    user_id = getUserData()!!.user_id.toString(),
                    companyId = getUserData()!!.company_id.toString()
                )
            ),
            responseJsonKeyword = "data",
            successMutableLiveData = mutableLiveDataMaintenanceReportList,
            errorMutableLiveData = mutableLiveDataMaintenanceReportListError
        )
    }

    fun getVendorsList() {
        makeApiCall(
            apiInterface.getVendorsList(
                VendorListRequest(
                    user_id = getUserData()!!.user_id.toString(),
                    companyId = getUserData()!!.company_id.toString()
                )
            ),
            responseJsonKeyword = "data",
            successMutableLiveData = mutableLiveDataVendors,
            errorMutableLiveData = mutableLiveDataVendorsError
        )
    }

    fun getVendorsListForIncidents() {
        makeApiCall(
            apiInterface.getVendorsListForIncidents(
                VendorListRequest(
                    user_id = getUserData()!!.user_id.toString(),
                    companyId = getUserData()!!.company_id.toString()
                )
            ),
            responseJsonKeyword = "data",
            successMutableLiveData = mutableLiveDataVendorsForIncidents,
            errorMutableLiveData = mutableLiveDataVendorsForIncidentsError
        )
    }

    fun getContactsList() {
        makeApiCall(
            apiInterface.getContactList(
                ContractListRequest(
                    user_id = getUserData()!!.user_id.toString(),
                    companyId = getUserData()!!.company_id.toString()
                )
            ),
            responseJsonKeyword = "data",
            successMutableLiveData = mutableLiveDataContactList,
            errorMutableLiveData = mutableLiveDataContactListError
        )
    }

    fun getServiceTypes() {
        makeApiCall(
            apiInterface.getServiceTypes(
                ServiceTypeRequest(
                    user_id = getUserData()!!.user_id.toString(),
                    companyId = getUserData()!!.company_id.toString()
                )
            ),
            responseJsonKeyword = "data",
            successMutableLiveData = mutableLiveDataServiceTypes,
            errorMutableLiveData = mutableLiveDataServiceTypesError
        )
    }

    fun addMaintenanceReport(
        imagePath: String,
        vehicleId: String,
        vehicleVinNo: String,
        requestTypeId: String,
        stateId: String,
        stateName: String,
        contractPeriod: String,
        reportDate: String,
        serviceTypeId: String,
        mileage: String,
        serviceCost: String,
        labourHours: String,
        labourMinutes: String,
        vendorId: String,
        vendorName: String,
        repairDescription: String,
        note: String
        /*maintenanceReportDataId: String,
        reportStatus*/
    ) {

        val noImage = imagePath == ""
        var file: File = File("")
        var ext: String = ""
        try {
            file = File(imagePath)
            ext = file.name.split(".")[1]
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        Log.d("maintenanceRequestData","vehicleId $vehicleId" +
                "        vehicleVinNo $vehicleVinNo" +
                "        stateId $stateId" +
                "        stateName $stateName" +
                "        requestTypeId $requestTypeId" +
                "        contractPeriod $contractPeriod" +
                "        reportDate $reportDate" +
                "        serviceTypeId $serviceTypeId" +
                "        mileage $mileage" +
                "        serviceCost $serviceCost" +
                "        labourHours $labourHours" +
                "        labourMinutes $labourMinutes" +
                "        vendorId $vendorId" +
                "        vendorName $vendorName" +
                "        repairDescription $repairDescription" +
                "        note $note")

        makeApiCall(
            apiInterface.addMaintenanceReport(
                if (noImage) null else
                    MultipartBody.Part.createFormData(
                        "reciept",
                        file.name,
                        file.asRequestBody("image/$ext".toMediaType())
                    ),
                "MOB".toRequestBody(MultipartBody.FORM),
                getUserData()!!.user_id.toString().toRequestBody(MultipartBody.FORM),
                getUserData()!!.company_id.toString().toRequestBody(MultipartBody.FORM),
                "${getUserData()!!.first_name} ${getUserData()!!.last_name}".toRequestBody(MultipartBody.FORM),
                getUserData()!!.email.toRequestBody(MultipartBody.FORM),
                getUserData()!!.role_name.toRequestBody(MultipartBody.FORM),
                "0".toRequestBody(MultipartBody.FORM),
                vehicleId.toRequestBody(MultipartBody.FORM),
                vehicleVinNo.toRequestBody(MultipartBody.FORM),
                requestTypeId.toRequestBody(MultipartBody.FORM),
                stateId.toRequestBody(MultipartBody.FORM),
                stateName.toRequestBody(MultipartBody.FORM),
                contractPeriod.toRequestBody(MultipartBody.FORM),
                reportDate.toRequestBody(MultipartBody.FORM),
                serviceTypeId.toRequestBody(MultipartBody.FORM),
                mileage.toRequestBody(MultipartBody.FORM),
                serviceCost.toRequestBody(MultipartBody.FORM),
                labourHours.toRequestBody(MultipartBody.FORM),
                labourMinutes.toRequestBody(MultipartBody.FORM),
                /*labourHours.split(".")[0].toRequestBody(MultipartBody.FORM),
                (if (labourHours.split(".").size > 1) labourHours.split(".")[1] else "00").toRequestBody(
                    MultipartBody.FORM
                ),*/
                vendorId.toRequestBody(MultipartBody.FORM),
                vendorName.toRequestBody(MultipartBody.FORM),
                repairDescription.toRequestBody(MultipartBody.FORM),
                note.toRequestBody(MultipartBody.FORM)
                /*maintenanceReportDataId.toRequestBody(MultipartBody.FORM),
                reportStatus.toRequestBody(MultipartBody.FORM)*/
            ),
            responseJsonKeyword = "details",
            successMutableLiveData = mutableLiveDataAddMaintenanceReport,
            errorMutableLiveData = mutableLiveDataAddMaintenanceReportError
        )
    }

    fun logout() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.logoutApi(
                    LogoutRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString(),
                        source = "MOB"
                    )
                ),
                isResponseAString = true,
                successMutableLiveData = mutableLiveDataLogout,
                errorMutableLiveData = mutableLiveDataLogoutError
            )
        }
    }

    fun adminHelp(
        location_lat: String,
        location_long: String,
        message: String,
        subject: String
    ) {
        checkIfLoggedIn()
        makeApiCall(
            observable = apiInterface.adminHelp(
                source = "MOB".toRequestBody(MultipartBody.FORM),
                user_id = getUserData()?.user_id.toString().toRequestBody(MultipartBody.FORM),
                companyId = getUserData()?.company_id.toString().toRequestBody(MultipartBody.FORM),
                message = message.toRequestBody(MultipartBody.FORM),
                subject = subject.toRequestBody(MultipartBody.FORM),
                location_lat = location_lat.toRequestBody(MultipartBody.FORM),
                location_long = location_long.toRequestBody(MultipartBody.FORM),
                operator_shift_time_details_id = giveShiftId().toRequestBody(
                    MultipartBody.FORM
                ),
                vehicleId = giveVehicleId().toRequestBody(MultipartBody.FORM)
            ),
            isResponseAString = true,
            successMutableLiveData = mutableLiveDataAdminHelpSuccess,
            errorMutableLiveData = mutableLiveDataAdminHelpError
        )
    }

    fun getInspectionList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getInspectionList(
                    InspectionListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString(),
                        source = "MOB"
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataInspectionList,
                errorMutableLiveData = mutableLiveDataInspectionListError
            )
        }
    }

    fun getExtraTimeList(extraTimeListRequest: ExtraTimeListRequest) {
        makeApiCall(
            apiInterface.getExtraTimeList(
                extraTimeListRequest
            ),
            responseJsonKeyword = "data",
            successMutableLiveData = mutableLiveDataExtraTimeList,
            errorMutableLiveData = mutableLiveDataExtraTimeListError
        )
    }

    fun updateUserProfile(
        first_name: String,
        last_name: String,
        phone: String,
        address: String,
        city: String,
        zip: String,
        profile_image: String? = null
    ) {
        var file: File? = null
        var ext = ""
        profile_image?.let {
            file = File(profile_image)
            ext = file!!.name.split(".")[1]
        }

        makeApiCall(
            apiInterface.updateProfile(
                profile_image =
                profile_image?.let {
                    MultipartBody.Part.createFormData(
                        "profile_image",
                        file!!.name,
                        file!!.asRequestBody("image/$ext".toMediaType())
                    )
                } ?: run {
                    null
                },
                source = "MOB".toRequestBody(MultipartBody.FORM),
                companyId = getUserData()!!.company_id.toString().toRequestBody(MultipartBody.FORM),
                user_id = getUserData()!!.user_id.toString().toRequestBody(MultipartBody.FORM),
                first_name = first_name.toRequestBody(MultipartBody.FORM),
                last_name = last_name.toRequestBody(MultipartBody.FORM),
                phone = phone.toRequestBody(MultipartBody.FORM),
                address = address.toRequestBody(MultipartBody.FORM),
                city = city.toRequestBody(MultipartBody.FORM),
                zip = zip.toRequestBody(MultipartBody.FORM)
            ),
            doNotLookForResponseBody = true,
            successMutableLiveData = mutableLiveDataUpdateProfile,
            errorMutableLiveData = mutableLiveDataUpdateProfileError,
            callback = object : SuperRepositoryCallback<String> {
                override fun success(result: String) {
                    super.success(result)

                    login(
                        LoginRequest(
                            email = getSharedPref().getString(EMAIL, "")!!,
                            password = getSharedPref().getString(PASSWORD, "")!!,
                            device_token = getSharedPref().getString(DEVICE_FCM_TOKEN, "")!!,
                            deviceId = getDeviceId(),
                            latitude = getSharedPref().getString(CURRENT_LAT, "")!!,
                            longitude = getSharedPref().getString(CURRENT_LONG, "")!!,
                        )
                    )
                }
            }
        )
    }

    fun getExtraTimeCancelReasons() {
        makeApiCall(
            apiInterface.getExtraTimeCancelReasonList(
                ExtraTimeCancelReasonsRequest(
                    getUserData()!!.company_id.toString(),
                    getUserData()!!.user_id.toString()
                )
            ),
            responseJsonKeyword = "data",
            successMutableLiveData = mutableLiveDataExtraTimeCancelReasons,
            errorMutableLiveData = mutableLiveDataExtraTimeCancelReasonsError
        )
    }

    fun getInspectionQuestion() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getInspectionPreOpsQuestions(
                    QuestionListRequest(
                        companyId = it.company_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                errorMutableLiveData = mutableLiveInsQuestionListError,
                callback = object :
                    SuperRepositoryCallback<ArrayList<InsectionQuestionLisResponse>> {

                    override fun success(result: ArrayList<InsectionQuestionLisResponse>) {
                        super.success(result)

                        result.forEach { ques ->
                            ques.name = ques.inspection_vehicle_question
                        }

                        mutableLiveInsQuestionList.postValue(Event(result))
                    }
                }
            )
        }
    }


    fun getNotificationList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getNotificationList(
                    NotificationRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString(),
                        source = "MOB"
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataNotificationList,
                errorMutableLiveData = mutableLiveDataNotificationListError
            )
        }
    }


    fun notificationReadUnRead(notificationMasterId: String, isRead: String) {
        checkIfLoggedIn()
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.notificationReadUnRead(
                    NotificationReadUnReadRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString(),
                        source = "MOB",
                        notification_master_id = notificationMasterId,
                        is_read = isRead
                    )
                ),
                isResponseAString = true,
                successMutableLiveData = mutableLiveDataNotificationReadUnReadRequest,
                errorMutableLiveData = mutableLiveDataNotificationReadUnReadRequestError
            )
        }
    }


    fun getInspectionDetails(insepectionReportId: String) {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getInspectionDetails(
                    InspectionDetailsRequest(
                        companyId = it.company_id.toString(),
                        userId = it.user_id.toString(),
                        inspectionReportId = insepectionReportId
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataInspectionDeatils,
                errorMutableLiveData = mutableLiveDataInspectionDeatilsError
            )
        }
    }

    fun updateExtraTime(extraTimeRequest: ExtraTimeRequest) {
        makeApiCall(
            apiInterface.updateExtraTime(extraTimeRequest),
            doNotLookForResponseBody = true,
            successMutableLiveData = mutableLiveDataExtraTimeRequest,
            errorMutableLiveData = mutableLiveDataExtraTimeRequestError
        )
    }

    fun getShiftReportRequest() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getShiftReportList(
                    GetShiftReportRequest(
                        companyId = it.company_id.toString(),
                        userId = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataShiftReportList,
                errorMutableLiveData = mutableLiveDataShiftReportListError
            )
        }
    }

    fun getUserList(type:String="") {

        getUserData()?.let {
            var roleId= if(type=="crash"){ "4"}else null
            makeApiCall(
                observable = apiInterface.getUserList(
                    GetUserListRequest(
                        companyId = it.company_id.toString(),
                        roleId = roleId
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataGetUserList,
                errorMutableLiveData = mutableLiveDataGetUserListError
            )
        }
    }

    fun requestOTPToResetPassword(forgotPasswordOTPRequest: ForgotPasswordOTPRequest) {
        makeApiCall(
            apiInterface.requestOTPToResetPassword(forgotPasswordOTPRequest),
            doNotLookForResponseBody = true,
            successMutableLiveData = mutableLiveDataOTPRequestForResetPassword,
            errorMutableLiveData = mutableLiveDataOTPRequestForResetPasswordError
        )
    }

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest) {
        makeApiCall(
            apiInterface.resetPassword(resetPasswordRequest),
            doNotLookForResponseBody = true,
            successMutableLiveData = mutableLiveDataResetPassword,
            errorMutableLiveData = mutableLiveDataResetPasswordError
        )
    }

    fun getSOSReasonList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getSOSReasonList(
                    SOSReasonRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString(),
                        source = "MOB"
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataSOSReasonList,
                errorMutableLiveData = mutableLiveDataSOSReasonListError
            )
        }
    }

    fun sendLocationToServer(isPermissionToEndShiftRequested: Boolean = false) {
        getUserData()?.let {
            if (shiftStarted()) {
                val locationUpdateRequest: LocationUpdateRequest = LocationUpdateRequest(
                    operator_shift_time_details_id = giveShiftId(),
                    vehicle_id = giveVehicleId(),
                    user_id = getUserData()!!.user_id.toString()
                )
                locationUpdatesSendToServerForUpdate.apply {
                    clear()
                    getUserLocations().forEach { locationUpdate ->
                        add(locationUpdate.copy())
                        locationUpdateRequest.coordinates.add(locationUpdate)
                    }
                }
                makeApiCall(
                    apiInterface.sendLocationToServer(
                        locationUpdateRequest
                    ),
                    doNotLookForResponseBody = true,
                    callback = object : SuperRepositoryCallback<String> {
                        override fun success(result: String) {
                            super.success(result)

                            deleteUserLocations(locationUpdatesSendToServerForUpdate)

                            if (isPermissionToEndShiftRequested)
                                mutableLiveDataPermissionToEndShift.postValue(
                                    Event("yes")
                                )
                        }

                        override fun error(result: Result) {
                            super.error(result)
                            locationUpdatesSendToServerForUpdate.clear()

                            if (isPermissionToEndShiftRequested)
                                mutableLiveDataPermissionToEndShiftError.postValue(
                                    Event(result)
                                )
                        }
                    }
                )
            }
        }
    }

    fun changePatrollingStatus(
        latitude: String,
        longitude: String,
        direction: String,
        description: String
    ) {
        makeApiCall(
            apiInterface.changePatrollingStatus(
                PatrollingStatusChangeRequest(
                    operator_shift_time_details_id = giveShiftId(),
                    indicator_status = if (giveIndicatorStatus() == "1") "2" else "1",
                    lat = latitude,
                    lng = longitude,
                    indicator_direction = direction,
                    indicator_description = description,
                    timeUTC = DateUtil.getUTCTimeStringForServer(),
                    vehicle_id = giveVehicleId(),
                    user_id = getUserData()!!.user_id.toString()
                )
            ),
            doNotLookForResponseBody = true,
            successMutableLiveData = mutableLiveDataPatrollingStatusChange,
            errorMutableLiveData = mutableLiveDataPatrollingStatusChangeError,
            callback = object : SuperRepositoryCallback<String> {
                override fun success(result: String) {
                    super.success(result)

                    getHomeGridItems()
                }
            }
        )
    }

    fun startBreak() {
        makeApiCall(
            apiInterface.startBreak(
                StartBreakTimeRequest(
                    companyId = getUserData()!!.company_id.toString(),
                    userId = getUserData()!!.user_id.toString(),
                    user_id = getUserData()!!.user_id.toString(),
                    shiftId = giveShiftId(),
                    vehicle_id = giveVehicleId()
                )
            ),
            responseJsonKeyword = "data",
            successMutableLiveData = mutableLiveDataStartBreak,
            errorMutableLiveData = mutableLiveDataStartBreakError,
            callback = object : SuperRepositoryCallback<StartBreakTimeResponse> {

                override fun success(result: StartBreakTimeResponse) {
                    super.success(result)

                    getSharedPref()
                        .edit()
                        .putString(
                            BREAK_TIME_ID,
                            result.breakTimeId
                        )
                        .apply()
                }
            }
        )
    }

    fun endBreak() {
        makeApiCall(
            apiInterface.endBreak(
                EndBreakTimeRequest(
                    getUserData()!!.company_id.toString(),
                    getUserData()!!.user_id.toString(),
                    getUserData()!!.user_id.toString(),
                    giveShiftId(),
                    giveBreakTimeId(),
                    giveVehicleId()
                )
            ),
            doNotLookForResponseBody = true,
            successMutableLiveData = mutableLiveDataEndBreak,
            errorMutableLiveData = mutableLiveDataEndBreakError,
            callback = object : SuperRepositoryCallback<String> {

                override fun success(result: String) {
                    super.success(result)

                    getSharedPref()
                        .edit()
                        .remove(BREAK_TIME_ID)
                        .apply()
                }
            }
        )
    }

    fun getShiftType() {
        makeApiCall(
            apiInterface.getShiftType(
                ShiftTypeRequest(
                    user_id = getUserData()!!.user_id.toString(),
                    companyId = getUserData()!!.company_id.toString()
                )
            ),
            responseJsonKeyword = "data",
            successMutableLiveData = mutableLiveDataShiftType,
            errorMutableLiveData = mutableLiveDataShiftTypeError
        )
    }

    fun sendSurvey(name: String? = null, email: String, sendSurveyVia: SendSurveyVia) {
        checkIfLoggedIn()
        makeApiCall(
            apiInterface.sendSurvey(
                SendSurveyRequest(
                    userId = getUserData()!!.user_id.toString(),
                    companyId = getUserData()!!.company_id.toString(),
                    userEmail = email,
                    userName = name,
                    via = sendSurveyVia
                )
            ),
            responseJsonKeyword = "data",
            successMutableLiveData = mutableLiveDataSendSurvey,
            errorMutableLiveData = mutableLiveDataSendSurveyError
        )
    }

    fun changePassword(password: String) {
        checkIfLoggedIn()
        makeApiCall(
            apiInterface.changePassword(
                ChangePasswordRequest(
                    getUserData()!!.user_id.toString(),
                    getUserData()!!.company_id.toString(),
                    password
                )
            ),
            doNotLookForResponseBody = true,
            errorMutableLiveData = mutableLiveDataChangePaasswordError,
            successMutableLiveData = mutableLiveDataChangePaassword
        )
    }

    fun checkIfLoggedIn() {
        makeApiCall(
            apiInterface.isLoggedIn(
                IsLoggedInRequest(
                    getDeviceId(),
                    getUserData()!!.user_id.toString()
                )
            ),
            doNotLookForResponseBody = true,
            callback = object : SuperRepositoryCallback<String> {
                override fun success(result: String) {
                    super.success(result)
                }

                override fun error(result: Result) {
                    super.error(result)

                    this@Repository.notAuthorised()
                    context.sendBroadcast(
                        Intent(
                            KeyWordsAndConstants.NOT_AUTHORISED
                        )
                    )
                }
            }
        )
    }

    fun checkVersion() {
        makeApiCall(
            apiInterface.checkIfUpdateRequired(
                CheckUpdateRequest(
                    version = BuildConfig.VERSION_CODE.toString()
                )
            ),
            responseJsonKeyword = "data",
            successMutableLiveData = mutableLiveDataCheckVersion,
            errorMutableLiveData = mutableLiveDataCheckVersionError
        )
    }

    fun getMotoristTypesList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getMotiristVehicleType(
                    MotoristTypeListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                canReadOnlyOnce = false,
                successMutableLiveData = mutableLiveDataMotoristType,
                errorMutableLiveData = mutableLiveDataMotoristTypeError
            )
        }
    }

    fun getRoadSurfaceTypesList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getRoadSurfaces(
                    RoadSurfaceTypeListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                canReadOnlyOnce = false,
                successMutableLiveData = mutableLiveDataRoadSurfaces,
                errorMutableLiveData = mutableLiveDataRoadSurfacesError
            )
        }
    }

    fun getMotoristVehiclesList() {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getMotoristVehicles(
                    MotoristVehicleListRequest(
                        companyId = it.company_id.toString(),
                        user_id = it.user_id.toString()
                    )
                ),
                responseJsonKeyword = "data",
                canReadOnlyOnce = false,
                successMutableLiveData = mutableLiveDataMotoristVehiclesList,
                errorMutableLiveData = mutableLiveDataMotoristVehiclesListError
            )
        }
    }

    fun getIncidentFields(incidentTypeId: String) {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.getIncidentFields(
                    IncidentFieldListRequest(
                        user_id = it.user_id.toString(),
                        companyId = it.company_id.toString(),
                        incident_type_id = incidentTypeId
                    )
                ),
                responseJsonKeyword = "data",
                successMutableLiveData = mutableLiveDataIncidentFieldsList,
                errorMutableLiveData = mutableLiveDataIncidentFieldsListError
            )
        }
    }

    fun updateWazeInformation(lat: String, long: String, direction: String, desc: String) {
        getUserData()?.let {
            makeApiCall(
                observable = apiInterface.updateWazeInformation(
                    UpdateWazeInformationRequest(
                        user_id = it.user_id.toString(),
                        companyId = it.company_id.toString(),
                        timeUTC = DateUtil.getUTCTimeStringForServer(),
                        lat = lat,
                        lng = long,
                        direction = direction,
                        description = desc
                    )
                ),
                doNotLookForResponseBody = true,
                successMutableLiveData = mutableLiveDataUpdateWazeInformation,
                errorMutableLiveData = mutableLiveDataUpdateWazeInformationError
            )
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Public methods
    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun getSharedPref(): SharedPreferences {
        return context.getSharedPreferences(
            KeyWordsAndConstants.SHARED_PREF_DB,
            Context.MODE_PRIVATE
        )
    }

    fun saveUserDataToPref(userData: UserDetails) {
        getSharedPref()
            .edit()
            .putString(
                KeyWordsAndConstants.USER_DATA,
                toJson(userData)
            )
            .apply()
    }

    fun getUserData(): UserDetails? {
        val data = getSharedPref().getString(KeyWordsAndConstants.USER_DATA, "")
        if (
            data.equals("")
        )
            return null
        return fromJson<UserDetails>(
            getSharedPref().getString(
                KeyWordsAndConstants.USER_DATA,
                ""
            )!!
        )
    }

    fun ifPreOpsDone(): Boolean {
        return getSharedPref()
            .getBoolean(
                PRE_OPS_STARTED,
                false
            )
    }

    fun preOpsDone() {
        getSharedPref()
            .edit()
            .putBoolean(
                PRE_OPS_STARTED,
                true
            )
            .apply()
    }

    fun clearAllData() {
        var preOpsDone = false
        if (shiftStarted())
            preOpsDone = getSharedPref().getBoolean(PRE_OPS_STARTED, false)

        val fcm = getSharedPref().getString(DEVICE_FCM_TOKEN, "")

        getSharedPref().edit().clear().apply()

        getSharedPref()
            .edit()
            .putBoolean(
                PRE_OPS_STARTED,
                preOpsDone
            )
            .apply()

        getSharedPref()
            .edit()
            .putString(
                DEVICE_FCM_TOKEN,
                fcm
            )
            .apply()
    }

    fun isLoggedIn(): Boolean {
        getUserData()?.let {
            return true
        } ?: run {
            return false
        }
    }

    fun prepareIncidentDetailsVideoThumb(videoUrl: String) {
        Thread(
            Runnable {
                VideoThumbnailUtil.retrieveVideoFrameFromVideo(
                    videoUrl,
                    object : VideoThumbnailUtil.Callback {
                        override fun gotThumbnail(thumbNail: Bitmap) {
                            logUtil.logV("Incident Details thumb ready.")
                            mutableLiveDataIncidentDetailsVideoThumb.postValue(
                                Event(
                                    content = thumbNail,
                                    shouldReadOnlyOnce = false
                                )
                            )
                        }
                    }
                )
            }
        ).start()
    }

    fun shiftStarted(): Boolean {
        val shiftId = getSharedPref().getString(
            SHIFT_ID,
            ""
        )

        return shiftId!!.isNotEmpty()
    }

    fun giveShiftId(): String {
        return getSharedPref().getString(
            SHIFT_ID,
            ""
        )!!
    }

    fun giveVehicleId(): String {
        var id = getSharedPref().getString(
            VEHICLE_ID,
            if (this::vehicleSelectedByManager.isInitialized) vehicleSelectedByManager.vehicle_id else ""
        )!!

        if (id == "")
            id =
                if (this::vehicleSelectedByManager.isInitialized) vehicleSelectedByManager.vehicle_id else ""

        return id
    }

    fun giveVehicleIdToShow(): String {
        var id = getSharedPref().getString(
            VEHICLE_ID_TO_SHOW,
            if (this::vehicleSelectedByManager.isInitialized) vehicleSelectedByManager.ID else ""
        )!!

        if (id == "")
            id =
                if (this::vehicleSelectedByManager.isInitialized) vehicleSelectedByManager.ID else ""

        return id
    }

    fun giveVehicleVinNumberToShow(): String {
        var id = getSharedPref().getString(
            VIN_NUMBER,
            if (this::vehicleSelectedByManager.isInitialized) vehicleSelectedByManager.vin_number else ""
        )!!

        if (id == "")
            id =
                if (this::vehicleSelectedByManager.isInitialized) vehicleSelectedByManager.vin_number else ""

        return id
    }

    fun giveVehicleColor(): String {
        return getSharedPref().getString(
            VEHICLE_COLOR,
            ""
        )!!
    }

    fun giveVinNumber(): String {
        return getSharedPref().getString(
            VIN_NUMBER,
            ""
        )!!
    }

    fun giveStateId(): String {
        var id = getSharedPref().getString(
            STATE_ID,
            if (this::vehicleSelectedByManager.isInitialized) vehicleSelectedByManager.state_id else ""
        )!!

        if (id == "")
            id =
                if (this::vehicleSelectedByManager.isInitialized) vehicleSelectedByManager.state_id else ""

        return id
    }

    fun giveStateName(): String {
        var id = getSharedPref().getString(
            STATE_NAME,
            if (this::vehicleSelectedByManager.isInitialized) vehicleSelectedByManager.state_name else ""
        )!!

        if (id == "")
            id =
                if (this::vehicleSelectedByManager.isInitialized) vehicleSelectedByManager.state_name else ""

        return id
    }

    fun giveLaneId(): String {
        return getSharedPref().getString(
            LANE_ID, ""
        )!!
        /*var id = getSharedPref().getString(
            LANE_ID,
            if (this::vehicleSelectedByManager.isInitialized) vehicleSelectedByManager.state_id else ""
        )!!

        if (id == "")
            id =
                if (this::vehicleSelectedByManager.isInitialized) vehicleSelectedByManager.state_id else ""

        return id*/
    }

    fun giveLaneName(): String {
        return getSharedPref().getString(
            LANE_NAME, ""
        )!!
    }

    fun giveOperationAreaId(): String {
        return getSharedPref().getString(
            OPERATION_AREA_ID,
            ""
        )!!
    }

    fun giveOperationAreaName(): String {
        return getSharedPref().getString(
            OPERATION_AREA_NAME,
            ""
        )!!
    }

    fun giveIndicatorStatus(): String {
        return getSharedPref().getString(
            INDICATOR_STATUS,
            ""
        )!!
    }

    fun giveUnReadNotificationCount(): Int {
        return getSharedPref().getInt(
            UNREAD_NOTIFICATION_COUNT,
            0
        )!!
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun gotPushNotification(obj: JSONObject) {
        getUserData()?.let {
            notificationMaster.showNotification(
                fromJson(obj.toString())
            )
        }
    }

    fun permissionToEndShift() {
        if (getUserLocations().size == 0)
            mutableLiveDataPermissionToEndShift.postValue(
                Event("yes")
            )
        else
            sendLocationToServer(true)
    }

    fun giveBreakTimeId(): String {
        return getSharedPref()
            .getString(
                BREAK_TIME_ID, ""
            )!!
    }

    fun getDeviceId(): String {
        return getSharedPref().getString(KeyWordsAndConstants.DEVICE_FCM_TOKEN, "")!!
    }

    fun saveInspectionData(
        inspectionFinalRequest: InspectionFinalRequest
    ) {
        getSharedPref()
            .edit()
            .putString(
                KeyWordsAndConstants.SAVED_INSPECTION_DATA,
                toJson(inspectionFinalRequest)
            )
            .apply()
    }

    fun clearInspectionData() {
        getSharedPref()
            .edit()
            .putString(
                SAVED_INSPECTION_DATA, ""
            )
            .apply()

    }

    fun getSavedInspectionData(): InspectionFinalRequest? {
        val strSaved = getSharedPref()
            .getString(SAVED_INSPECTION_DATA, "")

        if (strSaved == "")
            return null

        return fromJson<InspectionFinalRequest>(strSaved!!)
    }

    fun getForegroundNotificationForLocationUpdate(): Notification? {
        return notificationMaster.getNotificationForLocationMonitoring()
    }

    fun gotLocation(latitude: String, longitude: String) {
        if (
            getSharedPref().getString(
                KeyWordsAndConstants.ON_GOING_INCIDENTS, ""
            ) == ""
        ) {
            arrayListOf<IncidentDetails>()
        } else {
            fromJson(
                getSharedPref().getString(
                    KeyWordsAndConstants.ON_GOING_INCIDENTS, ""
                )!!
            )
        }.forEach { incident ->
            if (
                incident.incident_status == "On Scene" &&
                incident.latitude != null &&
                incident.latitude != "" &&
                incident.longitude != "" &&
                incident.longitude != null
            ) {
                try {
                    val currentLocation = Location("")
                    currentLocation.latitude = latitude.toDouble()
                    currentLocation.longitude = longitude.toDouble()
                    val onSceneLocation = Location("")
                    onSceneLocation.latitude = incident.latitude?.toDouble()?:0.0
                    onSceneLocation.longitude = incident.longitude?.toDouble()?:0.0
                    val distance = currentLocation.distanceTo(onSceneLocation)

                    if (((distance / 1000) * 1.609) > PATROLLING_MILE_LIMIT
                    ) {
                        logUtil.logV("the distance is not within the limit. changing the patrolling status.")
                        closeIncident(
                            IncidentCloseRequest(
                                companyId = incident.company_id!!,
                                userId = incident.company_user_id!!,
                                incidentReportId = incident.Incidents_report_data_id!!,
                                is_force_logout = "1",
                                timeUTC = DateUtil.getUTCTimeStringForServer()

                            ),
                            propagate = false
                        )
                    } else {
                        logUtil.logV("the distance is within the limit.")
                    }
                }catch(e:NumberFormatException){
                    FirebaseCrashlytics.getInstance().log(e.message?:"")
                }
                catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().log(e.message?:"")
                }
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Private Methods
    private fun startMonitoringLocation() {
        context.startService(
            Intent(
                context,
                LocationMonitorForegroundService::class.java
            )
        )
    }

    private fun stopMonitoringLocation() {
        context.stopService(
            Intent(
                context,
                LocationMonitorForegroundService::class.java
            )
        )
    }

    private fun gotListOfIncidents(incidents: ArrayList<IncidentDetails>) {
        getSharedPref()
            .edit()
            .putString(
                KeyWordsAndConstants.ON_GOING_INCIDENTS,
                toJson(incidents)
            )
            .apply()

        if (incidents.size > 0) {
            var atLeastOneWithOnGoingStatus = false
            incidents.forEach {
                if (it.incident_status == "On Scene") {
                    atLeastOneWithOnGoingStatus = true
                }
            }
            if (atLeastOneWithOnGoingStatus)
                startMonitoringLocation()
            else
                stopMonitoringLocation()
        } else {
            stopMonitoringLocation()
        }
    }

    private fun getUserLocations(): ArrayList<LocationUpdate> {
        val list: ArrayList<LocationUpdate> = ArrayList()
        var rawString = getSharedPref().getString(USER_LOCATIONS, "")
        rawString ?: run {
            rawString = ""
        }
        if (rawString != "") {
            list.clear()
            list.addAll(
                fromJson<ArrayList<LocationUpdate>>(rawString!!)
            )
        }
        return list
    }

    private fun deleteUserLocations(list: ArrayList<LocationUpdate>) {
        val listSaved = getUserLocations()
        val toInsert: ArrayList<LocationUpdate> = ArrayList()

        listSaved.forEach { saved ->
            var found = false
            list.forEach { toRemove ->
                if (toRemove.id == saved.id)
                    found = true
            }
            if (!found)
                toInsert.add(saved)
        }

        getSharedPref()
            .edit()
            .putString(
                USER_LOCATIONS,
                toJson(toInsert)
            )
            .apply()

        locationUpdatesSendToServerForUpdate.clear()
    }

    private fun notAuthorised() {
        clearAllData()

        /*Thread(
            Runnable {
                Thread.sleep(1000)
                restartApp()
            }
        ).start()*/
    }

    private fun restartApp() {
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                context,
                123,
                Intent(context, LoginActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val alarmManager: AlarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        alarmManager.set(
            AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent
        )

        exitProcess(0)
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
