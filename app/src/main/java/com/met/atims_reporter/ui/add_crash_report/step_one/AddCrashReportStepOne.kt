package com.met.atims_reporter.ui.add_crash_report.step_one

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityAddCrashReportStepOneBinding
import com.met.atims_reporter.model.CrashReport
import com.met.atims_reporter.model.GetUserListResponce
import com.met.atims_reporter.model.StateList
import com.met.atims_reporter.ui.add_crash_report.step_two.AddCrashReportStepTwo
import com.met.atims_reporter.ui.crash_report.CrashReportList
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.util.DateUtil
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import com.met.atims_reporter.widget.spinner.Spinner
import com.met.atims_reporter.widget.spinner.SpinnerData
import kotlinx.android.synthetic.main.activity_add_crash_report_step_one.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import permissions.dispatcher.*

@RuntimePermissions
class AddCrashReportStepOne : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()

    private val provider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    private lateinit var binding: ActivityAddCrashReportStepOneBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var selectedState: StateList
    private var wereYouInTheTruck = ""
    private var policeData = ""
    private lateinit var getUserListResponce: GetUserListResponce
    private var mSavedInstanceState: Bundle?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_add_crash_report_step_one)
        binding.context = this

        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            provider
        ).get(ViewModel::class.java)

        mSavedInstanceState = savedInstanceState
        setPageTitle("ADD CRASH REPORT")
        enableBackButton()
        willHandleBackNavigation()
        initView()

        binding.name.apply {
            heading("Name")
            editTextWidthPercent(60)
        }

        binding.vehicleId.apply {
            heading("Vehicle Id")
            editTextWidthPercent(60)
        }

        binding.latitude.apply {
            heading("Latitude")
            editTextWidthPercent(60)
            editable(false)
        }

        binding.longitude.apply {
            heading("Longitude")
            editTextWidthPercent(60)
            editable(false)
        }

        binding.wereYouInTruck.apply {
            heading("Were you in the vehicle")
            spinnerWidthPercent(60)
            val items: ArrayList<SpinnerData<String>> = arrayListOf(
                SpinnerData("yes", "yes"),
                SpinnerData("no", "no")
            )
            wereYouInTheTruck = items[0].data
            addItems(
                items,
                object : Spinner.OnItemSelectedListener {
                    override fun <T> selected(item: SpinnerData<T>) {
                        wereYouInTheTruck = item.data as String
                    }
                }
            )
        }

        binding.spinnerPolice.apply {
            heading("Were the Police Notified?")
            spinnerWidthPercent(60)
            val items: ArrayList<SpinnerData<String>> = arrayListOf(
                SpinnerData(
                    "Yes-Police arrived and completed report",
                    "Yes-Police arrived and completed report"
                ),
                SpinnerData(
                    "Yes-Police arrived and did not complete report",
                    "Yes-Police arrived and did not complete report"
                ),
                SpinnerData("No Police Involved", "No Police Involved")
            )
            policeData = items[0].data
            addItems(
                items,
                object : Spinner.OnItemSelectedListener {
                    override fun <T> selected(item: SpinnerData<T>) {
                        policeData = item.data as String
                    }
                }
            )
        }

        binding.anyPropertyDamagedNo.setOnCheckedChangeListener { _, b ->
            if (b) {
                binding.linearLayoutThirdPartyVehiclePresent.visibility = View.GONE
            }
        }

        binding.anyPropertyDamagedYes.setOnCheckedChangeListener { _, b ->
            if (b) {
                binding.linearLayoutThirdPartyVehiclePresent.visibility = View.VISIBLE
            }
        }

        binding.wasAnyOneElseInjuredNo.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.linearLayoutHowManyPeopleInjured.visibility = View.GONE
            }
        }

        binding.wasAnyOneElseInjuredYes.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.linearLayoutHowManyPeopleInjured.visibility = View.VISIBLE
            }
        }

        fusedLocationProviderClient = FusedLocationProviderClient(this)
        getLocationPermissionWithPermissionCheck()



        bindToViewModel()
        viewModel.getStateList()
        getUserList()
    }


    override fun goBack() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.back_alert_title_txt)
        builder.setMessage(R.string.back_alert_txt)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(Html.fromHtml("<font color='#FFC405'>Yes</font>")) { dialog, arg1 ->
            viewModel.giveRepository().getHomeGridItems()
            startActivity(
                Intent(this, CrashReportList::class.java)
            )
            finish()
        }
        builder.setNegativeButton(Html.fromHtml("<font color='#FFC405'>No</font>")) { dialog, arg1 ->

        }
        builder.create()
        builder.show()
    }

    private fun initView() {

    }

    private fun getUserList() {
        showProgress()
        viewModel.getUserList()
    }

    fun moveToNext() {
        if (
            binding.name.getText().isEmpty()
        ) {
            showMessageInDialog("Please enter name")
            return
        }
        if (
            binding.vehicleId.getText().isEmpty()
        ) {
            showMessageInDialog("Please enter vehicle id")
            return
        }
        if (
            !binding.areYouInjuredYes.isChecked &&
            !binding.areYouInjuredNo.isChecked
        ) {
            showMessageInDialog("Please provide information for are you injured.")
            return
        }
        /* if (
             !binding.wasAnyOneElseInjuredYes.isChecked &&
             !binding.wasAnyOneElseInjuredNo.isChecked
         ) {
             showMessageInDialog("Please provide information for was any one else injured.")
             return
         }*/
        if (
            binding.wasAnyOneElseInjuredYes.isChecked &&
            binding.numberToggleHowMany.getCount() == 0
        ) {
            showMessageInDialog("Please provide information for how many other injured.")
            return
        }
      /*  if (
            !binding.didYouContactTmcYes.isChecked &&
            !binding.didYouContactTmcNo.isChecked
        ) {
            showMessageInDialog("Please provide information for did you contact tmc.")
            return
        }*/

        if (
            !binding.safetyBeltOnYes.isChecked &&
            !binding.safetyBeltOnNo.isChecked
        ) {
            showMessageInDialog("Please provide information for was your safety belt was fastened.")
            return
        }

        val intent = Intent(this, AddCrashReportStepTwo::class.java)
        intent.putExtra(
            DATA,
            toJson(
                CrashReport(
                    user_id = viewModel.giveRepository().getUserData()!!.user_id.toString(),
                    companyId = viewModel.giveRepository().getUserData()!!.company_id.toString(),
                    vehicle_id = viewModel.giveRepository().giveVehicleId(),
                    state_id = selectedState.state_id,
                    latitude = binding.latitude.getText(),
                    longitude = binding.longitude.getText(),
                    crash_report_date = DateUtil.getDateStringForServerMDY(),
                    crash_report_time = DateUtil.getTimeStringForServer(),
                    self_injured = if (areYouInjuredYes.isChecked) "yes" else "no",
                    other_injured = if (wasAnyOneElseInjuredYes.isChecked) "yes" else "no",
                    number_of_injured_people = if (wasAnyOneElseInjuredYes.isChecked) binding.numberToggleHowMany.getCount()
                        .toString() else "0",
                    contacted_tmc = if (didYouContactTmcYes.isChecked) "yes" else if(didYouContactTmcNo.isChecked) "no" else "",
                    contacted_supervisor = if(binding.spinnerSupervisor.isVisible)"${getUserListResponce.first_name} ${getUserListResponce.last_name}" else "",
                    you_inside_truck = wereYouInTheTruck,
                    safety_belt = if (safetyBeltOnYes.isChecked) "yes" else "no",
                    police_report = policeData,
                    anyPropertyDamaged = true,
                    thirdPartyVehicleAvailable = thirdPartyVehicleYes.isChecked
                )
            )
        )
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataStateList
            .observe(
                this,
                Observer<Event<ArrayList<StateList>>> { t ->
                    t?.let {
                        if (it.shouldReadContent()) {
                            val list = it.getContent()!!
                            if (
                                list.size == 0
                            ) {
                                showMessageWithOneButton(
                                    message = "We are unable to get some data for you to proceed. Please contact admin.",
                                    cancellable = false,
                                    callback = object : DialogUtil.CallBack {
                                        override fun buttonClicked() {
                                            super.buttonClicked()

                                            finish()
                                        }
                                    }
                                )
                            } else {
                                val toAdd: ArrayList<SpinnerData<StateList>> = ArrayList()
                                list.forEach { state ->
                                    toAdd.add(
                                        SpinnerData(state.state_name, state)
                                    )
                                }

                                if (list.size > 0)
                                    selectedState = list[0]

                                binding.state.apply {
                                    heading("State")
                                    editable(false)
                                    spinnerWidthPercent(60)
                                    addItems(
                                        toAdd,
                                        object : Spinner.OnItemSelectedListener {
                                            override fun <T> selected(item: SpinnerData<T>) {
                                                selectedState = item.data as StateList
                                            }
                                        }
                                    )
                                    select<SpinnerData<StateList>>(
                                        toShowString = viewModel.giveRepository().giveStateName()
                                    )
                                }
                            }
                        }
                    }
                }
            )

        binding.name.setText(
            "${viewModel.giveRepository().getUserData()!!.first_name} ${viewModel.giveRepository()
                .getUserData()!!.last_name}"
        )
        binding.vehicleId.setText(
            viewModel.giveRepository().giveVehicleIdToShow()
        )

        viewModel.mediatorLiveDataGetUserList.observe(
            this,
            Observer<Event<ArrayList<GetUserListResponce>>> { t ->
                if (t.shouldReadContent()) {
                    hideProgress()
                    loadSpinerUserList(t.getContent() ?: ArrayList())
                }
            }
        )
        viewModel.mediatorLiveDataGetUserListError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )
    }

    private fun loadSpinerUserList(trafficeList: ArrayList<GetUserListResponce>) {
        if (
            trafficeList.size == 0
        ) {
            showMessageWithOneButton(
                message = "We are unable to get some data for you to proceed. Please contact admin.",
                cancellable = false,
                callback = object : DialogUtil.CallBack {
                    override fun buttonClicked() {
                        super.buttonClicked()

                        finish()
                    }
                }
            )
            return
        }
        if (trafficeList.size > 0)
            getUserListResponce = trafficeList[0]
        val list: ArrayList<SpinnerData<GetUserListResponce>> = ArrayList()
        for (i in 0 until trafficeList.size) {
            list.add(
                SpinnerData(
                    trafficeList[i].first_name + " " + trafficeList[i].last_name, trafficeList[i]
                )
            )
        }
        binding.spinnerSupervisor.heading("Which supervisor did you contact?")
        binding.spinnerSupervisor.spinnerWidthPercent(60)
        binding.spinnerSupervisor.addItems(
            list,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    getUserListResponce = item.data as GetUserListResponce
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
                it?.let { location ->
                    binding.latitude.setText(
                        location.latitude.toString()
                    )
                    binding.longitude.setText(
                        location.longitude.toString()
                    )
                }
            }
    }
}