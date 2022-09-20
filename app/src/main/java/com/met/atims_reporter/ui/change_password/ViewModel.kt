package com.met.atims_reporter.ui.change_password

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataChangePassword: MediatorLiveData<Event<String>> = MediatorLiveData()
    val mediatorLiveDataChangePasswordError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    init {
        mediatorLiveDataChangePassword.addSource(
            repository.mutableLiveDataChangePaassword,
            Observer { t -> mediatorLiveDataChangePassword.postValue(t) }
        )
        mediatorLiveDataChangePasswordError.addSource(
            repository.mutableLiveDataChangePaasswordError,
            Observer { t -> mediatorLiveDataChangePasswordError.postValue(t) }
        )
    }

    fun changePassword(password: String) {
        repository.changePassword(password)
    }
}