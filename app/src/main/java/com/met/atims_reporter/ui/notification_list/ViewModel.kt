package com.met.atims_reporter.ui.notification_list

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.*
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataNoificationList: MediatorLiveData<Event<NotificationListResponce>> =
        MediatorLiveData()
    val mediatorLiveDataNotificationListError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataNoificationReadUnRead: MediatorLiveData<Event<String>> =
        MediatorLiveData()
    val mediatorLiveDataNoificationReadUnReadError: MediatorLiveData<Event<Result>> = MediatorLiveData()


    init {
        mediatorLiveDataNoificationList.addSource(
            repository.mutableLiveDataNotificationList
        ) { t -> mediatorLiveDataNoificationList.postValue(t) }
        mediatorLiveDataNotificationListError.addSource(
            repository.mutableLiveDataNotificationListError
        ) { t -> mediatorLiveDataNotificationListError.postValue(t) }

        mediatorLiveDataNoificationReadUnRead.addSource(
            repository.mutableLiveDataNotificationReadUnReadRequest
        ) { t -> mediatorLiveDataNoificationReadUnRead.postValue(t) }
        mediatorLiveDataNoificationReadUnReadError.addSource(
            repository.mutableLiveDataNotificationReadUnReadRequestError
        ) { t -> mediatorLiveDataNoificationReadUnReadError.postValue(t) }

    }

    fun getNotificationList() = repository.getNotificationList()
    fun NotificationReadUnread(notificationManager:String,isRead:String)= repository.notificationReadUnRead(notificationManager,isRead)
}