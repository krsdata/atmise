package com.met.atims_reporter.ui.sos

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.databinding.ActivitySOSBinding
import com.met.atims_reporter.model.SOSReasonListResponce
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import com.met.atims_reporter.widget.spinner.SpinnerData
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import permissions.dispatcher.*

@RuntimePermissions
class SOS : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()
    private lateinit var binding: ActivitySOSBinding
    private val viewModelProvider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var sosReason: SOSReasonListResponce

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)
        binding = setLayout(R.layout.activity_s_o_s)
        binding.context = this
        showSOSBar()
        setPageTitle("SOS")
        enableBackButton()
        init()
        getSOSReasonList()
        bindToViewModel()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        getLocationPermissionWithPermissionCheck()

        viewModel.giveRepository().checkIfLoggedIn()
    }

    override fun goBack() {
        finish()
    }


    private fun init() {
        binding.tvOperatorName.text =
            viewModel.getUserDeatils()?.first_name + " " + viewModel.getUserDeatils()?.last_name
        binding.tvPhone.text = viewModel.getUserDeatils()?.contact_mobile
        binding.tvTruckNo.text = viewModel.getVehicelData()
    }

    override fun cancelSOS() {
        super.cancelSOS()
        clickedCancel()
    }
    private fun getSOSReasonList() {
        showProgress()
        viewModel.getSOSReasonList()
    }

    private fun sendSOS(lat: String, long: String, reasonId: String, message: String) {
        showProgress()
        viewModel.sendSOS(lat, long, reasonId, message)
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataSOS.observe(
            this,
            Observer<Event<String>> { t ->
                if (t.shouldReadContent()) {
                    t.readContent()
                    hideProgress()
                    showMessageWithOneButton(
                        message = "Your SOS submitted successfully",
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
        )
        viewModel.mediatorLiveDataSOSError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataSOSReasonList.observe(
            this,
            Observer<Event<ArrayList<SOSReasonListResponce>>> { t ->
                if (t.shouldReadContent()) {
                    hideProgress()
                    loadSpinneScendaryCrashData(t.getContent() ?: ArrayList())
                }
            }
        )
        viewModel.mediatorLiveDataSOSReasonListError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

    }

    private fun loadSpinneScendaryCrashData(sosReasonList: ArrayList<SOSReasonListResponce>) {
        if (sosReasonList.size > 0)
            sosReason = sosReasonList[0]
        val list: ArrayList<SpinnerData<SOSReasonListResponce>> = ArrayList()
        for (i in 0 until sosReasonList.size) {
            list.add(
                SpinnerData(
                    sosReasonList[i].sos_reason, sosReasonList[i]
                )
            )
        }
        binding.spinnerSOSReason.heading("What type of Emergency?")
        binding.spinnerSOSReason.spinnerWidthPercent(60)
        binding.spinnerSOSReason.addItems(
            list,
            object : com.met.atims_reporter.widget.spinner.Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    sosReason = item.data as SOSReasonListResponce
                }
            }
        )
    }

    fun clickedSend() {
        showMessageWithTwoButton(
            message = "Do you want to send SOS?",
            cancellable = true,
            callback = object : DialogUtil.MultiButtonCallBack {
                override fun buttonOneClicked() {
                    super.buttonOneClicked()
                    sendSOS(
                        binding.tvLat.text.toString(),
                        binding.tvLng.text.toString(),
                        sosReason.sos_reasons_master_id,
                        sosReason.sos_reason
                    )
                }
            },
            buttonOneText = "Yes",
            buttonTwoText = "No"
        )
    }

    fun clickedCancel() {
        finish()
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

    private fun getLocationAndSetToUI() {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener {
                it?.let { location ->
                    binding.tvLat.setText(
                        location.latitude.toString()
                    )
                    binding.tvLng.setText(
                        location.longitude.toString()
                    )
                }
            }
    }
}