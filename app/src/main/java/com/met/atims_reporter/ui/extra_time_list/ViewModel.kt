package com.met.atims_reporter.ui.extra_time_list

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.*
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataExtraTimeList: MediatorLiveData<Event<ArrayList<ExtraTime>>> =
        MediatorLiveData()
    val mediatorLiveDataExtraTimeListError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataExtraTimeCancelReasons: MediatorLiveData<Event<ArrayList<ExtraTimeCancelReason>>> =
        MediatorLiveData()
    val mediatorLiveDataExtraTimeCancelReasonsError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    val mediatorLiveDataExtraTimeRequest: MediatorLiveData<Event<String>> =
        MediatorLiveData()
    val mediatorLiveDataExtraTimeRequestError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()
    val mediatorLiveDataCallList: MediatorLiveData<Event<ArrayList<CallAtTimeSpinnerData>>> =
        MediatorLiveData()
    val mediatorLiveDatCallListError: MediatorLiveData<Event<Result>> = MediatorLiveData()
    init {
        mediatorLiveDataExtraTimeList.addSource(
            repository.mutableLiveDataExtraTimeList
        ) { t -> mediatorLiveDataExtraTimeList.postValue(t) }
        mediatorLiveDataExtraTimeListError.addSource(
            repository.mutableLiveDataExtraTimeListError
        ) { t -> mediatorLiveDataExtraTimeListError.postValue(t) }

        mediatorLiveDataExtraTimeCancelReasons.addSource(
            repository.mutableLiveDataExtraTimeCancelReasons
        ) { t -> mediatorLiveDataExtraTimeCancelReasons.postValue(t) }
        mediatorLiveDataExtraTimeCancelReasonsError.addSource(
            repository.mutableLiveDataExtraTimeCancelReasonsError
        ) { t -> mediatorLiveDataExtraTimeCancelReasonsError.postValue(t) }

        mediatorLiveDataExtraTimeRequest.addSource(
            repository.mutableLiveDataExtraTimeRequest
        ) { t -> mediatorLiveDataExtraTimeRequest.postValue(t) }
        mediatorLiveDataExtraTimeRequestError.addSource(
            repository.mutableLiveDataExtraTimeRequestError
        ) { t -> mediatorLiveDataExtraTimeRequestError.postValue(t) }

        mediatorLiveDataCallList.addSource(
            repository.mutableLiveDataCallList
        ) { t -> mediatorLiveDataCallList.postValue(t) }
        mediatorLiveDatCallListError.addSource(
            repository.mutableLiveDataCallError
        ) { t -> mediatorLiveDatCallListError.postValue(t) }
    }

    fun getExtraTimeList(extraTimeListRequest: ExtraTimeListRequest) =
        repository.getExtraTimeList(extraTimeListRequest)

    fun getExtraTimeCancelReasons() = repository.getExtraTimeCancelReasons()

    fun updateExtraTime(extraTimeRequest: ExtraTimeRequest) =
        repository.updateExtraTime(extraTimeRequest)
    fun getCallList() = repository.pushCallSpinnerData()
}