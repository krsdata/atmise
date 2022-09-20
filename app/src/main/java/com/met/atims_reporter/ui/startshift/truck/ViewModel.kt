package com.met.atims_reporter.ui.startshift.truck

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.StartShiftRequest
import com.met.atims_reporter.model.StartShiftResponse
import com.met.atims_reporter.model.VehicleList
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataVehicelList: MediatorLiveData<Event<ArrayList<VehicleList>>> =
        MediatorLiveData()
    val mediatorLiveDataVehicelListError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataStartShift: MediatorLiveData<Event<StartShiftResponse>> =
        MediatorLiveData()
    val mediatorLiveDataStartShiftError: MediatorLiveData<Event<Result>> = MediatorLiveData()


    init {
        mediatorLiveDataVehicelList.addSource(
            repository.mutableLiveDataVehicleList
        ) { t -> mediatorLiveDataVehicelList.postValue(t) }
        mediatorLiveDataVehicelListError.addSource(
            repository.mutableLiveDataVehicleError
        ) { t -> mediatorLiveDataVehicelListError.postValue(t) }
        mediatorLiveDataStartShift.addSource(
            repository.mutableLiveDataStartShift
        ) { t -> mediatorLiveDataStartShift.postValue(t) }
        mediatorLiveDataStartShiftError.addSource(
            repository.mutableLiveDataStartShiftError
        ) { t -> mediatorLiveDataStartShiftError.postValue(t) }
    }

    fun getVehicleList(stateId: String, operationAreaId: String, zoneId: String) =
        repository.getVehicleList(stateId, operationAreaId, zoneId)

    fun startShift(startShiftRequest: StartShiftRequest) {
        repository.startShift(startShiftRequest)
    }
}