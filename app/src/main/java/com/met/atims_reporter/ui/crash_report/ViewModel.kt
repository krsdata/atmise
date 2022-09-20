package com.met.atims_reporter.ui.crash_report

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.CrashReport
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataCrashReportList: MediatorLiveData<Event<ArrayList<CrashReport>>> =
        MediatorLiveData()
    val mediatorLiveDataCrashReportListError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    init {
        mediatorLiveDataCrashReportList.addSource(
            repository.mutableLiveDataCrashReportList
        ) { t ->
            mediatorLiveDataCrashReportList.postValue(t)
        }
        mediatorLiveDataCrashReportListError.addSource(
            repository.mutableLiveDataCrashReportListError
        ) { t ->
            mediatorLiveDataCrashReportListError.postValue(t)
        }
    }

    fun getCrashReportList() = repository.getCrashReportList()
}