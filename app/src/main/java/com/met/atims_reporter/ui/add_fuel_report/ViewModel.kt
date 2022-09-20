package com.met.atims_reporter.ui.add_fuel_report

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.met.atims_reporter.model.FuelType
import com.met.atims_reporter.model.VehicleList
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import java.io.File

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataVehicles: MediatorLiveData<Event<ArrayList<VehicleList>>> =
        MediatorLiveData()
    val mediatorLiveDataVehiclesError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataFuelTypes: MediatorLiveData<Event<ArrayList<FuelType>>> =
        MediatorLiveData()
    val mediatorLiveDataFuelTypesError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataAddFuelReport: MediatorLiveData<Event<Result>> =
        MediatorLiveData()
    val mediatorLiveDataAddFuelReportError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    init {
        mediatorLiveDataVehicles.addSource(
            repository.mutableLiveDataVehicleList,
            Observer<Event<ArrayList<VehicleList>>> { t ->
                mediatorLiveDataVehicles.postValue(t)
            }
        )
        mediatorLiveDataVehiclesError.addSource(
            repository.mutableLiveDataVehicleError,
            Observer<Event<Result>> { t ->
                mediatorLiveDataVehiclesError.postValue(t)
            }
        )
        mediatorLiveDataFuelTypes.addSource(
            repository.mutableLiveDataFuelTypeList,
            Observer<Event<ArrayList<FuelType>>> { t ->
                mediatorLiveDataFuelTypes.postValue(t)
            }
        )
        mediatorLiveDataFuelTypesError.addSource(
            repository.mutableLiveDataFuelTypeError,
            Observer<Event<Result>> { t ->
                mediatorLiveDataFuelTypesError.postValue(t)
            }
        )

        mediatorLiveDataAddFuelReport.addSource(
            repository.mutableLiveDataAddFuelReort,
            Observer<Event<Result>> { t ->
                mediatorLiveDataAddFuelReport.postValue(t)
            }
        )
        mediatorLiveDataAddFuelReportError.addSource(
            repository.mutableLiveDataAddFuelReortError,
            Observer<Event<Result>> { t ->
                mediatorLiveDataAddFuelReportError.postValue(t)
            }
        )


    }

    fun getVehicles() = repository.getVehicleList()

    fun getFuelTypes() = repository.getFuelTypes()

    fun AddFuelReportApi(
        vehicle_id: String,
        cost_per_galon: String,
        total_cost: String,
        fuel_quantity: String,
        refueling_date: String,
        refueling_time: String,
        fuel_type: String,
        odo_meter_reading: String,
        fuel_taken_tank: String,

        fuel_taken_can: String,
        latitude: String,
        longitude: String,
        note: String,
        status: String,
        isReceipt: Boolean,
        image: File?
    ) {
        repository.addFuelReport(
            vehicle_id,
            cost_per_galon,
            total_cost,
            fuel_quantity,
            refueling_date,
            refueling_time,
            fuel_type,
            odo_meter_reading,
            fuel_taken_tank,
            fuel_taken_can,
            latitude,
            longitude,
            note,
            status,
            isReceipt,
            image
        )
    }
}