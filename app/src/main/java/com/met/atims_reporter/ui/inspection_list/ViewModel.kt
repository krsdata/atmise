package com.met.atims_reporter.ui.inspection_list

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.*
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataInspectionList: MediatorLiveData<Event<ArrayList<InspectionListResponce>>> =
        MediatorLiveData()
    val mediatorLiveDataInspectionListError: MediatorLiveData<Event<Result>> = MediatorLiveData()



    init {
        mediatorLiveDataInspectionList.addSource(
            repository.mutableLiveDataInspectionList
        ) { t -> mediatorLiveDataInspectionList.postValue(t) }
        mediatorLiveDataInspectionListError.addSource(
            repository.mutableLiveDataInspectionListError
        ) { t -> mediatorLiveDataInspectionListError.postValue(t) }

    }

    fun getInspectionList() = repository.getInspectionList()

}