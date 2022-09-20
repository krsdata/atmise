package com.met.atims_reporter.ui.add_incident.data

import androidx.lifecycle.MediatorLiveData
import com.met.atims_reporter.model.*
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.util.SuperViewModel
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

class ViewModel(private val repository: Repository) : SuperViewModel(repository) {

    val mediatorLiveDataColorList: MediatorLiveData<Event<ArrayList<ColorResponce>>> =
        MediatorLiveData()
    val mediatorLiveDataColorError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataTrafficeDirectionList: MediatorLiveData<Event<ArrayList<TraficeDirectionListResponce>>> =
        MediatorLiveData()
    val mediatorLiveDataTraficeDirectionError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataSecendaryCrashList: MediatorLiveData<Event<ArrayList<SecondaryCrashResponce>>> =
        MediatorLiveData()
    val mediatorLiveDataScondaryError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataRouteList: MediatorLiveData<Event<ArrayList<RouteListResponce>>> =
        MediatorLiveData()
    val mediatorLiveDataRouteError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataProperterdamageList: MediatorLiveData<Event<ArrayList<PropertyDamageResponce>>> =
        MediatorLiveData()
    val mediatorLiveDataProperterdamageError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataFirstResponceList: MediatorLiveData<Event<ArrayList<FirstResponce>>> =
        MediatorLiveData()
    val mediatorLiveDataFirstResponceError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataStateList: MediatorLiveData<Event<ArrayList<StateList>>> =
        MediatorLiveData()
    val mediatorLiveDataStateListError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataLaneList: MediatorLiveData<Event<ArrayList<LaneList>>> =
        MediatorLiveData()
    val mediatorLiveDataLaneListError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataVehicelTypeList: MediatorLiveData<Event<ArrayList<VehicleTypeResponce>>> =
        MediatorLiveData()
    val mediatorLiveDataVehicelTypeError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataMotoristTypeList: MediatorLiveData<Event<ArrayList<VehicleMotoristModel>>> =
        MediatorLiveData()
    val mediatorLiveDataMotoristTypeError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataRoadSurfaceTypeList: MediatorLiveData<Event<ArrayList<RoadSurface>>> =
        MediatorLiveData()
    val mediatorLiveDataRoadSurfaceTypeError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataMotoristVehiclesList: MediatorLiveData<Event<ArrayList<MotoristVehicleModel>>> =
        MediatorLiveData()
    val mediatorLiveDataMotoristVehiclesListError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    val mediatorLiveDataIncidentTypeList: MediatorLiveData<Event<ArrayList<IncidentTypeResponce>>> =
        MediatorLiveData()
    val mediatorLiveDataIncidentTypeError: MediatorLiveData<Event<Result>> = MediatorLiveData()


    val mediatorLiveDataAssectTypeList: MediatorLiveData<Event<ArrayList<AssectTypeResponce>>> =
        MediatorLiveData()
    val mediatorLiveDatAssectTypeError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataCallList: MediatorLiveData<Event<ArrayList<CallAtTimeSpinnerData>>> =
        MediatorLiveData()
    val mediatorLiveDatCallError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataVendors: MediatorLiveData<Event<ArrayList<Vendor>>> =
        MediatorLiveData()
    val mediatorLiveDataVendorsError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataContacts: MediatorLiveData<Event<ArrayList<Contract>>> =
        MediatorLiveData()
    val mediatorLiveDataContactsError: MediatorLiveData<Event<Result>> = MediatorLiveData()

    val mediatorLiveDataIncidentFieldsList: MediatorLiveData<Event<ArrayList<IncidentFieldItem>>> =
        MediatorLiveData()
    val mediatorLiveDataIncidentFieldsListError: MediatorLiveData<Event<Result>> =
        MediatorLiveData()

    init {
        mediatorLiveDataColorList.addSource(
            repository.mutableLiveDataColorList
        ) { t -> mediatorLiveDataColorList.postValue(t) }
        mediatorLiveDataColorError.addSource(
            repository.mutableLiveDataColorError
        ) { t -> mediatorLiveDataColorError.postValue(t) }


        mediatorLiveDataTrafficeDirectionList.addSource(
            repository.mutableLiveDataTrafficeDriectionList
        ) { t -> mediatorLiveDataTrafficeDirectionList.postValue(t) }
        mediatorLiveDataTraficeDirectionError.addSource(
            repository.mutableLiveDataTrafficeDirectionError
        ) { t -> mediatorLiveDataTraficeDirectionError.postValue(t) }


        mediatorLiveDataSecendaryCrashList.addSource(
            repository.mutableLiveDataSecondaryCrashList
        ) { t -> mediatorLiveDataSecendaryCrashList.postValue(t) }
        mediatorLiveDataScondaryError.addSource(
            repository.mutableLiveDataSecondaryError
        ) { t -> mediatorLiveDataScondaryError.postValue(t) }

        mediatorLiveDataRouteList.addSource(
            repository.mutableLiveDataRouteList
        ) { t -> mediatorLiveDataRouteList.postValue(t) }
        mediatorLiveDataRouteError.addSource(
            repository.mutableLiveDataRouteListError
        ) { t -> mediatorLiveDataRouteError.postValue(t) }


        mediatorLiveDataProperterdamageList.addSource(
            repository.mutableLiveDataPropertyDamagerList
        ) { t -> mediatorLiveDataProperterdamageList.postValue(t) }
        mediatorLiveDataProperterdamageError.addSource(
            repository.mutableLiveDataPopertyDamageError
        ) { t -> mediatorLiveDataProperterdamageError.postValue(t) }

        mediatorLiveDataFirstResponceList.addSource(
            repository.mutableLiveDataFirstResponceList
        ) { t -> mediatorLiveDataFirstResponceList.postValue(t) }
        mediatorLiveDataFirstResponceError.addSource(
            repository.mutableLiveDataFuelError
        ) { t -> mediatorLiveDataFirstResponceError.postValue(t) }


        mediatorLiveDataStateList.addSource(
            repository.mutableLiveDataStateList
        ) { t -> mediatorLiveDataStateList.postValue(t) }
        mediatorLiveDataStateListError.addSource(
            repository.mutableLiveDataStateListError
        ) { t -> mediatorLiveDataStateListError.postValue(t) }

        mediatorLiveDataLaneList.addSource(
            repository.mutableLiveDataLaneList
        ) { t -> mediatorLiveDataLaneList.postValue(t) }
        mediatorLiveDataLaneListError.addSource(
            repository.mutableLiveDataLaneListError
        ) { t -> mediatorLiveDataLaneListError.postValue(t) }


        mediatorLiveDataVehicelTypeList.addSource(
            repository.mutableLiveVehicleTypeList
        ) { t -> mediatorLiveDataVehicelTypeList.postValue(t) }
        mediatorLiveDataVehicelTypeError.addSource(
            repository.mutableLiveVehicelTypeError
        ) { t -> mediatorLiveDataVehicelTypeError.postValue(t) }


        mediatorLiveDataMotoristTypeList.addSource(
            repository.mutableLiveDataMotoristType
        ) { t -> mediatorLiveDataMotoristTypeList.postValue(t) }
        mediatorLiveDataMotoristTypeError.addSource(
            repository.mutableLiveDataMotoristTypeError
        ) { t -> mediatorLiveDataMotoristTypeError.postValue(t) }


        mediatorLiveDataRoadSurfaceTypeList.addSource(
            repository.mutableLiveDataRoadSurfaces
        ) { t -> mediatorLiveDataRoadSurfaceTypeList.postValue(t) }
        mediatorLiveDataRoadSurfaceTypeError.addSource(
            repository.mutableLiveDataRoadSurfacesError
        ) { t -> mediatorLiveDataRoadSurfaceTypeError.postValue(t) }


        mediatorLiveDataMotoristVehiclesList.addSource(
            repository.mutableLiveDataMotoristVehiclesList
        ) { t -> mediatorLiveDataMotoristVehiclesList.postValue(t) }
        mediatorLiveDataMotoristVehiclesListError.addSource(
            repository.mutableLiveDataMotoristVehiclesListError
        ) { t -> mediatorLiveDataMotoristVehiclesListError.postValue(t) }


        mediatorLiveDataIncidentTypeList.addSource(
            repository.mutableLiveIncidentTypeList
        ) { t -> mediatorLiveDataIncidentTypeList.postValue(t) }
        mediatorLiveDataIncidentTypeError.addSource(
            repository.mutableLiveIncidentTypeError
        ) { t -> mediatorLiveDataIncidentTypeError.postValue(t) }


        mediatorLiveDataAssectTypeList.addSource(
            repository.mutableLiveAssectList
        ) { t -> mediatorLiveDataAssectTypeList.postValue(t) }
        mediatorLiveDatAssectTypeError.addSource(
            repository.mutableLiveAssectListError
        ) { t -> mediatorLiveDatAssectTypeError.postValue(t) }


        mediatorLiveDataCallList.addSource(
            repository.mutableLiveDataCallList
        ) { t -> mediatorLiveDataCallList.postValue(t) }
        mediatorLiveDataColorError.addSource(
            repository.mutableLiveDataCallError
        ) { t -> mediatorLiveDatAssectTypeError.postValue(t) }


        mediatorLiveDataVendors.addSource(
            repository.mutableLiveDataVendorsForIncidents
        ) { t -> mediatorLiveDataVendors.postValue(t) }
        mediatorLiveDataVendorsError.addSource(
            repository.mutableLiveDataVendorsForIncidentsError
        ) { t -> mediatorLiveDataVendorsError.postValue(t) }


        mediatorLiveDataContacts.addSource(
            repository.mutableLiveDataContactList
        ) { t -> mediatorLiveDataContacts.postValue(t) }
        mediatorLiveDataContactsError.addSource(
            repository.mutableLiveDataContactListError
        ) { t -> mediatorLiveDataContactsError.postValue(t) }


        mediatorLiveDataIncidentFieldsList.addSource(
            repository.mutableLiveDataIncidentFieldsList
        ) { t -> mediatorLiveDataIncidentFieldsList.postValue(t) }
        mediatorLiveDataIncidentFieldsListError.addSource(
            repository.mutableLiveDataIncidentFieldsListError
        ) { t -> mediatorLiveDataIncidentFieldsListError.postValue(t) }
    }

    fun getColorList() = repository.getColorList()
    fun getTrafficeDirectionList() = repository.getTraficeDirectionList()
    fun getSecendaryCrashList() = repository.getSecendaryCrashList()
    fun getRouteList() = repository.getRouteList()
    fun getProperterDamageList() = repository.getPropertyDamageList()
    fun getFirstList() = repository.getFirstResponceList()
    fun getCallList() = repository.pushCallSpinnerData()
    fun getStateList() = repository.getStateList()
    fun getLaneList() = repository.getLaneList()
    fun getMaintenanceRequestTypeList() = repository.getLaneList()
    fun getVehicelTypeList() = repository.getVehicelTypeList()
    fun getIncidentTypeList() = repository.getIncidentTypeList()
    fun getAssectList() = repository.getAssistList()
    fun getMotoristType() = repository.getMotoristTypesList()
    fun getRoadSurfaceTypes() = repository.getRoadSurfaceTypesList()
    fun getMotoristVehicles() = repository.getMotoristVehiclesList()
    fun getVendorList() = repository.getVendorsListForIncidents()
    fun getContactList() = repository.getContactsList()
    fun getIncidentsFieldsList(incidentTypeId: String) = repository.getIncidentFields(incidentTypeId)

}