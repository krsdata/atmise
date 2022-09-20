package com.met.atims_reporter.ui.inspection_details.one

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.*
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataInspectionDetailsList: MediatorLiveData<Event<InspectionListDetailsResponce>> =
        MediatorLiveData()
    val mediatorLiveDataInspectionDeatilsError: MediatorLiveData<Event<Result>> = MediatorLiveData()



    init {
        mediatorLiveDataInspectionDetailsList.addSource(
            repository.mutableLiveDataInspectionDeatils
        ) { t -> mediatorLiveDataInspectionDetailsList.postValue(t) }
        mediatorLiveDataInspectionDeatilsError.addSource(
            repository.mutableLiveDataInspectionDeatilsError
        ) { t -> mediatorLiveDataInspectionDeatilsError.postValue(t) }

    }

    fun getInspectionDetails(inspectionDeatilsId:String) = repository.getInspectionDetails(inspectionDeatilsId)

}