package com.met.atims_reporter.ui.fuel_report

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.*
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataFuelList: MediatorLiveData<Event<ArrayList<FuelListDetails>>> =
        MediatorLiveData()
    val mediatorLiveDataFuelListError: MediatorLiveData<Event<Result>> = MediatorLiveData()



    init {
        mediatorLiveDataFuelList.addSource(
            repository.mutableLiveDataFuelList
        ) { t -> mediatorLiveDataFuelList.postValue(t) }
        mediatorLiveDataFuelListError.addSource(
            repository.mutableLiveDataFuelError
        ) { t -> mediatorLiveDataFuelListError.postValue(t) }

    }

    fun getFuelList() = repository.getFuelListing()

}