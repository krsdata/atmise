package com.met.atims_reporter.ui.maintenance_report

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.MaintenanceReport
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataMaintenanceReportList: MediatorLiveData<Event<ArrayList<MaintenanceReport>>> =
        MediatorLiveData()
    val mediatorLiveDataMaintenanceReportListError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()


    init {
        mediatorLiveDataMaintenanceReportList.addSource(
            repository.mutableLiveDataMaintenanceReportList
        ) { t -> mediatorLiveDataMaintenanceReportList.postValue(t) }
        mediatorLiveDataMaintenanceReportListError.addSource(
            repository.mutableLiveDataMaintenanceReportListError
        ) { t -> mediatorLiveDataMaintenanceReportListError.postValue(t) }

    }

    fun getMaintenanceReportList() = repository.getMaintenanceReport()

}