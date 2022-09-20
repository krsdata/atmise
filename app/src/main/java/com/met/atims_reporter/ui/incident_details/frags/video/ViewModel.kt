package com.met.atims_reporter.ui.incident_details.frags.video

import android.graphics.Bitmap
import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.repository.Event

class ViewModel(repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataIncidentDetailsVideoThumb: MediatorLiveData<Event<Bitmap>> =
        MediatorLiveData()

    init {
        mediatorLiveDataIncidentDetailsVideoThumb.addSource(
            repository.mutableLiveDataIncidentDetailsVideoThumb
        ) { t -> mediatorLiveDataIncidentDetailsVideoThumb.postValue(t) }
    }
}