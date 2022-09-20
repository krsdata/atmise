package com.met.atims_reporter.enums

enum class HomeGridItems(private val title: String, private val permissionTypeId: Int) {
    START_SHIFT("Start Shift", 2),
    INCIDENTS("Incident", 1),
    FUEL("Fuel", 3),
    PRE_OPS("Pre-Ops", 4),
    EXTRA_TIME("Extra Time", 5),
    CRASH_REPORT("Crash Report", 6),
    SOS("Emergency", 7),
    HELP("Help", 8),
    MAINTENANCE_REPORT("Maintenance Request", 9),
    INSPECTION("Inspection", 10),
    SEND_SURVEY("Send Survey", 37),
    ADMIN("Admin", 100);

    fun getTitle(): String {
        return title
    }

    fun getPermissionTypeId() = permissionTypeId
}