package com.met.atims_reporter.ui.add_incident.media

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.met.atims_reporter.model.Contract
import com.met.atims_reporter.model.VehicleInformation
import com.met.atims_reporter.model.Vendor
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import java.io.File

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataAddOrUpdateIncident: MediatorLiveData<Event<String>> =
        MediatorLiveData()
    val mediatorLiveDataAddOrUpdateIncidentError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    val mediatorLiveDataUpdateWazeInformation: MediatorLiveData<Event<String>> = MediatorLiveData()
    val mediatorLiveDataUpdateWazeInformationError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    init {
        mediatorLiveDataAddOrUpdateIncident.addSource(
            repository.mutableLiveDataAddOrUpdateIncident,
            Observer<Event<String>> { t ->
                mediatorLiveDataAddOrUpdateIncident.postValue(t)
            }
        )
        mediatorLiveDataAddOrUpdateIncidentError.addSource(
            repository.mutableLiveDataAddOrUpdateIncidentError,
            Observer<Event<Result>> { t ->
                mediatorLiveDataAddOrUpdateIncidentError.postValue(t)
            }
        )
        mediatorLiveDataUpdateWazeInformation.addSource(
            repository.mutableLiveDataUpdateWazeInformation,
            Observer { t ->
                mediatorLiveDataUpdateWazeInformation.postValue(t)
            }
        )
        mediatorLiveDataUpdateWazeInformationError.addSource(
            repository.mutableLiveDataUpdateWazeInformationError,
            Observer { t ->
                mediatorLiveDataUpdateWazeInformationError.postValue(t)
            }
        )
    }

    fun addOrUpdateIncident(
        incidentReportId: String? = null,
        latitude: String = "",
        longitude: String = "",
        callAt: String = "",
        callStarted: String = "",
        callComplete: String = "",
        incidentTime: String = "",
        incidentType: String,
        trafficDirection: String,
        mileMaker: String = "",
        propertyDamage: String = "",
        crashInvolced: String = "",
        firstResponder: String = "",
        firstResponderUnit: String = "",
        roadSurver: String = "",
        laneLocation: String = "",
        personTransported: String = "",
        companyColor: String = "",
        vehicleType: String = "",
        assistType: String = "",
        comments: String = "",
        actionStatus: String = "",
        userId: String = "",
        companyId: String = "",
        source: String = "",
        incedent_photo: File?,
        incedent_video: File?,
        shift_id: String,
        plate_no: String,
        note: String,
        incident_status: String,
        direction: String,
        description: String,
        companyRoute: String,
        vehicleQty: String,
        vehicleId: String,
        vehicleInformation: ArrayList<VehicleInformation>,
        vendor: Vendor? = null,
        contract: Contract? = null,
        ramp_lane:String?=null,
        travel_lanes_blocked:String?=null,
        lane_restoration_time:String?=null,
        incident_no:String?=null
    ) = repository.addOrUpdateIncident(
        incidentReportId,
        latitude,
        longitude,
        callAt,
        callStarted,
        callComplete,
        incidentTime,
        incidentType,
        trafficDirection,
        mileMaker,
        propertyDamage,
        crashInvolced,
        firstResponder,
        firstResponderUnit,
        roadSurver,
        laneLocation,
        personTransported,
        companyColor,
        vehicleType,
        assistType,
        comments,
        actionStatus,
        userId,
        companyId,
        source,
        incedent_photo,
        incedent_video,
        shift_id,
        plate_no,
        note,
        incident_status,
        direction,
        description,
        companyRoute,
        vehicleQty,
        vehicleId,
        vehicleInformation,
        vendor,
        contract,
       ramp_lane?:"",
        travel_lanes_blocked?:"",
        lane_restoration_time?:"",
        incident_no?:""
    )

    fun updateWazeInformation(lat: String, long: String, direction: String, desc: String) =
        repository.updateWazeInformation(lat, long, direction, desc)
}