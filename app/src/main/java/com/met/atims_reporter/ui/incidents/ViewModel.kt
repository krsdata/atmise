package com.met.atims_reporter.ui.incidents

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.IncidentCloseRequest
import com.met.atims_reporter.model.IncidentDetails
import com.met.atims_reporter.model.IncidentStatusChangeRequest
import com.met.atims_reporter.model.IncidentTypeResponce
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataIncidentList: MediatorLiveData<Event<ArrayList<IncidentDetails>>> =
        MediatorLiveData()
    val mediatorLiveDataIncidentListError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataIncidentStatusUpdate: MediatorLiveData<Event<String>> =
        MediatorLiveData()
    val mediatorLiveDataIncidentStatusUpdateError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    val mediatorLiveDataCloseIncidentStatusUpdate: MediatorLiveData<Event<String>> =
        MediatorLiveData()
    val mediatorLiveDataCloseIncidentStatusUpdateError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    val mediatorLiveDataIncidentTypeList: MediatorLiveData<Event<ArrayList<IncidentTypeResponce>>> =
        MediatorLiveData()
    val mediatorLiveDataIncidentTypeError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataUpdateWazeInformation: MediatorLiveData<Event<String>> = MediatorLiveData()
    val mediatorLiveDataUpdateWazeInformationError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    init {
        mediatorLiveDataIncidentList.addSource(
            repository.mutableLiveDataIncidentListList
        ) { t -> mediatorLiveDataIncidentList.postValue(t) }
        mediatorLiveDataIncidentListError.addSource(
            repository.mutableLiveDataIncidentListError
        ) { t -> mediatorLiveDataIncidentListError.postValue(t) }
        mediatorLiveDataIncidentStatusUpdate.addSource(
            repository.mutableLiveDataIncidentStatusUpdate
        ) { t -> mediatorLiveDataIncidentStatusUpdate.postValue(t) }
        mediatorLiveDataIncidentStatusUpdateError.addSource(
            repository.mutableLiveDataIncidentStatusUpdateError
        ) { t -> mediatorLiveDataIncidentStatusUpdateError.postValue(t) }
        mediatorLiveDataCloseIncidentStatusUpdate.addSource(
            repository.mutableLiveDataCloseIncident
        ) { t -> mediatorLiveDataCloseIncidentStatusUpdate.postValue(t) }
        mediatorLiveDataCloseIncidentStatusUpdateError.addSource(
            repository.mutableLiveDataCloseIncidentError
        ) { t -> mediatorLiveDataCloseIncidentStatusUpdateError.postValue(t) }
        mediatorLiveDataIncidentTypeList.addSource(
            repository.mutableLiveIncidentTypeList
        ) { t -> mediatorLiveDataIncidentTypeList.postValue(t) }
        mediatorLiveDataIncidentTypeError.addSource(
            repository.mutableLiveIncidentTypeError
        ) { t -> mediatorLiveDataIncidentTypeError.postValue(t) }
        mediatorLiveDataUpdateWazeInformation.addSource(
            repository.mutableLiveDataUpdateWazeInformation
        ) { t ->
                mediatorLiveDataUpdateWazeInformation.postValue(t)
            }

        mediatorLiveDataUpdateWazeInformationError.addSource(
            repository.mutableLiveDataUpdateWazeInformationError)
             { t ->
                mediatorLiveDataUpdateWazeInformationError.postValue(t)
            }

    }

    fun getIncidentList() = repository.getIncidentList()

    fun updateIncident(incidentStatusChangeRequest: IncidentStatusChangeRequest) =
        repository.updateIncidentStatus(incidentStatusChangeRequest)

    fun closeIncident(incidentCloseRequest: IncidentCloseRequest) =
        repository.closeIncident(incidentCloseRequest)

    fun getIncidentTypeList() = repository.getIncidentTypeList()

    fun updateWazeInformation(lat: String, long: String, direction: String, desc: String) =
        repository.updateWazeInformation(lat, long, direction, desc)
}