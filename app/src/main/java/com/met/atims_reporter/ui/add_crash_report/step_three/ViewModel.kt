package com.met.atims_reporter.ui.add_crash_report.step_three

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.AddCrashReportResponse
import com.met.atims_reporter.model.CrashReport
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event


class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataAddCrashReport: MediatorLiveData<Event<AddCrashReportResponse>> =
        MediatorLiveData()
    val mediatorLiveDataAddCrashReportError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataCrashReportFilesUpload: MediatorLiveData<Event<String>> =
        MediatorLiveData()
    val mediatorLiveDataCrashReportFilesUploadError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    init {
        mediatorLiveDataAddCrashReport.addSource(
            repository.mutableLiveDataAddCrashReport
        ) { t ->
            mediatorLiveDataAddCrashReport.postValue(t)
        }
        mediatorLiveDataAddCrashReportError.addSource(
            repository.mutableLiveDataCrashReportListError
        ) { t ->
            mediatorLiveDataAddCrashReportError.postValue(t)
        }
        mediatorLiveDataCrashReportFilesUpload.addSource(
            repository.mutableLiveDataCrashReportFilesUpload
        ) { t ->
            mediatorLiveDataCrashReportFilesUpload.postValue(t)
        }
        mediatorLiveDataCrashReportFilesUploadError.addSource(
            repository.mutableLiveDataCrashReportFilesUploadError
        ) { t ->
            mediatorLiveDataCrashReportFilesUploadError.postValue(t)
        }
    }

    fun addCrashReport(crashReport: CrashReport) = repository.addCrashReport(crashReport)

    fun uploadCrashReportFiles(images: ArrayList<ArrayList<String>>, reportId: String) {
        repository.uploadCrashReportFiles(
            images, reportId
        )
    }
}