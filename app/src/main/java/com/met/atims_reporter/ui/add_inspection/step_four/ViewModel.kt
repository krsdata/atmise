package com.met.atims_reporter.ui.add_inspection.step_four

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.InspectionFinalRequest
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataPreOpsImageUpload: MediatorLiveData<Event<ArrayList<String>>> =
        MediatorLiveData()
    val mediatorLiveDataPreOpsImageUploadError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataFinalPreOps: MediatorLiveData<Event<String>> =
        MediatorLiveData()
    val mediatorLiveDataFinalPreOpsError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    init {
        mediatorLiveDataPreOpsImageUpload.addSource(
            repository.mutableLiveDataInspectinImageUpload
        ) { t -> mediatorLiveDataPreOpsImageUpload.postValue(t) }
        mediatorLiveDataPreOpsImageUploadError.addSource(
            repository.mutableLiveDataInspectinImageUploadError
        ) { t -> mediatorLiveDataPreOpsImageUploadError.postValue(t) }

        mediatorLiveDataFinalPreOps.addSource(
            repository.mutableLiveDataFinalInspection
        ) { t -> mediatorLiveDataFinalPreOps.postValue(t) }
        mediatorLiveDataFinalPreOpsError.addSource(
            repository.mutableLiveDataFinalInspectionError
        ) { t -> mediatorLiveDataFinalPreOpsError.postValue(t) }

    }

    fun uploadInspectionImage(imagePath: String) = repository.uploadInsectionFile(imagePath)

    fun uploadFinalInspectionData(finalRequest: InspectionFinalRequest) =
        repository.uploadFinalInspectionData(finalRequest)
}