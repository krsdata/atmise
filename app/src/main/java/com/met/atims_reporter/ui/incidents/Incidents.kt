package com.met.atims_reporter.ui.incidents

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.core.KeyWordsAndConstants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
import com.met.atims_reporter.core.KeyWordsAndConstants.OPERATION_MODE
import com.met.atims_reporter.core.KeyWordsAndConstants.UPDATE_INTERVAL_IN_MILLISECONDS
import com.met.atims_reporter.databinding.ActivityIncidentsBinding
import com.met.atims_reporter.databinding.DenyIncidentDialogBinding
import com.met.atims_reporter.databinding.DialogDirectionDescriptionBinding
import com.met.atims_reporter.enums.OperationMode
import com.met.atims_reporter.model.IncidentCloseRequest
import com.met.atims_reporter.model.IncidentStatusChangeRequest
import com.met.atims_reporter.model.IncidentTypeResponce
import com.met.atims_reporter.ui.add_incident.data.AddIncidentDataActivity
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.ui.incident_details.IncidentDetails
import com.met.atims_reporter.ui.incidents.adapter.IncidentListAdapter
import com.met.atims_reporter.util.DateUtil
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import com.met.atims_reporter.widget.edittext.EditTextInputMode
import com.met.atims_reporter.widget.edittext.MultiLineConfig
import com.met.atims_reporter.widget.spinner.Spinner
import com.met.atims_reporter.widget.spinner.SpinnerData
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import permissions.dispatcher.*
@RuntimePermissions
class Incidents : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()
    private lateinit var binding: ActivityIncidentsBinding
    private val mTitle = arrayOf("Open Incidents")
    private lateinit var incidentListAdapter: IncidentListAdapter
    private val viewModelProvider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    private var checkIngForArrivedAtSpot: Boolean = false
    private lateinit var incidentDetailsForUpdate: com.met.atims_reporter.model.IncidentDetails
    private lateinit var declineCustomDialog: Dialog
    private lateinit var directionAndDescriptionDialog: Dialog
    private val incidentTypes: ArrayList<IncidentTypeResponce> = ArrayList()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var selectedDirection:String?=""
    private var selectedDescription:String?=""
    private var currentLocation: Location?=null
    private var mLocationCallback: LocationCallback? = null
    private var mLocationRequest: LocationRequest? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_incidents)
        binding.tab.setTabData(mTitle)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                currentLocation = locationResult.lastLocation
            }
        }
        getLocationPermissionWithPermissionCheck()

        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)
        showIncidentsActionBar()
        setPageTitle("INCIDENTS")
        enableBackButton()
        willHandleBackNavigation()
        setUpSwipeRefresh()
        bindToViewModel()

        viewModel.giveRepository().checkIfLoggedIn()

    }

    override fun onResume() {
        super.onResume()

        getIncidentListAPI()
    }

    override fun goBack() {
        viewModel.giveRepository().getHomeGridItems()
        startActivity(
            Intent(
                this,
                Dashboard::class.java
            )
        )
        finish()
    }

    private fun getIncidentListAPI() {
        showProgress()
        viewModel.getIncidentList()
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataIncidentList.observe(
            this,
            Observer<Event<ArrayList<com.met.atims_reporter.model.IncidentDetails>>> { t ->
                hideProgress()
                binding.swipeRefreshLayout.isRefreshing = false
                if (t.shouldReadContent()) {
                    setAdapter(t.getContent() ?: ArrayList())
                    viewModel.getIncidentTypeList()
                }
            }
        )
        viewModel.mediatorLiveDataIncidentListError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )
        viewModel.mediatorLiveDataIncidentStatusUpdate.observe(
            this,
            Observer<Event<String>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        if (
                            checkIngForArrivedAtSpot
                        ) {
                            checkIngForArrivedAtSpot = false
                            try {
                                declineCustomDialog.dismiss()
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                            updateIncidentReport(incidentDetailsForUpdate)
                        } else
                            getIncidentListAPI()
                    }
                }
            }
        )
        viewModel.mediatorLiveDataIncidentStatusUpdateError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )
        viewModel.mediatorLiveDataCloseIncidentStatusUpdate.observe(
            this,
            Observer<Event<String>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        it.readContent()
                        showMessageInDialog(
                            "Incident is closed."
                        )
                    }
                }
            }
        )
        viewModel.mediatorLiveDataCloseIncidentStatusUpdateError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )
        viewModel.mediatorLiveDataIncidentTypeList.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        /*incidentTypes.clear()
                        incidentTypes.addAll(it.getContent()!!)*/
                    }
                }
            }
        )
        viewModel.mediatorLiveDataIncidentTypeError.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )
        viewModel.mediatorLiveDataUpdateWazeInformation.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()){
                        it.readContent()
                        hideProgress()
                        //sendIncidentDataToServer()
                        startActivity(
                            Intent(this, AddIncidentDataActivity::class.java)
                                .putExtra(
                                    OPERATION_MODE,OperationMode.ADD
                                )
                                .putExtra(
                                    AddIncidentDataActivity.DIRECTION,
                                    selectedDirection
                                )
                                .putExtra(
                                    AddIncidentDataActivity.DESCRIPTION,
                                    selectedDescription
                                ))
                    }
                }
            }
        )

        viewModel.mediatorLiveDataUpdateWazeInformationError.observe(
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

    private fun updateIncidentReport(incidentDetails: com.met.atims_reporter.model.IncidentDetails) {
        startActivity(
            Intent(
                this,
                AddIncidentDataActivity::class.java
            )
                .putExtra(
                    OPERATION_MODE,
                    OperationMode.EDIT
                )
                .putExtra(
                    DATA,
                    toJson(incidentDetails)
                )
        )
    }

    private fun setAdapter(incidentList: ArrayList<com.met.atims_reporter.model.IncidentDetails>) {
        binding.textNoDataFound.visibility = View.GONE
        if (incidentList.size == 0) {
            binding.textNoDataFound.visibility = View.VISIBLE
        }
        binding.recyclerViewIncident.layoutManager = LinearLayoutManager(this)
        incidentListAdapter = IncidentListAdapter(incidentList,
            object : IncidentListAdapter.Callback {
                override fun updateIncident(incidentDetails: com.met.atims_reporter.model.IncidentDetails) {
                    checkIfArrivedAtSpot(incidentDetails)
                }

                override fun gotoDetails(incidentDetails: com.met.atims_reporter.model.IncidentDetails) {
                    startActivity(
                        Intent(
                            this@Incidents,
                            IncidentDetails::class.java
                        )
                            .putExtra(
                                DATA,
                                toJson(incidentDetails)
                            )
                    )
                }

                override fun accept(incidentDetails: com.met.atims_reporter.model.IncidentDetails) {
                    showProgress()
                    viewModel.updateIncident(
                        IncidentStatusChangeRequest(
                            companyId = viewModel.giveRepository()
                                .getUserData()!!.company_id.toString(),
                            userId = viewModel.giveRepository().getUserData()!!.user_id.toString(),
                            incidentsReportId = incidentDetails.Incidents_report_data_id!!,
                            incident_status = "En-Route",
                            timeUTC = DateUtil.getUTCTimeStringForServer()
                        )
                    )
                }

                override fun decline(incidentDetails: com.met.atims_reporter.model.IncidentDetails) {
                    denyIncident(incidentDetails)
                }

                override fun close(incidentDetails: com.met.atims_reporter.model.IncidentDetails) {
                    closeIncident(incidentDetails)
                }
            }
        )
        binding.recyclerViewIncident.adapter = incidentListAdapter
    }

    private fun setUpSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            getIncidentListAPI()
        }
    }

    override fun addIncident() {
        super.addIncident()
        directionDescriptionsDialog(OperationMode.ADD)
    }

    private fun checkIfArrivedAtSpot(incidentDetails: com.met.atims_reporter.model.IncidentDetails) {
        if (incidentDetails.incident_status == "En-Route") {
            directionDescriptionsDialog(
                OperationMode.EDIT,
                incidentDetails
            )

            return
        }
        if(incidentDetails.is_dispatched_incedent=="0"){
            updateIncident(incidentDetails)
            return
        }


        showMessageWithTwoButton(
            message = "Have you arrived at the spot?",
            buttonOneText = "Yes",
            buttonTwoText = "No",
            callback = object : DialogUtil.MultiButtonCallBack {
                override fun buttonOneClicked() {
                    super.buttonOneClicked()

                    updateIncident(incidentDetails)
                }
            }
        )
    }

    private fun updateIncident(incidentDetails: com.met.atims_reporter.model.IncidentDetails){
        showProgress()
        incidentDetailsForUpdate = incidentDetails
        checkIngForArrivedAtSpot = true
        viewModel.updateIncident(
            IncidentStatusChangeRequest(
                companyId = viewModel.giveRepository()
                    .getUserData()!!.company_id.toString(),
                userId = viewModel.giveRepository().getUserData()!!.user_id.toString(),
                incidentsReportId = incidentDetails.Incidents_report_data_id!!,
                incident_status = "On Scene",
                timeUTC = DateUtil.getUTCTimeStringForServer()
            )
        )
    }

    @SuppressLint("SetTextI18n")
    private fun denyIncident(incidentDetails: com.met.atims_reporter.model.IncidentDetails) {
        val dialogBinding = DenyIncidentDialogBinding.inflate(
            LayoutInflater.from(this),
            null,
            false
        )
        declineCustomDialog = Dialog(this)
        declineCustomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        declineCustomDialog.setContentView(dialogBinding.root)

        dialogBinding.editText.apply {
            heading("Please provide reason.")
            inputMode(EditTextInputMode.INPUT_TEXT_MULTI_LINE, MultiLineConfig(2, 4))
        }
        dialogBinding.buttonAction.text = "Submit"

        declineCustomDialog.window
            ?.setLayout(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            )

        declineCustomDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        declineCustomDialog.setCancelable(true)
        declineCustomDialog.show()

        dialogBinding.buttonAction.setOnClickListener {
            showProgress()
            viewModel.updateIncident(
                IncidentStatusChangeRequest(
                    companyId = viewModel.giveRepository().getUserData()!!.company_id.toString(),
                    userId = viewModel.giveRepository().getUserData()!!.user_id.toString(),
                    incidentsReportId = incidentDetails.Incidents_report_data_id!!,
                    incident_status = "Denied",
                    deny_reason = dialogBinding.editText.getText(),
                    timeUTC = DateUtil.getUTCTimeStringForServer()
                )
            )
        }
    }

    private fun closeIncident(incident: com.met.atims_reporter.model.IncidentDetails) {
        showProgress()
        viewModel.closeIncident(
            IncidentCloseRequest(
                companyId = incident.company_id!!,
                userId = incident.company_user_id!!,
                incidentReportId = incident.Incidents_report_data_id!!,
                timeUTC = DateUtil.getUTCTimeStringForServer()
            )
        )
    }

    @SuppressLint("SetTextI18n")
    private fun directionDescriptionsDialog(
        mode: OperationMode,
        incidentDetails: com.met.atims_reporter.model.IncidentDetails? = null
    ) {
        showProgress()
        /*if (incidentTypes.size == 0) {
            Handler().postDelayed(
                {
                    directionDescriptionsDialog(
                        mode,
                        incidentDetails
                    )

                },
                1000
            )
            return
        }*/

        Handler(Looper.getMainLooper()).postDelayed({
            showDirectionDescriptionDialog(
                mode, incidentDetails
            )
        }, 300)
    }

    private fun showDirectionDescriptionDialog(
        mode: OperationMode,
        incidentDetails: com.met.atims_reporter.model.IncidentDetails? = null
    ) {
        val dialogBinding = DialogDirectionDescriptionBinding.inflate(
            LayoutInflater.from(this),
            null,
            false
        )
        directionAndDescriptionDialog = Dialog(this)
        directionAndDescriptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        directionAndDescriptionDialog.setContentView(dialogBinding.root)

        directionAndDescriptionDialog.window
            ?.setLayout(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            )

        directionAndDescriptionDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        directionAndDescriptionDialog.setCancelable(true)

         selectedDirection = "One Direction"

        dialogBinding.spinnerDirection.apply {
            heading("Direction")
            addItems(
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
            )
        }

         selectedDescription = "Motor Vehicle Accident"

        dialogBinding.spinnerDescription.heading("Description")
        dialogBinding.spinnerDescription.addItems(
            arrayListOf(
                SpinnerData(
                    "Motor Vehicle Accident",
                    "Motor Vehicle Accident"
                ),
                SpinnerData(
                    "Safety Service Operator-Roadside Assistance",
                    "Safety Service Operator-Roadside Assistance"
                ),
                SpinnerData(
                    "Debris in Road",
                    "Debris in Road"
                ),
                SpinnerData(
                    "Disabled Vehicle",
                    "Disabled Vehicle"
                )
            ),
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    selectedDescription = item.data as String
                }
            }
        )
//        var selectedIncidentType = incidentTypes[0]

        val listIncidentTypes: ArrayList<SpinnerData<IncidentTypeResponce>> = ArrayList()
        incidentTypes.forEach {
            listIncidentTypes.add(
                SpinnerData(
                    it.name,
                    it
                )
            )
        }
        dialogBinding.spinnerIncidentType.heading("Incident Type")
        dialogBinding.spinnerIncidentType.addItems(
            listIncidentTypes,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
//                    selectedIncidentType = item.data as IncidentTypeResponce
                }
            }
        )
        dialogBinding.spinnerIncidentType.visibility = View.GONE

        dialogBinding.buttonActionOne.text = "Submit"

        dialogBinding.buttonActionTwo.text = "Cancel"

        dialogBinding.buttonActionTwo.setOnClickListener {
            directionAndDescriptionDialog.dismiss()
        }

        dialogBinding.buttonActionOne.setOnClickListener {

            if (mode == OperationMode.ADD) {

               /* if(currentLocation==null||
                    currentLocation?.latitude==null||currentLocation?.latitude==0.0 ||
                    currentLocation?.longitude==null ||  currentLocation?.longitude==0.0 )
                         {
                    DialogUtil(this).showMessage("Unable to get your current location, please try again later")
                    return@setOnClickListener
                }*/
                showProgress()
                viewModel.updateWazeInformation(this.currentLocation?.latitude.toString(),
                    this.currentLocation?.longitude.toString(), selectedDirection?:"", selectedDescription?:"")
                /*startActivity(
                    Intent(this, AddIncidentDataActivity::class.java)
                        .putExtra(
                            OPERATION_MODE,
                            mode
                        )
                        .putExtra(
                            AddIncidentDataActivity.DIRECTION,
                            selectedDirection
                        )
                        .putExtra(
                            AddIncidentDataActivity.DESCRIPTION,
                            selectedDescription
                        ))*/
            } else {
                startActivity(
                    Intent(
                        this,
                        AddIncidentDataActivity::class.java
                    )
                        .putExtra(
                            OPERATION_MODE,
                            mode
                        )
                        .putExtra(
                            DATA,
                            toJson(incidentDetails!!)
                        )
                        .putExtra(
                            AddIncidentDataActivity.DIRECTION,
                            selectedDirection
                        )
                        .putExtra(
                            AddIncidentDataActivity.DESCRIPTION,
                            selectedDescription
                        )
                )
            }
            directionAndDescriptionDialog.dismiss()
        }
        directionAndDescriptionDialog.setOnShowListener {
           hideProgress()
        }
        directionAndDescriptionDialog.show()
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
                //getLocationAndSetToUI()
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
         mLocationRequest = LocationRequest()
        mLocationRequest?.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest!!)
        val client = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener(this) {
            Handler().postDelayed({
                getLocationAndSetToUI()
            },3000)

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
        //fusedLocationProviderClient?.requestLocationUpdates(locationRequest, )
    }
    @NeedsPermission(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
     fun getLocationAndSetToUI() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener {
                    if(it==null ){
                        fusedLocationProviderClient.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback, Looper.myLooper()
                        )
                        return@addOnSuccessListener
                    }else{
                        currentLocation=it
                    }

                }


        }
    }
}
