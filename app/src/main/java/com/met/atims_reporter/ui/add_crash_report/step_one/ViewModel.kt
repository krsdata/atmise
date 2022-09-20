package com.met.atims_reporter.ui.add_crash_report.step_one

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.GetUserListResponce
import com.met.atims_reporter.model.StateList
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataStateList: MediatorLiveData<Event<ArrayList<StateList>>> =
        MediatorLiveData()
    val mediatorLiveDataStateListError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataGetUserList: MediatorLiveData<Event<ArrayList<GetUserListResponce>>> =
        MediatorLiveData()
    val mediatorLiveDataGetUserListError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    init {
        mediatorLiveDataStateList.addSource(
            repository.mutableLiveDataStateList
        ) { t ->
            mediatorLiveDataStateList.postValue(t)
        }
        mediatorLiveDataStateListError.addSource(
            repository.mutableLiveDataStateListError
        ) { t ->
            mediatorLiveDataStateListError.postValue(t)
        }

        mediatorLiveDataGetUserList.addSource(
            repository.mutableLiveDataGetUserList
        ) { t -> mediatorLiveDataGetUserList.postValue(t) }
        mediatorLiveDataGetUserListError.addSource(
            repository.mutableLiveDataGetUserListError
        ) { t -> mediatorLiveDataGetUserListError.postValue(t) }
    }

    fun getStateList() = repository.getStateList()
    fun getUserList()=repository.getUserList("crash")
}