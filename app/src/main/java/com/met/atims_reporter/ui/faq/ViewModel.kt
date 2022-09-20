package com.met.atims_reporter.ui.faq

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.*
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataFaqList: MediatorLiveData<Event<ArrayList<FaqResponce>>> =
        MediatorLiveData()
    val mediatorLiveDataFaqListError: MediatorLiveData<Event<Result>> = MediatorLiveData()



    init {
        mediatorLiveDataFaqList.addSource(
            repository.mutableLiveDataFaqList
        ) { t -> mediatorLiveDataFaqList.postValue(t) }
        mediatorLiveDataFaqListError.addSource(
            repository.mutableLiveDataFaqError
        ) { t -> mediatorLiveDataFaqListError.postValue(t) }

    }

    fun getFaqList() = repository.getFaqList()

}