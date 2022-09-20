package com.met.atims_reporter.ui.home

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.enums.SendSurveyVia
import com.met.atims_reporter.model.HomeGridItem
import com.met.atims_reporter.model.SendSurveyResponse
import com.met.atims_reporter.model.VehicleList
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataDashboardMenus: MediatorLiveData<Event<ArrayList<HomeGridItem>>> =
        MediatorLiveData()
    val mediatorLiveDataDashboardMenusError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataEndShift: MediatorLiveData<Event<String>> = MediatorLiveData()
    val mediatorLiveDataEndShiftError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataPermissionToEndShift: MediatorLiveData<Event<String>> = MediatorLiveData()
    val mediatorLiveDataPermissionToEndShiftError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    val mediatorLiveDataPatrollingStatusChange: MediatorLiveData<Event<String>> = MediatorLiveData()
    val mediatorLiveDataPatrollingStatusChangeError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    val mediatorLiveDataSendSurvey: MediatorLiveData<Event<SendSurveyResponse>> = MediatorLiveData()
    val mediatorLiveDataSendSurveyError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    val mediatorLiveDataVehicelList: MediatorLiveData<Event<ArrayList<VehicleList>>> =
        MediatorLiveData()
    val mediatorLiveDataVehicelListError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    init {
        mediatorLiveDataDashboardMenus.addSource(
            repository.mutableLiveDataDashboardMenus
        ) { t -> mediatorLiveDataDashboardMenus.postValue(t) }
        mediatorLiveDataDashboardMenusError.addSource(
            repository.mutableLiveDataDashboardMenusError
        ) { t -> mediatorLiveDataDashboardMenusError.postValue(t) }
        mediatorLiveDataEndShift.addSource(
            repository.mutableLiveDataEndShift
        ) { t -> mediatorLiveDataEndShift.postValue(t) }
        mediatorLiveDataEndShiftError.addSource(
            repository.mutableLiveDataEndShiftError
        ) { t -> mediatorLiveDataEndShiftError.postValue(t) }
        mediatorLiveDataPermissionToEndShift.addSource(
            repository.mutableLiveDataPermissionToEndShift
        ) { t -> mediatorLiveDataPermissionToEndShift.postValue(t) }
        mediatorLiveDataPermissionToEndShiftError.addSource(
            repository.mutableLiveDataPermissionToEndShiftError
        ) { t -> mediatorLiveDataPermissionToEndShiftError.postValue(t) }
        mediatorLiveDataPatrollingStatusChange.addSource(
            repository.mutableLiveDataPatrollingStatusChange
        ) { t -> mediatorLiveDataPatrollingStatusChange.postValue(t) }
        mediatorLiveDataPatrollingStatusChangeError.addSource(
            repository.mutableLiveDataPatrollingStatusChangeError
        ) { t -> mediatorLiveDataPatrollingStatusChangeError.postValue(t) }
        mediatorLiveDataSendSurvey.addSource(
            repository.mutableLiveDataSendSurvey
        ) { t -> mediatorLiveDataSendSurvey.postValue(t) }
        mediatorLiveDataSendSurveyError.addSource(
            repository.mutableLiveDataSendSurveyError
        ) { t -> mediatorLiveDataSendSurveyError.postValue(t) }
        mediatorLiveDataVehicelList.addSource(
            repository.mutableLiveDataVehicleList
        ) { t -> mediatorLiveDataVehicelList.postValue(t) }
        mediatorLiveDataVehicelListError.addSource(
            repository.mutableLiveDataVehicleError
        ) { t -> mediatorLiveDataVehicelListError.postValue(t) }
    }

    fun getHomeGridItems() = repository.getHomeGridItems()

    fun endShift() = repository.endShift()

    fun permissionToEndShift() = repository.permissionToEndShift()

    fun changePatrollingStatus(
        latitude: String,
        longitude: String,
        direction: String,
        description: String
    ) =
        repository.changePatrollingStatus(latitude, longitude, direction, description)

    fun startBreak() = repository.startBreak()

    fun sendSurvey(name: String? = null, email: String, sendSurveyVia: SendSurveyVia) =
        repository.sendSurvey(name, email, sendSurveyVia)

    fun getVehicleList() =
        repository.getVehicleList()
}