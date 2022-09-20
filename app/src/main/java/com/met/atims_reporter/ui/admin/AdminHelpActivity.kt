package com.met.atims_reporter.ui.admin

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
import com.met.atims_reporter.databinding.ActivityAdminHelpBinding
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import permissions.dispatcher.*

@RuntimePermissions
class AdminHelpActivity : AtimsSuperActivity(), KodeinAware {
    private lateinit var binding: ActivityAdminHelpBinding
    override val kodein: Kodein by kodein()
    private val viewModelProvider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lat = "0.0"
    private var long = "0.0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_admin_help)
        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)
        showSOSBar()
        setPageTitle("ADMIN HELP")
        enableBackButton()
        initView()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        getLocationPermissionWithPermissionCheck()
        bindToViewModel()

        viewModel.giveRepository().checkIfLoggedIn()
    }

    private fun initView() {
        binding.tvSend.setOnClickListener {
            adminHelp()
        }
    }

    override fun cancelSOS() {
        super.cancelSOS()
        finish()
        overridePendingTransition(0,0)
    }


    fun adminHelp() {
        if (
            binding.etSubject.text.toString().isEmpty()
        ) {
            showMessageInDialog("Please provide subject.")
            return
        }

        if (
            binding.edtNotes.text.toString().isEmpty()
        ) {
            showMessageInDialog("Please provide message.")
            return
        }

        showProgress()
        viewModel.adminHelp(lat, long,binding.etSubject.text.toString(),binding.edtNotes.text.toString())
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataAdminHelpSuccess.observe(
            this,
            Observer<Event<String>> { t ->
                if (t.shouldReadContent()){
                    t.readContent()
                    hideProgress()
                    showMessageWithOneButton(
                        message = "Your request submitted successfully",
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
        viewModel.mediatorLiveDataAdminHelpError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
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

    private fun getLocationAndSetToUI() {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener {
                it?.let { location ->
                    lat = location.latitude.toString()
                    long = location.longitude.toString()
                }
            }
    }
}