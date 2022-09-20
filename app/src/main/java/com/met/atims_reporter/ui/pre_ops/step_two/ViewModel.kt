package com.met.atims_reporter.ui.pre_ops.step_two

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.*
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataQueestionList: MediatorLiveData<Event<ArrayList<QuestionLisResponse>>> =
        MediatorLiveData()
    val mediatorLiveDataQueestionError: MediatorLiveData<Event<Result>> = MediatorLiveData()



    init {
        mediatorLiveDataQueestionList.addSource(
            repository.mutableLiveQuestionList
        ) { t -> mediatorLiveDataQueestionList.postValue(t) }
        mediatorLiveDataQueestionError.addSource(
            repository.mutableLiveQuestionError
        ) { t -> mediatorLiveDataQueestionError.postValue(t) }

    }

    fun getQuestionList() = repository.getQuestionList()

}