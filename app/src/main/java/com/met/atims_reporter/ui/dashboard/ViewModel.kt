package com.met.atims_reporter.ui.dashboard

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.StartBreakTimeResponse
import com.met.atims_reporter.model.VehicleList
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataLogout: MediatorLiveData<Event<String>> = MediatorLiveData()
    val mediatorLiveDataLogoutError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataStartBreak: MediatorLiveData<Event<StartBreakTimeResponse>> =
        MediatorLiveData()
    val mediatorLiveDataStartBreakError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    val mediatorLiveDataEndBreak: MediatorLiveData<Event<String>> =
        MediatorLiveData()
    val mediatorLiveDataEndBreakError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    val mediatorLiveDataVehicelList: MediatorLiveData<Event<ArrayList<VehicleList>>> =
        MediatorLiveData()
    val mediatorLiveDataVehicelListError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    init {
        mediatorLiveDataLogout.addSource(
            repository.mutableLiveDataLogout
        ) { t -> mediatorLiveDataLogout.postValue(t) }
        mediatorLiveDataLogoutError.addSource(
            repository.mutableLiveDataLoginError
        ) { t -> mediatorLiveDataLogoutError.postValue(t) }
        mediatorLiveDataStartBreak.addSource(
            repository.mutableLiveDataStartBreak
        ) { t -> mediatorLiveDataStartBreak.postValue(t) }
        mediatorLiveDataStartBreakError.addSource(
            repository.mutableLiveDataStartBreakError
        ) { t -> mediatorLiveDataStartBreakError.postValue(t) }
        mediatorLiveDataEndBreak.addSource(
            repository.mutableLiveDataEndBreak
        ) { t -> mediatorLiveDataEndBreak.postValue(t) }
        mediatorLiveDataEndBreakError.addSource(
            repository.mutableLiveDataEndBreakError
        ) { t -> mediatorLiveDataEndBreakError.postValue(t) }
        mediatorLiveDataVehicelList.addSource(
            repository.mutableLiveDataVehicleList
        ) { t -> mediatorLiveDataVehicelList.postValue(t) }
        mediatorLiveDataVehicelListError.addSource(
            repository.mutableLiveDataVehicleError
        ) { t -> mediatorLiveDataVehicelListError.postValue(t) }
    }

    fun logout() = repository.logout()

    fun endBreak() = repository.endBreak()

    fun getShiftReportRequest() = repository.getShiftReportRequest()

    fun getVehicleList() =
        repository.getVehicleList()
}