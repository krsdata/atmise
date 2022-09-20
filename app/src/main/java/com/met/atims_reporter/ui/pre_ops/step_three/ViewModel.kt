package com.met.atims_reporter.ui.pre_ops.step_three

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.*
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataToolList: MediatorLiveData<Event<ArrayList<ToolListResponce>>> =
        MediatorLiveData()
    val mediatorLiveDataToolListError: MediatorLiveData<Event<Result>> = MediatorLiveData()



    init {
        mediatorLiveDataToolList.addSource(
            repository.mutableLiveDataToolList
        ) { t -> mediatorLiveDataToolList.postValue(t) }
        mediatorLiveDataToolListError.addSource(
            repository.mutableLiveDataToolListError
        ) { t -> mediatorLiveDataToolListError.postValue(t) }

    }

    fun getToolList() = repository.getToolList()

}