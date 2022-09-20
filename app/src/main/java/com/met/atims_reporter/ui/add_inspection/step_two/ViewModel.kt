package com.met.atims_reporter.ui.add_inspection.step_two

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.*
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataQueestionList: MediatorLiveData<Event<ArrayList<InsectionQuestionLisResponse>>> =
        MediatorLiveData()
    val mediatorLiveDataQueestionError: MediatorLiveData<Event<Result>> = MediatorLiveData()



    init {
        mediatorLiveDataQueestionList.addSource(
            repository.mutableLiveInsQuestionList
        ) { t -> mediatorLiveDataQueestionList.postValue(t) }
        mediatorLiveDataQueestionError.addSource(
            repository.mutableLiveInsQuestionListError
        ) { t -> mediatorLiveDataQueestionError.postValue(t) }

    }

    fun getQuestionList() = repository.getInspectionQuestion()

}