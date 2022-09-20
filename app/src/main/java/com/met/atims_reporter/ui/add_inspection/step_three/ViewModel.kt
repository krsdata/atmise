package com.met.atims_reporter.ui.add_inspection.step_three

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.*
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataToolList: MediatorLiveData<Event<ArrayList<ToolListResponceInsp>>> =
        MediatorLiveData()
    val mediatorLiveDataToolListError: MediatorLiveData<Event<Result>> = MediatorLiveData()



    init {
        mediatorLiveDataToolList.addSource(
            repository.mutableLiveDataToolListInsp
        ) { t -> mediatorLiveDataToolList.postValue(t) }
        mediatorLiveDataToolListError.addSource(
            repository.mutableLiveDataToolListErrorInsp
        ) { t -> mediatorLiveDataToolListError.postValue(t) }

    }

    fun getToolList() = repository.getToolListInsp()

}