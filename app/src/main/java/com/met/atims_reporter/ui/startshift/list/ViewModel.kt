package com.met.atims_reporter.ui.startshift.list

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.OperationList
import com.met.atims_reporter.model.ShiftType
import com.met.atims_reporter.model.StateList
import com.met.atims_reporter.model.ZoneList
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataStateList: MediatorLiveData<Event<ArrayList<StateList>>> =
        MediatorLiveData()
    val mediatorLiveDataStateListError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataOperationList: MediatorLiveData<Event<ArrayList<OperationList>>> =
        MediatorLiveData()
    val mediatorLiveDataOperationListError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataZoneList: MediatorLiveData<Event<ArrayList<ZoneList>>> =
        MediatorLiveData()
    val mediatorLiveDataZoneListError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataShiftType: MediatorLiveData<Event<ArrayList<ShiftType>>> =
        MediatorLiveData()
    val mediatorLiveDataShiftTypeError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    init {
        mediatorLiveDataStateList.addSource(
            repository.mutableLiveDataStateList
        ) { t -> mediatorLiveDataStateList.postValue(t) }
        mediatorLiveDataStateListError.addSource(
            repository.mutableLiveDataStateListError
        ) { t -> mediatorLiveDataStateListError.postValue(t) }


        mediatorLiveDataOperationList.addSource(
            repository.mutableLiveDataOperationList
        ) { t -> mediatorLiveDataOperationList.postValue(t) }
        mediatorLiveDataOperationListError.addSource(
            repository.mutableLiveDataOperationError
        ) { t -> mediatorLiveDataOperationListError.postValue(t) }

        mediatorLiveDataZoneList.addSource(
            repository.mutableLiveDataZoneList
        ) { t -> mediatorLiveDataZoneList.postValue(t) }
        mediatorLiveDataZoneListError.addSource(
            repository.mutableLiveDataZoneError
        ) { t -> mediatorLiveDataZoneListError.postValue(t) }

        mediatorLiveDataShiftType.addSource(
            repository.mutableLiveDataShiftType
        ) { t -> mediatorLiveDataShiftType.postValue(t) }
        mediatorLiveDataShiftTypeError.addSource(
            repository.mutableLiveDataShiftTypeError
        ) { t -> mediatorLiveDataShiftTypeError.postValue(t) }
    }

    fun getStateList() = repository.getStateList()

    fun getOperationList(stateId: String) = repository.getOperationList(stateId)

    fun getZoneList(stateId: String, operationAreaId: String) =
        repository.getZoneList(stateId, operationAreaId)

    fun getShiftTime() =
        repository.getShiftType()

}