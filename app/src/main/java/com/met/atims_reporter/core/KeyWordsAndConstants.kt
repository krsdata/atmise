package com.met.atims_reporter.core

object KeyWordsAndConstants {
    const val LOG_TAG = "atims_log"

    //live
    //const val BASE_URL = "https://atims.sspreporter.com/api/"
    //Dev
    const val BASE_URL = "http://dev.fitser.com:3794/ATIMS/api/"

    const val SHARED_PREF_DB = "atimsAppSharedPref"

    const val PRE_OPS_STARTED = "preOpsStared"

    const val SHIFT_ID = "shiftId"
    const val VEHICLE_ID = "vehicleId"
    const val VEHICLE_ID_TO_SHOW = "vehicleIdToShow"
    const val VEHICLE_COLOR = "vehicleColor"
    const val VIN_NUMBER = "vinNumber"
    const val STATE_ID = "stateId"
    const val STATE_NAME = "stateName"
    const val LANE_ID = "stateId"
    const val LANE_NAME = "stateName"
    const val OPERATION_AREA_ID = "operationAreaId"
    const val OPERATION_AREA_NAME = "operationAreaName"
    const val INDICATOR_STATUS = "indicatorStatus"
    const val ROLE_ID = "roleId"
    const val UNREAD_NOTIFICATION_COUNT = "unreadNotificationCount"

    const val BREAK_TIME_ID = "breakTimeId"

    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val DEVICE_FCM_TOKEN = "fcmToken"
    const val CURRENT_LAT = "currentLat"
    const val CURRENT_LONG = "currentLong"

    const val USER_DATA = "userData"

    const val OPERATION_MODE = "operationMode"

    const val DATA = "data"

    const val REQUEST_CODE_PICK_IMAGE = 3000
    const val REQUEST_CODE_RECORD_VIDEO = 3001
    const val REQUEST_CHECK_SETTINGS_FOR_LOCATION = 3002

    const val END_SELF = "endSelf"

    const val HOME_GRID_SPAN = 2
    const val HOME_PAGE_GRID_SPACING = 80

    const val MAX_VIDEO_LENGTH_SECS = 60

    const val LOCATION_MONITORING_NOTIFICATION_ID = 100

    const val USER_LOCATIONS = "userLocations"

    const val ON_SCENE_STATUS_AT_LATITUDE = "onSceneStatusAtLatitude"
    const val ON_SCENE_STATUS_AT_LONGITUDE = "onSceneStatusAtLongitude"
    const val PATROLLING_MILE_LIMIT = 1
    const val ON_GOING_INCIDENTS = "onGoingIncidents"

    const val ROLE_NAME_MANAGER = "Manager"
    const val MANAGER_WANTS_TO_GO_ON_PATROLLING = "managerWantsToGoOnPatrolling"
    const val MANAGER_WANTS_TO_GO_ON_PATROLLING_ASKED = "managerWantsToGoOnPatrollingAsked"

    const val NOT_AUTHORISED = "notAuthorised"

    const val SAVED_INSPECTION_DATA = "savedInspectionData"

    const val NON_CRITICAL_UPDATE_LAST_SHOWN = "nonCriticalUpdateLastShown"
    const val TIME_DIFF_TO_SHOW_NON_CRITICAL_UPDATE = 1000 * 60 * 60 * 24 * 15
    const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 2*60*1000
    const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS:Long = (2*60*1000) / 2
}
