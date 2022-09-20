package com.met.atims_reporter.ui.report

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.*
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataShiftReportList: MediatorLiveData<Event<ArrayList<GetShiftReportResponse>>> =
        MediatorLiveData()
    val mediatorLiveDataShiftReportListError: MediatorLiveData<Event<Result>> = MediatorLiveData()



    init {
        mediatorLiveDataShiftReportList.addSource(
            repository.mutableLiveDataShiftReportList
        ) { t -> mediatorLiveDataShiftReportList.postValue(t) }
        mediatorLiveDataShiftReportListError.addSource(
            repository.mutableLiveDataShiftReportListError
        ) { t -> mediatorLiveDataShiftReportListError.postValue(t) }

    }

    fun getShiftReportRequest() = repository.getShiftReportRequest()

}