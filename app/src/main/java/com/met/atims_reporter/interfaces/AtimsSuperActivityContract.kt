package com.met.atims_reporter.interfaces

interface AtimsSuperActivityContract {
    fun goBack() {}
    fun logout() {}
    fun notification() {}
    fun addIncident() {}
    fun addFuelReport() {}
    fun addCrashReport() {}
    fun addInspection() {}
    fun addMaintenanceReport() {}
    fun cancelSOS(){}
    fun addExtraTimeReport() {}
    fun filterExtraTimeList(startDate: String, endDate: String) {}
}