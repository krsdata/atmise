package com.met.atims_reporter.ui.add_maintenance_report

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.*
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataStateList: MediatorLiveData<Event<ArrayList<StateList>>> =
        MediatorLiveData()
    val mediatorLiveDataStateListError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataVehicles: MediatorLiveData<Event<ArrayList<VehicleList>>> =
        MediatorLiveData()
    val mediatorLiveDataVehiclesError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataServiceTypes: MediatorLiveData<Event<ArrayList<ServiceType>>> =
        MediatorLiveData()
    val mediatorLiveDataServiceTypesError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataVendors: MediatorLiveData<Event<ArrayList<Vendor>>> =
        MediatorLiveData()
    val mediatorLiveDataVendorsError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataAddMaintenanceReport: MediatorLiveData<Event<AddMaintenanceReportResponse>> =
        MediatorLiveData()
    val mediatorLiveDataAddMaintenanceReportError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    val mediatorLiveDataMaintenanceRequestTypeList: MediatorLiveData<Event<ArrayList<MaintenanceRequestTypeList>>> =
        MediatorLiveData()
    val mediatorLiveDataMaintenanceRequestTypeListError: MediatorLiveData<Event<Result>> = MediatorLiveData()


    init {
        mediatorLiveDataStateList.addSource(
            repository.mutableLiveDataStateList
        ) { t -> mediatorLiveDataStateList.postValue(t) }
        mediatorLiveDataStateListError.addSource(
            repository.mutableLiveDataStateListError
        ) { t -> mediatorLiveDataStateListError.postValue(t) }

        mediatorLiveDataVehicles.addSource(
            repository.mutableLiveDataVehicleList
        ) { t -> mediatorLiveDataVehicles.postValue(t) }
        mediatorLiveDataVehiclesError.addSource(
            repository.mutableLiveDataVehicleError
        ) { t -> mediatorLiveDataVehiclesError.postValue(t) }

        mediatorLiveDataServiceTypes.addSource(
            repository.mutableLiveDataServiceTypes
        ) { t -> mediatorLiveDataServiceTypes.postValue(t) }
        mediatorLiveDataServiceTypesError.addSource(
            repository.mutableLiveDataServiceTypesError
        ) { t -> mediatorLiveDataServiceTypesError.postValue(t) }

        mediatorLiveDataVendors.addSource(
            repository.mutableLiveDataVendors
        ) { t -> mediatorLiveDataVendors.postValue(t) }
        mediatorLiveDataVendorsError.addSource(
            repository.mutableLiveDataVendorsError
        ) { t -> mediatorLiveDataVendorsError.postValue(t) }

        mediatorLiveDataAddMaintenanceReport.addSource(
            repository.mutableLiveDataAddMaintenanceReport
        ) { t -> mediatorLiveDataAddMaintenanceReport.postValue(t) }
        mediatorLiveDataAddMaintenanceReportError.addSource(
            repository.mutableLiveDataAddMaintenanceReportError
        ) { t -> mediatorLiveDataAddMaintenanceReportError.postValue(t) }


        mediatorLiveDataMaintenanceRequestTypeList.addSource(
            repository.mutableLiveDataMaintenanceRequestTypeList
        ) { t -> mediatorLiveDataMaintenanceRequestTypeList.postValue(t) }
        mediatorLiveDataMaintenanceRequestTypeListError.addSource(
            repository.mutableLiveDataMaintenanceRequestTypeListError
        ) { t -> mediatorLiveDataMaintenanceRequestTypeListError.postValue(t) }
    }

    fun addMaintenanceReport(
        imagePath: String,
        vehicleId: String,
        vehicleVinNo: String,
        requestTypeId: String,
        stateId: String,
        stateName: String,
        contractPeriod: String,
        reportDate: String,
        serviceTypeId: String,
        mileage: String,
        serviceCost: String,
        labourHours: String,
        labourMinutes: String,
        vendorId: String,
        vendorName: String,
        repairDescription: String,
        note: String,
        /*maintenanceReportDataId: String,
        reportStatus: String*/
    ) = repository.addMaintenanceReport(
        imagePath,
        vehicleId,
        vehicleVinNo,
        requestTypeId,
        stateId,
        stateName,
        contractPeriod,
        reportDate,
        serviceTypeId,
        mileage,
        serviceCost,
        labourHours,
        labourMinutes,
        vendorId,
        vendorName,
        repairDescription,
        note,
        /*maintenanceReportDataId,
        reportStatus*/
    )
}