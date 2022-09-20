package com.met.atims_reporter.ui.admin

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataAdminHelpSuccess: MediatorLiveData<Event<String>> = MediatorLiveData()
    val mediatorLiveDataAdminHelpError: MediatorLiveData<Event<Result>> = MediatorLiveData()



    init {
        mediatorLiveDataAdminHelpSuccess.addSource(
            repository.mutableLiveDataAdminHelpSuccess
        ) { t -> mediatorLiveDataAdminHelpSuccess.postValue(t) }
        mediatorLiveDataAdminHelpError.addSource(
            repository.mutableLiveDataSendSOSError
        ) { t -> mediatorLiveDataAdminHelpError.postValue(t) }

    }

    fun adminHelp(lat:String,long:String,subject:String,message:String) = repository.adminHelp(lat,long,subject,message)
    fun getUserDeatils()=repository.getUserData()
    fun getVehicelData()=repository.giveVehicleId()
}