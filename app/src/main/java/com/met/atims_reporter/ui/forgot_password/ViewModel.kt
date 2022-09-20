package com.met.atims_reporter.ui.forgot_password

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.ForgotPasswordOTPRequest
import com.met.atims_reporter.model.ResetPasswordRequest
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataOTPRequestForResetPassword: MediatorLiveData<Event<String>> =
        MediatorLiveData()
    val mediatorLiveDataOTPRequestForResetPasswordError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    val mediatorLiveDataResetPassword: MediatorLiveData<Event<String>> =
        MediatorLiveData()
    val mediatorLiveDataResetPasswordError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    init {
        mediatorLiveDataOTPRequestForResetPassword.addSource(
            repository.mutableLiveDataOTPRequestForResetPassword
        ) { t -> mediatorLiveDataOTPRequestForResetPassword.postValue(t) }
        mediatorLiveDataOTPRequestForResetPasswordError.addSource(
            repository.mutableLiveDataOTPRequestForResetPasswordError
        ) { t -> mediatorLiveDataOTPRequestForResetPasswordError.postValue(t) }
        mediatorLiveDataResetPassword.addSource(
            repository.mutableLiveDataResetPassword
        ) { t -> mediatorLiveDataResetPassword.postValue(t) }
        mediatorLiveDataResetPasswordError.addSource(
            repository.mutableLiveDataResetPasswordError
        ) { t -> mediatorLiveDataResetPasswordError.postValue(t) }
    }

    fun requestOTPForResetPassword(forgotPasswordOTPRequest: ForgotPasswordOTPRequest) =
        repository.requestOTPToResetPassword(forgotPasswordOTPRequest)

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest) =
        repository.resetPassword(resetPasswordRequest)
}