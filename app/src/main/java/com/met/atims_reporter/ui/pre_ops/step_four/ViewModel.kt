package com.met.atims_reporter.ui.pre_ops.step_four

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.PreOpsFileUploadResponse
import com.met.atims_reporter.model.PreOpsFinalRequest
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataPreOpsImageUpload: MediatorLiveData<Event<PreOpsFileUploadResponse>> =
        MediatorLiveData()
    val mediatorLiveDataPreOpsImageUploadError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataFinalPreOps: MediatorLiveData<Event<String>> =
        MediatorLiveData()
    val mediatorLiveDataFinalPreOpsError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    init {
        mediatorLiveDataPreOpsImageUpload.addSource(
            repository.mutableLiveDataPreOpsImageUpload
        ) { t -> mediatorLiveDataPreOpsImageUpload.postValue(t) }
        mediatorLiveDataPreOpsImageUploadError.addSource(
            repository.mutableLiveDataPreOpsImageUploadError
        ) { t -> mediatorLiveDataPreOpsImageUploadError.postValue(t) }

        mediatorLiveDataFinalPreOps.addSource(
            repository.mutableLiveDataFinalPreOps
        ) { t -> mediatorLiveDataFinalPreOps.postValue(t) }
        mediatorLiveDataFinalPreOpsError.addSource(
            repository.mutableLiveDataFinalPreOpsError
        ) { t -> mediatorLiveDataFinalPreOpsError.postValue(t) }

    }

    fun uploadPreOpsImage(imagePath: String) = repository.uploadPreOpsFile(imagePath)

    fun uploadFinalPreOpsData(finalRequest: PreOpsFinalRequest) =
        repository.uploadFinalPreOpsData(finalRequest)
}