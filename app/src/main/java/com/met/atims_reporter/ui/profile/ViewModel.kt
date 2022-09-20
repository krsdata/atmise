package com.met.atims_reporter.ui.profile

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataUpdateProfile: MediatorLiveData<Event<String>> = MediatorLiveData()
    val mediatorLiveDataUpdateProfileError: MediatorLiveData<Event<Result>> = MediatorLiveData()


    init {
        mediatorLiveDataUpdateProfile.addSource(
            repository.mutableLiveDataUpdateProfile
        ) { t -> mediatorLiveDataUpdateProfile.postValue(t) }
        mediatorLiveDataUpdateProfileError.addSource(
            repository.mutableLiveDataUpdateProfileError
        ) { t -> mediatorLiveDataUpdateProfileError.postValue(t) }

    }

    fun updateProfile(
        first_name: String,
        last_name: String,
        phone: String,
        address: String,
        city: String,
        zip: String,
        profile_image: String? = null
    ) = repository.updateUserProfile(
        first_name, last_name, phone, address, city, zip, profile_image
    )
}