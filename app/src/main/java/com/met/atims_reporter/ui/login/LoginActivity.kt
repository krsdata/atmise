package com.met.atims_reporter.ui.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants.CURRENT_LAT
import com.met.atims_reporter.core.KeyWordsAndConstants.CURRENT_LONG
import com.met.atims_reporter.core.KeyWordsAndConstants.DEVICE_FCM_TOKEN
import com.met.atims_reporter.databinding.ActivityLoginBinding
import com.met.atims_reporter.enums.SuperActivityStatusBarColor
import com.met.atims_reporter.model.LoginRequest
import com.met.atims_reporter.model.UserDetails
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.ui.forgot_password.ForgotPasswordActivity
import com.met.atims_reporter.util.UiUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AtimsSuperActivity(SuperActivityStatusBarColor.WHITE), KodeinAware {

    override val kodein: Kodein by kodein()

    private val viewModelProvider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    private lateinit var binding: ActivityLoginBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLat: String = ""
    private var currentLong: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_login)
        binding.context = this

        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)

        bindToViewModel()
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataLogin.observe(
            this,
            Observer<Event<UserDetails>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        processLoginResponse(it.getContent()!!)
                }
            }
        )
        viewModel.mediatorLiveDataLoginError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        handleGenericResult(it.getContent()!!)
                    }

                }
            }
        )
        viewModel.mediatorLiveDataFirebaseMessagingToken.observe(
            this,
            Observer<Event<String>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        it.readContent()
                        //login()
                        getCurrentLatLong()
                    }
                }
            }
        )
        viewModel.mediatorLiveDataFirebaseMessagingTokenError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )
    }

    fun login() {
        if (
            binding.editTextEmail.text.toString().isEmpty()
        ) {
            showMessageInDialog("Please provide user email.")
            return
        }
        if (
            !Patterns.EMAIL_ADDRESS.matcher(
                binding.editTextEmail.text.toString()
            ).matches()
        ) {
            showMessageInDialog("Please provide valid email.")
            return
        }
        if (
            binding.editTextPassword.text.toString().isEmpty()
        ) {
            showMessageInDialog("Please provide password.")
            return
        }

        showProgress()
        if (
            viewModel.giveRepository().getSharedPref().getString(
                DEVICE_FCM_TOKEN, "sjvnsviskfv123213"
            ) == ""
        ) {
            viewModel.getFirebaseToken()
            return
        }

        viewModel.login(
            LoginRequest(
                email = binding.editTextEmail.text.toString().trim(),
                password = binding.editTextPassword.text.toString(),
                device_token = viewModel.giveRepository().getSharedPref().getString(
                    DEVICE_FCM_TOKEN, "sjvnsviskfv123213"
                )!!,
                deviceId = viewModel.giveRepository().getDeviceId(),
                latitude = viewModel.giveRepository().getSharedPref().getString(
                    CURRENT_LAT, ""
                )!!,
                longitude = viewModel.giveRepository().getSharedPref().getString(
                    CURRENT_LONG, ""
                )!!
            )
        )
    }

    private fun processLoginResponse(userDetails: UserDetails) {
        hideProgress()
        moveToDashboard()
    }

    fun moveToForgotPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)

        startActivity(intent)
    }

    private fun moveToDashboard() {
        startActivity(
            UiUtil.clearStackAndStartNewActivity(
                Intent(
                    this,
                    Dashboard::class.java
                )
            )
        )
        finish()
    }


    fun getCurrentLatLong() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }else {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if (gpsStatus) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                //val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
                val locationRequest = LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            super.onLocationResult(locationResult)
                            for (location in locationResult.locations) {
                                currentLat = location.latitude.toString()
                                currentLong = location.longitude.toString()
                                Log.d("currentLatLong", "$currentLat $currentLong")
                                if ((currentLat.isEmpty() || currentLat == "null")
                                    && currentLong.isEmpty() || currentLong == "null"){
                                    getCurrentLatLong()
                                } else {
                                    val repository: Repository by instance()
                                    repository.getSharedPref().edit().putString(
                                            CURRENT_LAT,
                                            currentLat
                                        ).apply()

                                    repository.getSharedPref().edit().putString(
                                        CURRENT_LONG,
                                        currentLong
                                        ).apply()

                                    login()
                                    /*AndroidUtility.showOkDialog(
                                        base_activity,
                                        "$currentLat $currentLong",
                                        null
                                    )*/
                                }

                            }
                        }
                    },
                    null
                )
            } else{
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        }
    }
}
