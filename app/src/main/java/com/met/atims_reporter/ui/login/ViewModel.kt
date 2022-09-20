package com.met.atims_reporter.ui.login

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.LoginRequest
import com.met.atims_reporter.model.UserDetails
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataLogin: MediatorLiveData<Event<UserDetails>> = MediatorLiveData()
    val mediatorLiveDataLoginError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataFirebaseMessagingToken: MediatorLiveData<Event<String>> =
        MediatorLiveData()
    val mediatorLiveDataFirebaseMessagingTokenError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    init {
        mediatorLiveDataLogin.addSource(
            repository.mutableLiveDataLogin
        ) { t -> mediatorLiveDataLogin.postValue(t) }
        mediatorLiveDataLoginError.addSource(
            repository.mutableLiveDataLoginError
        ) { t -> mediatorLiveDataLoginError.postValue(t) }
        mediatorLiveDataFirebaseMessagingToken.addSource(
            repository.mutableLiveDataFirebaseMessagingToken
        ) { t -> mediatorLiveDataFirebaseMessagingToken.postValue(t) }
        mediatorLiveDataFirebaseMessagingTokenError.addSource(
            repository.mutableLiveDataFirebaseMessagingTokenError
        ) { t -> mediatorLiveDataFirebaseMessagingTokenError.postValue(t) }
    }

    fun login(loginRequest: LoginRequest) = repository.login(loginRequest)

    fun getFirebaseToken() = repository.getFirebaseToken()
}