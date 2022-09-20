package com.met.atims_reporter.ui.add_extra_time

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.IncidentTypeResponce
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.repository.Event
import com.met.atims_reporter.util.model.Result

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {
    val mediatorLiveDataIncidentTypeList: MediatorLiveData<Event<ArrayList<IncidentTypeResponce>>> =
        MediatorLiveData()
    val mediatorLiveDataIncidentTypeError: MediatorLiveData<Event<Result>> = MediatorLiveData()
}