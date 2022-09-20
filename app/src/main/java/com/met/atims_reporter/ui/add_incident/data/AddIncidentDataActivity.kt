package com.met.atims_reporter.ui.add_incident.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.met.atims_reporter.R
import com.met.atims_reporter.application.ApplicationClass
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityAddIncidentStepOneBinding
import com.met.atims_reporter.enums.OperationMode
import com.met.atims_reporter.model.*
import com.met.atims_reporter.ui.add_incident.data.adapter.LaneLocationListAdapter
import com.met.atims_reporter.ui.add_incident.data.adapter.RoadSurfaceListAdapter
import com.met.atims_reporter.ui.add_incident.data.adapter.VehicleInformationListAdapter
import com.met.atims_reporter.ui.add_incident.media.AddIncidentMediaActivity
import com.met.atims_reporter.ui.incidents.Incidents
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import com.met.atims_reporter.widget.edittext.EditTextInputMode
import com.met.atims_reporter.widget.edittext.MultiLineConfig
import com.met.atims_reporter.widget.spinner.Spinner
import com.met.atims_reporter.widget.spinner.SpinnerData
import com.sagar.android.logutilmaster.LogUtil
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import permissions.dispatcher.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import java.lang.StringBuilder


@RuntimePermissions
class AddIncidentDataActivity : AtimsSuperActivity(), KodeinAware {

    companion object {
        const val DIRECTION = "DIRECTION"
        const val DESCRIPTION = "DESCRIPTION"
        const val INCIDENT_TYPE = "INCIDENT_TYPE"
    }

    private lateinit var binding: ActivityAddIncidentStepOneBinding
    override val kodein: Kodein by kodein()
    private val logUtil: LogUtil by instance()
    private lateinit var viewModel: ViewModel
    private val viewModelProvider: ViewModelProvider by instance()
    private lateinit var colorresponce: ColorResponce
    private lateinit var traficeDirectionListResponce: TraficeDirectionListResponce
    private var firstResponce = "No"
    private lateinit var SecondaryCrashResponce: SecondaryCrashResponce
    private lateinit var propertyDamageResponce: PropertyDamageResponce
    private lateinit var routeListResponce: RouteListResponce
    private lateinit var stateList: StateList
    private lateinit var laneList: LaneList
    private lateinit var vehicleTypeResponce: VehicleTypeResponce
    private lateinit var incidentTypeResponce: IncidentTypeResponce
    private lateinit var assectTypeResponce: AssectTypeResponce
    private var selectedDirection: String = "One Direction"
    private var selectedDescription: String = "Motor Vehicle Accident"
    private lateinit var callAtTimeSpinnerData: CallAtTimeSpinnerData
    private lateinit var callStartTimeSpinnerData: CallAtTimeSpinnerData
    private lateinit var callCompleteTimeSpinnerData: CallAtTimeSpinnerData
    private lateinit var incidentTimeTimeSpinnerData: CallAtTimeSpinnerData
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var laneLocationListAdapter: LaneLocationListAdapter
    private lateinit var roadSurfaceListAdapter: RoadSurfaceListAdapter
    private var selectedPassengerTransported = ""
    private var selectedNumberOfVehicle = ""
    private lateinit var colorList: ArrayList<ColorResponce>
    private lateinit var vehicelTypeList: ArrayList<VehicleTypeResponce>
    private lateinit var motoristTypeList: ArrayList<VehicleMotoristModel>
    private lateinit var vehicleInformationListAdapter: VehicleInformationListAdapter
    private lateinit var selectedMotoristVehicle: MotoristVehicleModel
    private lateinit var selectedVendor: Vendor
    private lateinit var selectedContact: Contract
    private var selectedRamplanes:String?=null
    private val fields: ArrayList<IncidentFieldItem> = ArrayList()
    private var incidentType = ""
    private var selectedTrafficDirection: TraficeDirectionListResponce?=null
    private var loadCallCompleteFlag:Boolean=false
    private var mSavedInstanceState: Bundle?=null
    private var travelLanesBlocked: String? = "Yes"
    private var lineRestorationTime: String? = ""
    private var incidentNoPrefixStr: String? = ""
    private var operationModeStr: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_add_incident_step_one)
        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)
        mSavedInstanceState = savedInstanceState
        setPageTitle("ADD INCIDENT")
        enableBackButton()
        willHandleBackNavigation()
        initView()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.v("TAG","onRestoreInstanceState")
    }

    override fun goBack() {
        viewModel.giveRepository().getHomeGridItems()
        startActivity(Intent(this, Incidents::class.java))
        finish()
    }

    private fun getFields() {
        showProgress()
        intent.getSerializableExtra(
            KeyWordsAndConstants.OPERATION_MODE
        )?.let { mode ->
            if (mode == OperationMode.EDIT) {
                operationModeStr = "EDIT"
                val incidentDetails: IncidentDetails =
                    fromJson(intent.getStringExtra(DATA)!!)
                incidentType = incidentDetails.incedent_type_id ?: ""
            } else if (mode == OperationMode.ADD) {
                operationModeStr = "ADD"
                intent.getStringExtra(INCIDENT_TYPE)?.let { incidentTypeFromIntent ->
                    incidentType = incidentTypeFromIntent
                }
            }
            if (incidentType != ""){
                operationModeStr = "ADD"
                viewModel.getIncidentsFieldsList(incidentType)
            }
        }
    }

    private fun getRestOfData() {
        getColorSpinnerData()
        getTrafficeDirctionData()
        getSecondaryCrashData()
        getPropertyDamage()
        getRouteList()
        getStateList()
        getLaneList()
        getVeihicelTypeList()
        //getIncidentType()
        getAssectType()
        getCallList()
        getMotoristTypes()
        getRoadSurfacesTypes()
        getMotoristVehiclesList()
        getVendorList()
        getContractList()
    }

    private fun initView() {
        bindToViewModel()
        getIncidentType()
        getRestOfData()//getFields()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationPermissionWithPermissionCheck()

        setSpinnerData()
        tryPreFillingData()
    }

    private fun pickDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            TimePickerDialog(this, OnTimeSetListener { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)
                //doSomethingWith(pickedDateTime)
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }

    private fun bindToViewModel() {

        viewModel.mediatorLiveDataColorList.observe(
            this,
            Observer<Event<ArrayList<ColorResponce>>> { t ->
                hideProgress()
                if (t.shouldReadContent())
                    loadSpinnerColorData(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDataColorError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataTrafficeDirectionList.observe(
            this,
            Observer<Event<ArrayList<TraficeDirectionListResponce>>> { t ->
                hideProgress()
                if (t.shouldReadContent())
                    loadSpinnerTrafficeData(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDataTraficeDirectionError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataFirstResponceList.observe(
            this,
            Observer<Event<ArrayList<FirstResponce>>> { t ->
                hideProgress()
                if (t.shouldReadContent()) {
                }
                //loadSpinneFristResponderData(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDataFirstResponceError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataSecendaryCrashList.observe(
            this,
            Observer<Event<ArrayList<SecondaryCrashResponce>>> { t ->
                hideProgress()
                if (t.shouldReadContent())
                    loadSpinneScendaryCrashData(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDataScondaryError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataProperterdamageList.observe(
            this,
            Observer<Event<ArrayList<PropertyDamageResponce>>> { t ->
                hideProgress()
                if (t.shouldReadContent())
                    loadSpinnePrpertyDamageData(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDataProperterdamageError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataRouteList.observe(
            this,
            Observer<Event<ArrayList<RouteListResponce>>> { t ->
                hideProgress()
                if (t.shouldReadContent())
                    loadSpinneRoutListData(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDataRouteError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataStateList.observe(
            this,
            Observer<Event<ArrayList<StateList>>> { t ->
                hideProgress()
                if (t.shouldReadContent())
                    loadSpinnerStateListData(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDataStateListError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataLaneList.observe(
            this,
            Observer<Event<ArrayList<LaneList>>> { t ->
                hideProgress()
                if (t.shouldReadContent())
                    loadSpinnerLaneListData(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDataLaneListError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataVehicelTypeList.observe(
            this,
            Observer<Event<ArrayList<VehicleTypeResponce>>> { t ->
                hideProgress()
                if (t.shouldReadContent())
                    loadSpinnerVehicelTypeListData(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDataVehicelTypeError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataIncidentTypeList.observe(
            this,
            Observer<Event<ArrayList<IncidentTypeResponce>>> { t ->
                hideProgress()
                if (t.shouldReadContent())
                    loadSpinnerIncidentTypeListData(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDataIncidentTypeError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )


        viewModel.mediatorLiveDataAssectTypeList.observe(
            this,
            Observer<Event<ArrayList<AssectTypeResponce>>> { t ->
                hideProgress()
                if (t.shouldReadContent())
                    loadSpinnerAssectTypeListData(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDatAssectTypeError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataCallList.observe(
            this,
            Observer<Event<ArrayList<CallAtTimeSpinnerData>>> { t ->
                hideProgress()
                if (t.shouldReadContent())
                    loadSpinnerCallAtListData(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDatCallError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataMotoristTypeList.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        this.motoristTypeList = it.getContent()!!
                        initialiseVehicleInformationList()
                    }
                }
            }
        )
        viewModel.mediatorLiveDataMotoristTypeError.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )

        viewModel.mediatorLiveDataRoadSurfaceTypeList.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        if(!this@AddIncidentDataActivity::roadSurfaceListAdapter.isInitialized) {
                            binding.recyclerViewRoadSurface.apply {
                                layoutManager = LinearLayoutManager(this@AddIncidentDataActivity)
                                this@AddIncidentDataActivity.roadSurfaceListAdapter =
                                    RoadSurfaceListAdapter(it.getContent()!!)
                                adapter = this@AddIncidentDataActivity.roadSurfaceListAdapter
                                isNestedScrollingEnabled = false
                            }
                        }else{
                            binding.recyclerViewRoadSurface.apply {
                                layoutManager = LinearLayoutManager(this@AddIncidentDataActivity)
                                adapter = this@AddIncidentDataActivity.roadSurfaceListAdapter
                                isNestedScrollingEnabled = false
                            }
                        }
                    }
                }
            }
        )
        viewModel.mediatorLiveDataRoadSurfaceTypeError.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )

        viewModel.mediatorLiveDataMotoristVehiclesList.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        val toAdd: ArrayList<SpinnerData<MotoristVehicleModel>> = ArrayList()
                        it.getContent()!!.forEach { item ->
                            toAdd.add(
                                SpinnerData(
                                    item.ID,
                                    item
                                )
                            )
                        }
                        if (toAdd.size == 0)
                            return@Observer
                        selectedMotoristVehicle = toAdd[0].data
                    }
                }
            }
        )

        viewModel.mediatorLiveDataMotoristVehiclesListError.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )

        viewModel.mediatorLiveDataVendors.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        val toAdd: ArrayList<SpinnerData<Vendor>> = ArrayList()
                        it.getContent()?.forEach { item ->
                            toAdd.add(
                                SpinnerData(
                                    item.vendor_name,
                                    item
                                )
                            )
                        }
                        if (toAdd.size == 0)
                            return@Observer
                        selectedVendor = toAdd[0].data
                        binding.spinnerVendor.apply {
                            spinnerWidthPercent(60)
                            heading("Vendor")
                            addItems(
                                toAdd,
                                object : Spinner.OnItemSelectedListener {
                                    override fun <T> selected(item: SpinnerData<T>) {
                                        selectedVendor = item.data as Vendor
                                    }
                                }
                            )
                        }
                    }
                }
            }
        )
        viewModel.mediatorLiveDataVendorsError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataContacts.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        val toAdd: ArrayList<SpinnerData<Contract>> = ArrayList()
                        it.getContent()?.forEach { item ->
                            toAdd.add(
                                SpinnerData(
                                    item.contract_name,
                                    item
                                )
                            )
                        }
                        if (toAdd.size == 0)
                            return@Observer
                        selectedContact = toAdd[0].data
                        binding.spinnerContacts.apply {
                            heading("Contact")
                            spinnerWidthPercent(60)
                            addItems(
                                toAdd,
                                object : Spinner.OnItemSelectedListener {
                                    override fun <T> selected(item: SpinnerData<T>) {
                                        selectedContact = item.data as Contract
                                    }
                                }
                            )
                        }
                    }
                }
            }
        )
        viewModel.mediatorLiveDataContactsError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataIncidentFieldsList.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        fields.clear()
                        fields.addAll(it.getContent()!!)

                        var toShow = 0
                        fields.forEach { field ->
                            if (field.show == "1")
                                toShow++
                        }
                        if (toShow == 0) {
                            showMessageWithOneButton(
                                message = "There are no fields available to add incident. Please contact admin.",
                                callback = object : DialogUtil.CallBack {

                                    override fun buttonClicked() {
                                        super.buttonClicked()

                                        finish()
                                    }
                                }
                            )
                            return@Observer
                        }

                        var photoReq = false
                        var videoReq = false
                        var noteReq = false
                        binding.llIncidentStepOne.visibility = View.VISIBLE
                        fields.forEach { field ->
                            when (field.id) {
                                "1" -> {
                                    binding.editTextLatitude.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "2" -> {
                                    binding.editTextLongitude.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "3" -> {
                                    binding.spinnerCallAt.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "4" -> {
                                    binding.spinnerCallStarted.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "5" -> {
                                    binding.spinnerCallCompleted.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "6" -> {
                                    binding.spinnerIncidentTime.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "7" -> {
                                    binding.spinnerIncidentType.visibility = View.VISIBLE
                                        //if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "8" -> {
                                    binding.spinnerRoute.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "9" -> {
                                    binding.editTextMileMarker.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "10" -> {
                                    binding.spinnerState.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "11" -> {
                                    binding.spinnerPropertyDamage.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "12" -> {
                                    binding.spinnerSecondaryCrashInvolved.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "13" -> {
                                    binding.spinnerFirstResponder.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "14" -> {
                                    binding.editTextFirstResponderUnitNumber.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "15" -> {
                                    binding.linearLayoutRoadSurfaceHeading.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                    binding.recyclerViewRoadSurface.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "16" -> {
                                    binding.linearLayoutLaneLocationHeading.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                    binding.recyclerViewLaneLocation.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "17" -> {
                                    binding.spinnerPassengerTransported.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "18" -> {
                                    binding.spinnerAssistType.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "19" -> {
                                    binding.spinnerDirection.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "20" -> {
                                    binding.spinnerContacts.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "21" -> {
                                    binding.spinnerVendor.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "22" -> {
                                    binding.spinnerNumberOfVehicle.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                    binding.recyclerViewVehicleInformation.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "23" -> {
                                    binding.editTextComment.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                    //binding.editTextComment.visibility = View.GONE
                                }
                                "24" -> {
                                    photoReq = field.show == "1"
                                }
                                "25" -> {
                                    videoReq = field.show == "1"
                                }
                                "26" -> {
                                    noteReq = field.show == "1"
                                }
                                "27"->{
                                    binding.spinnerRamplanes.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "28"->{
                                    binding.spinnerTravelLanesBlocked.visibility =
                                        if (field.show == "1") View.VISIBLE else View.GONE
                                }
                                "29"->{
                                        if (field.show == "1"){
                                            binding.editTextLaneRestorationTime.visibility =View.VISIBLE
                                            if (operationModeStr == "ADD"){
                                                defaultDateTime()
                                            }
                                        } else {
                                            binding.editTextLaneRestorationTime.visibility =View.GONE
                                        }
                                }
                                "30"->{
                                    if (field.show == "1"){
                                        binding.incidentNoLL.visibility = View.VISIBLE
                                        if (operationModeStr == "ADD"){
                                            incidentNoPrefixStr = field.incident_no_prefix
                                            binding.incidentNoPrefixTv.text = incidentNoPrefixStr
                                        }
                                    } else{
                                        binding.incidentNoLL.visibility =View.GONE
                                    }
                                }
                            }
                        }

                        if (!photoReq && !videoReq && !noteReq)
                            binding.btnNext.text = "Submit"

                        //getRestOfData()
                    }
                }
            }
        )

        viewModel.mediatorLiveDataIncidentFieldsListError.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )
    }

    private fun getColorSpinnerData() {
        showProgress()
        viewModel.getColorList()
    }

    private fun getTrafficeDirctionData() {
        showProgress()
        viewModel.getTrafficeDirectionList()
    }

    private fun getFirstResponce() {
        showProgress()
        viewModel.getFirstList()
    }

    private fun getSecondaryCrashData() {
        showProgress()
        viewModel.getSecendaryCrashList()
    }

    private fun getPropertyDamage() {
        showProgress()
        viewModel.getProperterDamageList()
    }

    private fun getRouteList() {
        showProgress()
        viewModel.getRouteList()
    }

    private fun getStateList() {
        showProgress()
        viewModel.getStateList()
    }

    private fun getLaneList() {
        showProgress()
        viewModel.getLaneList()
    }

    private fun getVeihicelTypeList() {
        showProgress()
        viewModel.getVehicelTypeList()
    }

    private fun getIncidentType() {
        showProgress()
        viewModel.getIncidentTypeList()
    }

    private fun getAssectType() {
        showProgress()
        viewModel.getAssectList()
    }

    private fun getCallList() {
        showProgress()
        viewModel.getCallList()
    }

    private fun getMotoristTypes() {
        showProgress()
        viewModel.getMotoristType()
    }

    private fun getRoadSurfacesTypes() {
        showProgress()
        viewModel.getRoadSurfaceTypes()
    }

    private fun getMotoristVehiclesList() {
        showProgress()
        viewModel.getMotoristVehicles()
    }

    private fun getVendorList() {
        showProgress()
        viewModel.getVendorList()
    }

    private fun getContractList() {
        showProgress()
        viewModel.getContactList()
    }


    private fun setSpinnerData() {
        binding.editTextLatitude.apply {
            heading("Latitude")
            editTextWidthPercent(60)
            editable(false)
            isEnabled = false
        }
        binding.editTextLongitude.apply {
            heading("Longitude")
            editTextWidthPercent(60)
            editable(false)
            isEnabled = false
        }


        binding.spinnerIncidentTime.apply {
            heading("Incident Time")
            spinnerWidthPercent(60)
        }

        binding.editTextMileMarker.apply {
            heading("Mile Marker")
            editTextWidthPercent(60)
            inputMode(
                EditTextInputMode.INPUT_TEXT_MULTI_LINE,
                MultiLineConfig(1, 3)
            )
        }

        binding.recyclerViewLaneLocation.apply {
            layoutManager = LinearLayoutManager(this@AddIncidentDataActivity)
            this@AddIncidentDataActivity.laneLocationListAdapter = LaneLocationListAdapter()
            adapter = this@AddIncidentDataActivity.laneLocationListAdapter
            isNestedScrollingEnabled = false
        }
        binding.spinnerPassengerTransported.apply {
            heading("Passengers Transported")
            spinnerWidthPercent(60)
            addItems(
                arrayListOf(
                    SpinnerData(
                        "Not Applicable",
                        "Not Applicable"
                    ),
                    SpinnerData(
                        "1",
                        "1"
                    ),
                    SpinnerData(
                        "2",
                        "2"
                    ),
                    SpinnerData(
                        "3",
                        "3"
                    ),
                    SpinnerData(
                        "4",
                        "4"
                    )
                ),
                object : Spinner.OnItemSelectedListener {
                    override fun <T> selected(item: SpinnerData<T>) {
                        selectedPassengerTransported = item.data as String
                    }
                }
            )
        }

        binding.spinnerFirstResponder.apply {
            heading("First Responder")
            spinnerWidthPercent(60)
            addItems(
                arrayListOf(
                    SpinnerData(
                        "No",
                        "No"
                    ),
                    SpinnerData(
                        "Yes",
                        "Yes"
                    )

                ),
                object : Spinner.OnItemSelectedListener {
                    override fun <T> selected(item: SpinnerData<T>) {
                        firstResponce = item.data as String
                        if (
                            firstResponce == "Yes"
                        ) {
                            binding.editTextFirstResponderUnitNumber.visibility = View.VISIBLE
                        } else {
                            binding.editTextFirstResponderUnitNumber.visibility = View.GONE
                            binding.editTextFirstResponderUnitNumber.setText("")
                        }
                    }
                }
            )
        }

        binding.editTextFirstResponderUnitNumber.heading("First Responder Information")
        binding.editTextFirstResponderUnitNumber.editTextWidthPercent(60)

        binding.spinnerNumberOfVehicle.apply {
            heading("Number of Vehicles involved in Incident")
            spinnerWidthPercent(60)
            val list: ArrayList<SpinnerData<String>> = ArrayList()
            for (i in 0..10) {
                list.add(
                    SpinnerData(
                        i.toString(),
                        i.toString()
                    )
                )
            }
            addItems(
                list,
                object : Spinner.OnItemSelectedListener {
                    override fun <T> selected(item: SpinnerData<T>) {
                        selectedNumberOfVehicle = item.data as String
                        configureVehicleInformationCount()
                    }
                }
            )
        }
        /*binding.spinnerRamplanes.apply {
            //heading("Ramp lanes")
            heading("Lane")
            spinnerWidthPercent(60)
            addItems(
                arrayListOf(
                    SpinnerData(
                        "Exit Ramp",
                        "Exit Ramp"
                    ),
                    SpinnerData(
                        "On Ramp",
                        "On Ramp"
                    )
                ),
                object : Spinner.OnItemSelectedListener {
                    override fun <T> selected(item: SpinnerData<T>) {
                        selectedRamplanes = item.data as String

                    }
                }
            )
        }*/


        binding.editTextComment.apply {
            heading("Comment")
            inputMode(
                EditTextInputMode.INPUT_TEXT_MULTI_LINE,
                MultiLineConfig(2, 4)
            )
        }

        binding.spinnerTravelLanesBlocked.apply {
            heading("Travel Lanes Blocked")
            spinnerWidthPercent(60)
            addItems(
                arrayListOf(
                    SpinnerData(
                        "Yes",
                        "Yes"
                    ),
                    SpinnerData(
                        "No",
                        "No"
                    )
                ),
                object : Spinner.OnItemSelectedListener {
                    override fun <T> selected(item: SpinnerData<T>) {
                        travelLanesBlocked = if (item.data as String == "Yes")
                            "Yes"
                        else
                            "No"
                    }
                }
            )
        }

        binding.editTextLaneRestorationTime.apply {
            heading("Lane restoration time")
            //editable(false)
            editTextWidthPercent(60)
            inputMode(
                EditTextInputMode.DATE_TIME
            )
            /*inputMode(
                EditTextInputMode.INPUT_TEXT_MULTI_LINE,
                MultiLineConfig(1, 3)
            )*/
        }

       /* binding.editTextIncidentNo.apply {
            heading("Incident No")
            //editable(false)
            editTextWidthPercent(60)
            inputMode(
                EditTextInputMode.INPUT_TEXT_MULTI_LINE,
                MultiLineConfig(1, 3)
            )
        }*/

        binding.editTextLatitude.apply {
            heading("Latitude")
            editable(false)
        }


        binding.btnNext.setOnClickListener {
            moveToNextPage()
        }
    }

    private fun validate(): Boolean {

        /*if (
            binding.editTextMileMarker.getText().isEmpty() &&
            binding.editTextMileMarker.visibility == View.VISIBLE
        ) {
            showMessageInDialog("Please provide Mile Marker.")
            return false
        }

        if (
            binding.spinnerFirstResponder.visibility == View.VISIBLE &&
            firstResponce == "Yes" &&
            binding.editTextFirstResponderUnitNumber.getText().isEmpty() &&
            binding.editTextFirstResponderUnitNumber.visibility == View.VISIBLE
        ) {
            showMessageInDialog("Please provide First Responder Information.")
            return false
        }


        if (
            this.roadSurfaceListAdapter.getSelected() == "" &&
            binding.linearLayoutRoadSurfaceHeading.visibility == View.VISIBLE
        ) {
            showMessageInDialog("Please provide Road Surface.")
            return false
        }

        if (
            laneLocationListAdapter.getSelected() == "" &&
            binding.linearLayoutLaneLocationHeading.visibility == View.VISIBLE
        ) {
            showMessageInDialog("Please provide Lane Locations.")
            return false
        }

        if (
            selectedPassengerTransported == "" &&
            binding.spinnerPassengerTransported.visibility == View.VISIBLE
        ) {
            showMessageInDialog("Please provide Passenger Transport.")
            return false
        }


        if (
            selectedNumberOfVehicle == "" &&
            binding.spinnerNumberOfVehicle.visibility == View.VISIBLE
        ) {
            showMessageInDialog("Please provide Number of Vehicle.")
            return false
        }*/

        if(binding.spinnerNumberOfVehicle.visibility == View.VISIBLE &&
            selectedNumberOfVehicle!="0" &&
            !vehicleInformationListAdapter.isValid()){
            showMessageInDialog("Please provide License Plate.")
            return false
        }

        if (
            binding.editTextComment.getText().isEmpty() &&
            binding.editTextComment.visibility == View.VISIBLE
        ) {
            showMessageInDialog("Please provide Commment.")
            return false
        }


        return true
    }

    private fun moveToNextPage() {
        if (!validate())
            return


        var details: IncidentDetails? = null
        intent.getStringExtra(DATA)?.let {
            details = fromJson(it)
        }

        var status = details?.incident_status
        try {
            if (details == null) {
                status = "On Scene"
            } else if (details?.status != "") {
                status = details?.status
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        val jsonToSend = toJson(
            AddIncidentRequest(
                id = details?.Incidents_report_data_id,
                latitude = binding.editTextLatitude.getText(),
                longitude = binding.editTextLongitude.getText(),
                callAt = if (this::callAtTimeSpinnerData.isInitialized) callAtTimeSpinnerData.data else "",
                callStarted = if (this::callStartTimeSpinnerData.isInitialized) callStartTimeSpinnerData.data else "",
                callComplete = if (this::callCompleteTimeSpinnerData.isInitialized) callCompleteTimeSpinnerData.data else "",
                incidentTime = if (this::incidentTimeTimeSpinnerData.isInitialized) incidentTimeTimeSpinnerData.data else "",
                incidentType = if (incidentType != "") incidentType else incidentTypeResponce.incident_id,
                roadSurver = if (binding.linearLayoutRoadSurfaceHeading.visibility == View.VISIBLE) this.roadSurfaceListAdapter.getSelected() else "",
                companyRoute = if (this::routeListResponce.isInitialized) routeListResponce.route_id else "",
                trafficDirection = if (binding.spinnerDirection.visibility == View.VISIBLE) selectedTrafficDirection?.traffic_direction_id?:"" else "",
                mileMaker = binding.editTextMileMarker.getText(),
                propertyDamage = if (binding.spinnerPropertyDamage.visibility == View.VISIBLE) propertyDamageResponce.property_damage_id else "",
                firstResponder = if (binding.spinnerFirstResponder.visibility == View.VISIBLE) firstResponce else "",
                firstResponderUnit = binding.editTextFirstResponderUnitNumber.getText(),
                laneLocation = if (binding.linearLayoutLaneLocationHeading.visibility == View.VISIBLE) this.laneLocationListAdapter.getSelected() else "",
                personTransported = if (binding.spinnerPassengerTransported.visibility == View.VISIBLE) selectedPassengerTransported else "",
                vehicleType = "",
                assistType = if (binding.spinnerAssistType.visibility == View.VISIBLE) assectTypeResponce.assist_id else "",
                userId = "",
                companyId = "",
                note = details?.note?:"",
                comments = binding.editTextComment.getText(),
                source = "MOB",
                actionStatus = "",
                plate_no = "",
                crashInvolced = if (binding.spinnerSecondaryCrashInvolved.isShown && this::SecondaryCrashResponce.isInitialized) SecondaryCrashResponce.crash_Inv else "",
                incedent_photo = null,
                incedent_video = null,
                shift_id = "",
                incident_status = "On Scene",
                direction = selectedDirection,
                description = selectedDescription,
                vehicleQty = if (binding.spinnerNumberOfVehicle.visibility == View.VISIBLE) selectedNumberOfVehicle else "",
                vehicleInformation = if (binding.spinnerNumberOfVehicle.visibility == View.VISIBLE) vehicleInformationListAdapter.getSelected() else ArrayList(),
                vehicle_id = if (this::selectedMotoristVehicle.isInitialized) selectedMotoristVehicle.vehicle_id else "",
                vendor = if (binding.spinnerVendor.isShown && this::selectedVendor.isInitialized) selectedVendor else null,
                contract = if (this::selectedContact.isInitialized) selectedContact else null,
                //ramp_lane = if (binding.spinnerRamplanes.isShown && selectedRamplanes!=null) selectedRamplanes else null,
                ramp_lane = if (binding.spinnerRamplanes.visibility == View.VISIBLE) laneList.lane_id else "",
                travel_lanes_blocked = (if (binding.spinnerTravelLanesBlocked.isShown) travelLanesBlocked else "")!!,
                lane_restoration_time = (if (binding.editTextLaneRestorationTime.isVisible) binding.editTextLaneRestorationTime.getText() else ""),
                incident_no = (if (binding.incidentNoEt.isVisible) binding.incidentNoEt.getText().toString() else "")
            )
        )
        (this.applicationContext as ApplicationClass).mAddIncidentRequest =fromJson(jsonToSend)
        logUtil.logV(
            """
            to send json are >>
            $jsonToSend
        """.trimIndent()
        )

        startActivity(
            Intent(this, AddIncidentMediaActivity::class.java)
                .putExtra(
                    KeyWordsAndConstants.OPERATION_MODE,
                    if (
                        intent.getSerializableExtra(
                            KeyWordsAndConstants.OPERATION_MODE
                        ) == OperationMode.EDIT
                    ) {
                        OperationMode.EDIT
                    } else {
                        OperationMode.ADD
                    }
                )
                .putExtra(
                    DATA,
                    jsonToSend
                )
                .putExtra(
                    AddIncidentMediaActivity.FIELDS,
                    toJson(fields)
                )
        )
    }

    private fun loadSpinnerColorData(colorList: ArrayList<ColorResponce>) {
        this.colorList = colorList
        initialiseVehicleInformationList()
    }

    private fun loadSpinnerTrafficeData(trafficeList: ArrayList<TraficeDirectionListResponce>) {
        if (trafficeList.size > 0)
            traficeDirectionListResponce = trafficeList[0]
        val list: ArrayList<SpinnerData<TraficeDirectionListResponce>> = ArrayList()
        for (i in 0 until trafficeList.size) {
            list.add(
                SpinnerData(
                    trafficeList[i].traffic_direction, trafficeList[i]
                )
            )
        }
        binding.spinnerDirection.heading("Traffic Direction")
        binding.spinnerDirection.spinnerWidthPercent(60)
        binding.spinnerDirection.addItems(
            list,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    selectedTrafficDirection = item.data as TraficeDirectionListResponce
                }
            }
        )

        intent.getSerializableExtra(
            KeyWordsAndConstants.OPERATION_MODE
        )?.let { mode ->
            if (mode == OperationMode.EDIT) {
                val incidentDetails: IncidentDetails =
                    fromJson(intent.getStringExtra(DATA)!!)
                var traficeDirectionListResponce: TraficeDirectionListResponce? = null
                list.forEach { item ->
                    if (item.data.traffic_direction_id == incidentDetails.traffic_direction_id)
                        traficeDirectionListResponce = item.data
                }
                selectedTrafficDirection = traficeDirectionListResponce
            }
        }

    }

    /* private fun loadSpinneFristResponderData(firstResponceList: ArrayList<FirstResponce>) {
         if (firstResponceList.size > 0)
             firstResponce = firstResponceList[0]
         val list: ArrayList<SpinnerData<FirstResponce>> = ArrayList()
         for (i in 0 until firstResponceList.size) {
             list.add(
                 SpinnerData(
                     firstResponceList[i].first_responder, firstResponceList[i]
                 )
             )
         }


     }*/

    private fun loadSpinneScendaryCrashData(SecondayCrashList: ArrayList<SecondaryCrashResponce>) {
        if (SecondayCrashList.size > 0)
            SecondaryCrashResponce = SecondayCrashList[0]
        val list: ArrayList<SpinnerData<SecondaryCrashResponce>> = ArrayList()
        //for (i in SecondayCrashList.size-1 downTo 0) {
        for (i in 0 until SecondayCrashList.size) {
            list.add(
                SpinnerData(
                    SecondayCrashList[i].crash_Inv, SecondayCrashList[i]
                )
            )
        }
        binding.spinnerSecondaryCrashInvolved.heading("Secondary Crash Involved")
        binding.spinnerSecondaryCrashInvolved.spinnerWidthPercent(60)
        binding.spinnerSecondaryCrashInvolved.addItems(
            list,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    SecondaryCrashResponce = item.data as SecondaryCrashResponce
                }
            }
        )

        intent.getSerializableExtra(
            KeyWordsAndConstants.OPERATION_MODE
        )?.let { mode ->
            if (mode == OperationMode.EDIT) {
                val incidentDetails: IncidentDetails =
                    fromJson(intent.getStringExtra(DATA)!!)
                var secondaryCrashResponce: SecondaryCrashResponce? = null
                list.forEach { item ->
                    if (item.data.crash_Inv == incidentDetails.secendary_cash_involve)
                        secondaryCrashResponce = item.data
                }
                binding.spinnerSecondaryCrashInvolved.select(
                    data = secondaryCrashResponce
                )
            }
        }

    }

    private fun loadSpinnePrpertyDamageData(propertyDamageList: ArrayList<PropertyDamageResponce>) {
        if (propertyDamageList.size > 0)
            propertyDamageResponce = propertyDamageList[0]
        val list: ArrayList<SpinnerData<PropertyDamageResponce>> = ArrayList()
        for (i in 0 until propertyDamageList.size) {
            list.add(
                SpinnerData(
                    propertyDamageList[i].property_damage, propertyDamageList[i]
                )
            )
        }
        binding.spinnerPropertyDamage.heading("Property Damage")
        binding.spinnerPropertyDamage.spinnerWidthPercent(60)
        binding.spinnerPropertyDamage.addItems(
            list,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    propertyDamageResponce = item.data as PropertyDamageResponce
                }
            }
        )

        intent.getSerializableExtra(
            KeyWordsAndConstants.OPERATION_MODE
        )?.let { mode ->
            if (mode == OperationMode.EDIT) {
                val incidentDetails: IncidentDetails =
                    fromJson(intent.getStringExtra(DATA)!!)
                var propertyDamageResponce: PropertyDamageResponce? = null
                list.forEach { item ->
                    if (item.data.property_damage_id == incidentDetails.property_damage_id)
                        propertyDamageResponce = item.data
                }
                binding.spinnerPropertyDamage.select(
                    data = propertyDamageResponce
                )
            }
        }

    }

    private fun loadSpinneRoutListData(routeList: ArrayList<RouteListResponce>) {
        if (routeList.size > 0)
            routeListResponce = routeList[0]
        val list: ArrayList<SpinnerData<RouteListResponce>> = ArrayList()
        for (i in 0 until routeList.size) {
            list.add(
                SpinnerData(
                    routeList[i].route_name, routeList[i]
                )
            )
        }
        binding.spinnerRoute.heading("Route")
        binding.spinnerRoute.spinnerWidthPercent(60)
        binding.spinnerRoute.addItems(
            list,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    routeListResponce = item.data as RouteListResponce
                }
            }
        )


        intent.getSerializableExtra(
            KeyWordsAndConstants.OPERATION_MODE
        )?.let { mode ->
            if (mode == OperationMode.EDIT) {
                val incidentDetails: IncidentDetails =
                    fromJson(intent.getStringExtra(DATA)!!)
                var routeListResponce: RouteListResponce? = null
                list.forEach { item ->
                    if (item.data.route_id == incidentDetails.route_id)
                        routeListResponce = item.data
                }
                binding.spinnerRoute.select(
                    data = routeListResponce
                )
            }
        }

    }

    private fun loadSpinnerStateListData(stateListArray: ArrayList<StateList>) {
        if (stateListArray.size > 0) {
            val selectedStateId = viewModel.giveRepository().giveStateId()
            stateListArray.forEach { state ->
                if (state.state_id == selectedStateId) {
                    stateList = state
                }
            }
        }
        val list: ArrayList<SpinnerData<StateList>> = ArrayList()
        for (i in 0 until stateListArray.size) {
            list.add(
                SpinnerData(
                    stateListArray[i].state_name, stateListArray[i]
                )
            )
        }
        binding.spinnerState.heading("State")
        binding.spinnerState.editable(false)
        binding.spinnerState.spinnerWidthPercent(60)
        binding.spinnerState.addItems(
            list,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    stateList = item.data as StateList
                }
            }
        )
        try {
            binding.spinnerState.select<SpinnerData<StateList>>(
                toShowString = stateList.state_name
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        intent.getSerializableExtra(
            KeyWordsAndConstants.OPERATION_MODE
        )?.let { mode ->
            if (mode == OperationMode.EDIT) {
                val incidentDetails: IncidentDetails =
                    fromJson(intent.getStringExtra(DATA)!!)
                var stateList: StateList? = null
                list.forEach { item ->
                    if (item.data.state_id == incidentDetails.state_id)
                        stateList = item.data
                }
                binding.spinnerState.select(
                    data = stateList
                )
            }
        }

    }

    private fun loadSpinnerLaneListData(laneListArray: ArrayList<LaneList>) {
        if (laneListArray.size > 0)
            laneList = laneListArray[0]
        val list: ArrayList<SpinnerData<LaneList>> = ArrayList()
        for (i in 0 until laneListArray.size) {
            list.add(
                SpinnerData(
                    laneListArray[i].lane_name, laneListArray[i]
                )
            )
        }
        binding.spinnerRamplanes.heading("Lane")
        binding.spinnerRamplanes.spinnerWidthPercent(60)
        binding.spinnerRamplanes.addItems(
            list,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    laneList = item.data as LaneList
                }
            }
        )
        intent.getSerializableExtra(
            KeyWordsAndConstants.OPERATION_MODE
        )?.let { mode ->
            if (mode == OperationMode.EDIT) {
                val incidentDetails: IncidentDetails = fromJson(intent.getStringExtra(DATA)!!)
                var laneResponce: LaneList? = null
                list.forEach { item ->
                    if (item.data.lane_id == incidentDetails.lane_id)
                        laneResponce = item.data
                }
                binding.spinnerRamplanes.select(
                    data = laneResponce
                )
            }
        }

    }

    private fun loadSpinnerVehicelTypeListData(vehicelTypeList: ArrayList<VehicleTypeResponce>) {
        this.vehicelTypeList = vehicelTypeList
        initialiseVehicleInformationList()
    }

    private fun loadSpinnerIncidentTypeListData(incidentTypeList: ArrayList<IncidentTypeResponce>) {
        if (incidentTypeList.size > 0)
            incidentTypeResponce = incidentTypeList[0]
        val list: ArrayList<SpinnerData<IncidentTypeResponce>> = ArrayList()
        list.add(
            SpinnerData(
                "Select", IncidentTypeResponce("0","Select","")
            )
        )
        for (i in 0 until incidentTypeList.size) {
            list.add(
                SpinnerData(
                    incidentTypeList[i].name, incidentTypeList[i]
                )
            )
        }
        binding.spinnerIncidentType.heading("Incident Type")
        binding.spinnerIncidentType.spinnerWidthPercent(60)
        binding.spinnerIncidentType.addItems(
            list,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    incidentTypeResponce = item.data as IncidentTypeResponce
                    incidentType = incidentTypeResponce.incident_id
                    if(!incidentTypeResponce.name.equals("Select", true)){
                        getFields()
                    }else{
                        binding.llIncidentStepOne.visibility = View.GONE
                    }

                }
            }
        )
        binding.spinnerIncidentType.editable(true)
        binding.spinnerIncidentType.visibility = View.VISIBLE
        intent.getSerializableExtra(
            KeyWordsAndConstants.OPERATION_MODE
        )?.let { mode ->
            if (mode == OperationMode.EDIT) {
                val incidentDetails: IncidentDetails =
                    fromJson(intent.getStringExtra(DATA)!!)
                var incidentTypeResponce: IncidentTypeResponce? = null
                list.forEach { item ->
                    if (item.data.incident_id == incidentDetails.incedent_type_id)
                        incidentTypeResponce = item.data
                }
                binding.spinnerIncidentType.select(
                    data = incidentTypeResponce
                )
            }
            if (mode == OperationMode.ADD) {
            /*    val incidentTypeId = intent.getStringExtra("INCIDENT_TYPE")
                val selected = list.find { it.data.incident_id == incidentTypeId }
                selected?.let { selectedPositive ->
                    binding.spinnerIncidentType.select(data = selectedPositive.data)
                }*/
               val mAddIncidentRequest =  (this.applicationContext as ApplicationClass).mAddIncidentRequest
                val selected = list.find { it.data.incident_id == mAddIncidentRequest?.incidentType }
                selected?.let { selectedPositive ->
                    binding.spinnerIncidentType.select(data = selectedPositive.data)
                }
            }
        }
    }

    private fun loadSpinnerAssectTypeListData(assectTypeList: ArrayList<AssectTypeResponce>) {
        if (assectTypeList.size > 0)
            assectTypeResponce = assectTypeList[0]
        val list: ArrayList<SpinnerData<AssectTypeResponce>> = ArrayList()
        for (i in 0 until assectTypeList.size) {
            list.add(
                SpinnerData(
                    assectTypeList[i].assist_type, assectTypeList[i]
                )
            )
        }
        binding.spinnerAssistType.heading("Assist Type")
        binding.spinnerAssistType.spinnerWidthPercent(60)
        binding.spinnerAssistType.addItems(
            list,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    assectTypeResponce = item.data as AssectTypeResponce
                }
            }
        )

        /*binding.spinnerDirection.heading("Direction")
        binding.spinnerDirection.spinnerWidthPercent(60)
        binding.spinnerDirection.addItems(
            arrayListOf(
                SpinnerData(
                    "One Direction",
                    "One Direction"
                ),
                SpinnerData(
                    "Both Direction",
                    "Both Direction"
                )
            ),
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    selectedDirection = item.data as String
                }
            }
        )*/

        intent.getSerializableExtra(
            KeyWordsAndConstants.OPERATION_MODE
        )?.let { mode ->
            if (mode == OperationMode.EDIT) {
                val incidentDetails: IncidentDetails =
                    fromJson(intent.getStringExtra(DATA)!!)
                var assectTypeResponce: AssectTypeResponce? = null
                list.forEach { item ->
                    if (item.data.assist_id == incidentDetails.assist_type_id)
                        assectTypeResponce = item.data
                }
                binding.spinnerAssistType.select(
                    data = assectTypeResponce
                )
            }
        }

    }

    private fun loadSpinnerCallAtListData(callListData: ArrayList<CallAtTimeSpinnerData>) {
        val df = SimpleDateFormat("hh:mm a")
        val now = Calendar.getInstance()
        val hourOfDay = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)
        //now.add(Calendar.MINUTE,1)
        val currTime = df.format(now.time)

        val callAtData: ArrayList<SpinnerData<CallAtTimeSpinnerData>> = ArrayList()

        /*callListData.forEach { data ->
            val saved = Calendar.getInstance()
            val hourSaved = data.data.split(" ")[0].split(":")[0]
            val minuteSaved = data.data.split(" ")[0].split(":")[1]
            val amOrPmSaved = data.data.split(" ")[1]

            saved.set(Calendar.HOUR, hourSaved.toInt())
            saved.set(Calendar.MINUTE, minuteSaved.toInt())
            saved.set(Calendar.AM_PM, if (amOrPmSaved == "am") Calendar.AM else Calendar.PM)

            if (now.timeInMillis >= saved.timeInMillis) {
                callAtData.add(
                    SpinnerData(
                        data.data, data
                    )
                )
            }
        }*/
        if (callListData.size > 0) {
            callAtTimeSpinnerData = callListData[0]
            callStartTimeSpinnerData = callListData[0]
            callCompleteTimeSpinnerData = callListData[0]
            incidentTimeTimeSpinnerData = callListData[0]
        }

        val list: ArrayList<SpinnerData<CallAtTimeSpinnerData>> = ArrayList()
        for (i in 0 until callListData.size) {
            list.add(
                SpinnerData(
                    callListData[i].data, callListData[i]
                )
            )
        }

        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.MINUTE, 1)
        val newTime = df.format(cal.getTime())
        val callData = viewModel.giveRepository().getPastValuesListForCallAtSpinner(newTime)
        callData?.forEach {data->
            callAtData.add(SpinnerData(data.data,data))
        }

        binding.spinnerCallAt.heading("Dispatched Time")
        binding.spinnerCallAt.editable(true)
        binding.spinnerCallAt.spinnerWidthPercent(60)
        /*binding.spinnerCallAt.addItems(
            callAtData,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    callAtTimeSpinnerData = item.data as CallAtTimeSpinnerData
                }
            }
        )
        binding.spinnerCallAt.select<CallAtTimeSpinnerData>(
            callAtData[callAtData.size-1].toShow
        )*/

        intent.getSerializableExtra(
            KeyWordsAndConstants.OPERATION_MODE
        )?.let { mode ->
            when(mode) {
                OperationMode.EDIT-> {
                    val incidentDetails: IncidentDetails =
                        fromJson(intent.getStringExtra(DATA)!!)
                    binding.spinnerCallAt.addItems(
                        list,
                        object : Spinner.OnItemSelectedListener {
                            override fun <T> selected(item: SpinnerData<T>) {
                                callAtTimeSpinnerData = item.data as CallAtTimeSpinnerData
                            }
                        }
                    )
                    callAtTimeSpinnerData =
                        viewModel.giveRepository().getNearestFutureTimeForCallSpinner(
                            incidentDetails.call_at
                        )
                    binding.spinnerCallAt.select(
                        data = callAtTimeSpinnerData
                    )
                }
                OperationMode.ADD->{
                    binding.spinnerCallAt.addItems(
                        callAtData,
                        object : Spinner.OnItemSelectedListener {
                            override fun <T> selected(item: SpinnerData<T>) {
                                callAtTimeSpinnerData = item.data as CallAtTimeSpinnerData
                            }
                        }
                    )
                    binding.spinnerCallAt.select<CallAtTimeSpinnerData>(
                        callAtData[callAtData.size-1].toShow
                    )
                }else->{

            }
            }

        }

        binding.spinnerCallStarted.heading("Call Started")
        binding.spinnerCallStarted.editable(true)
        binding.spinnerCallStarted.spinnerWidthPercent(60)
        binding.spinnerCallStarted.addItems(
            list,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    callStartTimeSpinnerData = item.data as CallAtTimeSpinnerData
                    loadCallCompleteTimeData()

                }
            }
        )
        callStartTimeSpinnerData = viewModel.giveRepository().getNearestFutureTimeForCallSpinner(currTime)
        binding.spinnerCallStarted.select(
            data = callStartTimeSpinnerData
        )

        intent.getSerializableExtra(
            KeyWordsAndConstants.OPERATION_MODE
        )?.let { mode ->
            if (mode == OperationMode.EDIT) {
                try {
                    val incidentDetails: IncidentDetails =
                        fromJson(intent.getStringExtra(DATA)!!)
                    callStartTimeSpinnerData =
                        viewModel.giveRepository().getNearestFutureTimeForCallSpinner(
                            incidentDetails.call_started
                        )
                    binding.spinnerCallStarted.select(
                        data = callStartTimeSpinnerData
                    )
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }

        binding.spinnerCallCompleted.heading("Call Completed")
        binding.spinnerCallCompleted.spinnerWidthPercent(60)


        /*intent.getSerializableExtra(
            KeyWordsAndConstants.OPERATION_MODE
        )?.let { mode ->
            if (mode == OperationMode.EDIT) {
                val incidentDetails: IncidentDetails =
                    fromJson(intent.getStringExtra(DATA)!!)
                callCompleteTimeSpinnerData =
                    viewModel.giveRepository().getNearestFutureTimeForCallSpinner(
                        incidentDetails.call_completed
                    )
                binding.spinnerCallCompleted.select(
                    data = callCompleteTimeSpinnerData
                )
            }
        }*/

        /*binding.spinnerIncidentTime.addItems(
            list,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    incidentTimeTimeSpinnerData = item.data as CallAtTimeSpinnerData
                }
            }
        )

        intent.getSerializableExtra(
            KeyWordsAndConstants.OPERATION_MODE
        )?.let { mode ->
            if (mode == OperationMode.EDIT) {
                val incidentDetails: IncidentDetails =
                    fromJson(intent.getStringExtra(DATA)!!)
                var callAtTimeSpinnerData: CallAtTimeSpinnerData? = null
                list.forEach { item ->
                    if (item.data.equals(incidentDetails.incedent_time))
                        callAtTimeSpinnerData = item.data
                }
                binding.spinnerIncidentTime.select(
                    data = callAtTimeSpinnerData
                )
            }
        }*/
        binding.spinnerIncidentTime.heading("Incident Duration")
        binding.spinnerIncidentTime.spinnerWidthPercent(60)
        binding.spinnerIncidentTime.editable(false)
    }

    private fun loadCallCompleteTimeData() {
        val callstartTime = callStartTimeSpinnerData.data
        val df = SimpleDateFormat("hh:mm a")
        val startTime = df.parse(callstartTime)
        val spinnerDataForCallCompleted: ArrayList<SpinnerData<CallAtTimeSpinnerData>> = ArrayList()
        viewModel.giveRepository().getNextFutureValuesListForCallEndSpinner(
            callStartTimeSpinnerData.data
        )?.forEach { data ->
            spinnerDataForCallCompleted.add(
                SpinnerData(
                    data.data,
                    data
                )
            )
        }
      //  callCompleteTimeSpinnerData = viewModel.getNearestFutureTimeForCallSpinner()
        binding.spinnerCallCompleted.addItems(
            spinnerDataForCallCompleted,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    callCompleteTimeSpinnerData = item.data as CallAtTimeSpinnerData
                    val df = SimpleDateFormat("hh:mm a")
                    val endTime: Date? = df.parse(callCompleteTimeSpinnerData.data)
                    val startTime: Date? = df.parse(callStartTimeSpinnerData.data)
                    loadIncidentDuration(startTime, endTime)
                }
            }
        )
        binding.spinnerCallCompleted.select<CallAtTimeSpinnerData>(
            data= spinnerDataForCallCompleted[14].data
        )
        if(!this.loadCallCompleteFlag) {
            intent.getSerializableExtra(
                KeyWordsAndConstants.OPERATION_MODE
            )?.let { mode ->
                if (mode == OperationMode.EDIT) {
                    val incidentDetails: IncidentDetails =
                        fromJson(intent.getStringExtra(DATA)!!)
                    callCompleteTimeSpinnerData =
                        viewModel.giveRepository().getNearestFutureTimeForCallSpinner(
                            incidentDetails.call_completed
                        )
                    binding.spinnerCallCompleted.select(
                        data = callCompleteTimeSpinnerData
                    )
                }
            }
            loadCallCompleteFlag=true
        }




    }

    private fun loadIncidentDuration(startDate: Date?, endDate: Date?) {
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        var difference = (endDate?.time ?: Date().time) - ((startDate?.time ?: Date().time))
        if (difference < 0) {
            val dateMax = simpleDateFormat.parse("24:00:00")
            val dateMin = simpleDateFormat.parse("00:00:00")
            difference = dateMax.time - ((startDate?.time ?: Date().time) + ((endDate?.time
                ?: Date().time) - (dateMin?.time ?: Date().time)))
        }
        val days = (difference / (1000 * 60 * 60 * 24)).toInt()
        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
        val min =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)
        val sec =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours - 1000 * 60 * min).toInt() / 1000

        Log.i("log_tag", "Hours: $hours, Mins: $min, Secs: $sec")
        var diffTime = ""
        diffTime = "$hours h $min m"
        /*if(hours==0){
            diffTime =   "$min minutes"
        }else if(hours==1){
            diffTime =   "$hours h $min m"
        }else{
            diffTime =   "$hours h $min m"
        }*/
        val incidentTimeData: ArrayList<SpinnerData<CallAtTimeSpinnerData>> = ArrayList()
        incidentTimeData.add(
            SpinnerData(
                diffTime,
                CallAtTimeSpinnerData(diffTime)
            )
        )
        binding.spinnerIncidentTime.addItems(
            incidentTimeData,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    incidentTimeTimeSpinnerData = item.data as CallAtTimeSpinnerData
                }
            }
        )
    }

    @NeedsPermission(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun getLocationPermission() {
        startLocationIfNotEnabled()
    }

    @OnShowRationale(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun showRationaleLocationPermission(request: PermissionRequest) {
        showMessageWithTwoButton(
            message = "We need some permission to get location.",
            buttonOneText = "Okay",
            buttonTwoText = "Cancel",
            callback = object : DialogUtil.MultiButtonCallBack {
                override fun buttonOneClicked() {
                    super.buttonOneClicked()
                    request.proceed()
                }

                override fun buttonTwoClicked() {
                    super.buttonTwoClicked()
                    request.cancel()
                    finish()
                }
            }
        )
    }

    @OnPermissionDenied(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun permissionDeniedLocationPermission() {
        showMessageWithOneButton(
            "You will not be able to get location. you can again accept the permission by going directly to permission section in settings.",
            object : DialogUtil.CallBack {
                override fun dialogCancelled() {

                }

                override fun buttonClicked() {
                    finish()
                }

            }
        )
    }

    @OnNeverAskAgain(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun neverAskAgainLocationPermission() {
        showMessageWithOneButton(
            "You will not be able to get location. you can again accept the permission by going directly to permission section in settings.",
            object : DialogUtil.CallBack {
                override fun dialogCancelled() {

                }

                override fun buttonClicked() {
                    finish()
                }

            }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)
        if (requestCode == KeyWordsAndConstants.REQUEST_CHECK_SETTINGS_FOR_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                startLocationIfNotEnabled()
            } else {
                showMessageWithOneButton(
                    message = "We are not able to get your location.",
                    cancellable = false,
                    buttonText = "Okay",
                    callback = object : DialogUtil.CallBack {

                        override fun buttonClicked() {
                            super.buttonClicked()

                            finish()
                        }
                    }
                )
            }
        }
    }

    private fun startLocationIfNotEnabled() {
        val locationRequest = LocationRequest()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener(this) {
            getLocationAndSetToUI()
        }

        task.addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(
                        this,
                        KeyWordsAndConstants.REQUEST_CHECK_SETTINGS_FOR_LOCATION
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    showMessageWithOneButton(
                        message = "We are not able to get your location.",
                        cancellable = false,
                        buttonText = "Okay",
                        callback = object : DialogUtil.CallBack {

                            override fun buttonClicked() {
                                super.buttonClicked()

                                finish()
                            }
                        }
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationAndSetToUI() {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener {
                if(it==null ){
                    getLocationAndSetToUI()
                    return@addOnSuccessListener
                }
                it?.let { location ->
                    binding.editTextLatitude.setText(
                        location.latitude.toString()
                    )
                    binding.editTextLongitude.setText(
                        location.longitude.toString()
                    )
                }
            }
    }

    private fun tryPreFillingData() {
        intent.getSerializableExtra(KeyWordsAndConstants.OPERATION_MODE)?.let {
            if (
                (it as OperationMode) == OperationMode.EDIT
            ) {
                setPageTitle("EDIT INCIDENT")
                val incidentDeatils: IncidentDetails =
                    fromJson(intent.getStringExtra(DATA)!!)
                binding.editTextMileMarker.setText(
                    if (incidentDeatils.mile_marker == null) "" else incidentDeatils.mile_marker!!
                )
                setUpRoadSurfaceAndLaneLocationFromData(incidentDeatils)
                binding.spinnerPassengerTransported.select<SpinnerData<String>>(
                    incidentDeatils.passenger_transported
                )

                binding.spinnerFirstResponder.select<SpinnerData<String>>(
                    incidentDeatils.first_responder
                )

                binding.editTextFirstResponderUnitNumber.setText(
                    if (incidentDeatils.first_responder_unit_number == null) "" else incidentDeatils.first_responder_unit_number!!
                )

                binding.spinnerNumberOfVehicle.select<SpinnerData<String>>(
                    incidentDeatils.number_of_vehicle
                )
                /*binding.spinnerRamplanes.select<SpinnerData<String>>(
                    incidentDeatils.ramp_lane
                )*/
                binding.editTextComment.setText(
                    if (incidentDeatils.comments == null) "" else incidentDeatils.comments!!
                )
                binding.editTextLaneRestorationTime.setText(
                    if (incidentDeatils.lane_restoration_time == null) "" else incidentDeatils.lane_restoration_time!!
                )
                binding.incidentNoPrefixTv.text =
                    if (incidentDeatils.incident_no_prefix == null) "" else incidentDeatils.incident_no_prefix!!
                binding.incidentNoEt.setText(
                    if (incidentDeatils.incident_no == null) "" else incidentDeatils.incident_no!!
                )
                try {
                    intent.getStringExtra(DIRECTION)?.let { direction ->
                        selectedDirection = direction
                    }
                    intent.getStringExtra(DESCRIPTION)?.let { description ->
                        selectedDescription = description
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            } else {
                intent.getStringExtra(DIRECTION)?.let { direction ->
                    selectedDirection = direction
                }
                intent.getStringExtra(DESCRIPTION)?.let { description ->
                    selectedDescription = description
                }
            }
        }
    }

    private fun setUpRoadSurfaceAndLaneLocationFromData(incidentDeatils: IncidentDetails) {
        try {
            incidentDeatils.road_surface?.let {
                if (it.isEmpty())
                    return
                this.roadSurfaceListAdapter.setSelected(
                    it
                )
            }
            this.laneLocationListAdapter.setSelected(
                incidentDeatils.lane_location_name?:""
            )
            /*incidentDeatils.lane_location_name?.let {
                if (it.isEmpty())
                    return
                this.laneLocationListAdapter.setSelected(
                    it
                )
            }*/
        } catch (ex: Exception) {
            ex.printStackTrace()

            Handler(Looper.getMainLooper()).postDelayed(
                {
                    setUpRoadSurfaceAndLaneLocationFromData(incidentDeatils)
                },
                1000
            )
        }
    }

    private fun initialiseVehicleInformationList() {
        if (!this::colorList.isInitialized)
            return

        if (!this::motoristTypeList.isInitialized)
            return

        val mode = intent.getSerializableExtra(
            KeyWordsAndConstants.OPERATION_MODE
        )?.let { modeInner ->
            if (modeInner == OperationMode.EDIT) {
                OperationMode.EDIT
            } else {
                OperationMode.ADD
            }
        } ?: run {
            OperationMode.ADD
        }
        binding.recyclerViewVehicleInformation.layoutManager = LinearLayoutManager(this)
        if(!this::vehicleInformationListAdapter.isInitialized) {
            vehicleInformationListAdapter = VehicleInformationListAdapter(
                colorList,
                motoristTypeList,
                mode,
                if (mode == OperationMode.EDIT) fromJson<IncidentDetails>(intent.getStringExtra(DATA)!!) else null
            )
        }
        binding.recyclerViewVehicleInformation.adapter = vehicleInformationListAdapter

        Handler(Looper.getMainLooper())
            .postDelayed(
                {
                    intent.getSerializableExtra(
                        KeyWordsAndConstants.OPERATION_MODE
                    )?.let { mode ->
                        if (mode == OperationMode.EDIT) {
                            val incidentDetails: IncidentDetails =
                                fromJson(intent.getStringExtra(DATA)!!)

                            incidentDetails.vehicleInformation?.let {
                                vehicleInformationListAdapter.setSelected(
                                    it
                                )
                            }

                        }
                    }
                },
                1000
            )
    }

    private fun configureVehicleInformationCount() {
        try {
            vehicleInformationListAdapter.setCount(
                selectedNumberOfVehicle.toInt()
            )
        } catch (ex: Exception) {
            ex.printStackTrace()

            Handler(Looper.getMainLooper())
                .postDelayed(
                    {
                        configureVehicleInformationCount()
                    },
                    1000
                )
        }
    }

    private fun defaultDateTime(){
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        var formatedHour = startHour
        var formatedMinutes = startMinute
        var timeSet = ""
        if (formatedHour > 12) {
            formatedHour -= 12
            timeSet = "pm"
        } else if (formatedHour == 0) {
            formatedHour += 12
            timeSet = "am"
        } else if (formatedHour == 12) {
            timeSet = "pm"
        } else {
            timeSet = "am"
        }

        var min: String? = ""
        if (formatedMinutes < 10) min = "0$formatedMinutes" else min = java.lang.String.valueOf(formatedMinutes)

        val mTime = StringBuilder().append(formatedHour).append(':')
            .append(min).append(" ").append(timeSet).toString()
        binding.editTextLaneRestorationTime.setText(
            "${startMonth + 1}-$startDay-$startYear $mTime"
        )
    }
}
