package com.met.atims_reporter.ui.sos

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.SOSReasonListResponce
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataSOS: MediatorLiveData<Event<String>> = MediatorLiveData()
    val mediatorLiveDataSOSError: MediatorLiveData<Event<Result>> = MediatorLiveData()


    val mediatorLiveDataSOSReasonList: MediatorLiveData<Event<ArrayList<SOSReasonListResponce>>> =
        MediatorLiveData()
    val mediatorLiveDataSOSReasonListError: MediatorLiveData<Event<Result>> = MediatorLiveData()


    init {
        mediatorLiveDataSOS.addSource(
            repository.mutableLiveDataSendSOS
        ) { t -> mediatorLiveDataSOS.postValue(t) }
        mediatorLiveDataSOSError.addSource(
            repository.mutableLiveDataSendSOSError
        ) { t -> mediatorLiveDataSOSError.postValue(t) }

        mediatorLiveDataSOSReasonList.addSource(
            repository.mutableLiveDataSOSReasonList
        ) { t -> mediatorLiveDataSOSReasonList.postValue(t) }
        mediatorLiveDataSOSReasonListError.addSource(
            repository.mutableLiveDataSOSReasonListError
        ) { t -> mediatorLiveDataSOSReasonListError.postValue(t) }
    }

    fun sendSOS(lat: String, long: String, reasonId: String, message: String) =
        repository.sendSOS(lat, long, reasonId, message)

    fun getUserDeatils() = repository.getUserData()
    fun getVehicelData() = repository.giveVehicleIdToShow()
    fun getSOSReasonList() = repository.getSOSReasonList()
}