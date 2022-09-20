package com.met.atims_reporter.ui.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Window
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.fxn.pix.Pix
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.databinding.ActivityDashboardBinding
import com.met.atims_reporter.databinding.DialogChooseVehicleBinding
import com.met.atims_reporter.enums.OperationMode
import com.met.atims_reporter.model.StartBreakTimeResponse
import com.met.atims_reporter.model.VehicleList
import com.met.atims_reporter.ui.add_incident.data.AddIncidentDataActivity
import com.met.atims_reporter.ui.dashboard.adapter.DashboardFragmentsViewpagerAdapter
import com.met.atims_reporter.ui.login.LoginActivity
import com.met.atims_reporter.ui.notification_list.NotificationListActivity
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.ImagePickerUtil
import com.met.atims_reporter.util.UiUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import com.met.atims_reporter.widget.BottomNav
import com.met.atims_reporter.widget.spinner.Spinner
import com.met.atims_reporter.widget.spinner.SpinnerData
import com.sagar.android.logutilmaster.LogUtil
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import permissions.dispatcher.*

@RuntimePermissions
class Dashboard : AtimsSuperActivity(canBeLastActivity = true), KodeinAware {

    override val kodein: Kodein by kodein()

    private val provider: ViewModelProvider by instance()
    private val logUtil: LogUtil by instance()
    private lateinit var viewModel: ViewModel
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var adapter: DashboardFragmentsViewpagerAdapter
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var showVehiclesToChoose = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = setLayout(
            R.layout.activity_dashboard
        )

        showDashboardActionbar()
        setUpViewPager()
        willHandleBackNavigation()

        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            provider
        )
            .get(ViewModel::class.java)
        bindToViewModel()

        requestForLocationPermissionsWithPermissionCheck()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        viewModel.giveRepository().getHomeGridItems()
    }

    fun setNotificationCount(count: String) {
        notificationCount(count.toInt())
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataLogout.observe(
            this,
            Observer<Event<String>> { t ->
                if (t.shouldReadContent()) {
                    t.readContent()
                    hideProgress()

                    endAllActivities()
                    startActivity(
                        UiUtil.clearStackAndStartNewActivity(
                            Intent(
                                this@Dashboard,
                                LoginActivity::class.java
                            )
                        )
                    )
                    viewModel.giveRepository().clearAllData()
                    finish()
                }
            }
        )
        viewModel.mediatorLiveDataLogoutError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )
        viewModel.mediatorLiveDataStartBreak.observe(
            this,
            Observer<Event<StartBreakTimeResponse>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        it.readContent()
                        checkForBreak()
                    }
                }
            }
        )
        viewModel.mediatorLiveDataStartBreakError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )
        viewModel.mediatorLiveDataEndBreak.observe(
            this,
            Observer<Event<String>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        it.readContent()
                        checkForBreak()
                    }
                }
            }
        )
        viewModel.mediatorLiveDataEndBreakError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        showMessageWithOneButton(
                            message = it.getContent()!!.getMessageToShow(),
                            cancellable = false,
                            callback = object : DialogUtil.CallBack {

                                override fun buttonClicked() {
                                    super.buttonClicked()

                                    checkForBreak()
                                }
                            }
                        )
                    }
                }
            }
        )

        viewModel.mediatorLiveDataVehicelListError.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )
        viewModel.mediatorLiveDataVehicelList.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        if (!showVehiclesToChoose)
                            return@Observer

                        showVehiclesToChoose = false
                        hideProgress()
                        val bindingDialogChooseVehicle = DialogChooseVehicleBinding.inflate(
                            LayoutInflater.from(this),
                            null,
                            false
                        )
                        val dialogChooseVehicle = Dialog(this)
                        dialogChooseVehicle.apply {
                            requestWindowFeature(Window.FEATURE_NO_TITLE)
                            setContentView(bindingDialogChooseVehicle.root)
                            window
                                ?.setLayout(
                                    CoordinatorLayout.LayoutParams.MATCH_PARENT,
                                    CoordinatorLayout.LayoutParams.WRAP_CONTENT
                                )
                            window?.setBackgroundDrawableResource(android.R.color.transparent)
                            setCancelable(true)
                            show()
                        }
                        bindingDialogChooseVehicle.apply {
                            val vehicles = ArrayList<SpinnerData<VehicleList>>()
                            it.getContent()!!.forEach { vehicle ->
                                vehicles.add(
                                    SpinnerData(
                                        vehicle.ID,
                                        vehicle
                                    )
                                )
                            }
                            viewModel.giveRepository().vehicleSelectedByManager =
                                vehicles[0].data
                            spinnerVia.apply {
                                heading("Choose Vehicle")
                                addItems(
                                    vehicles,
                                    object : Spinner.OnItemSelectedListener {
                                        override fun <T> selected(item: SpinnerData<T>) {
                                            viewModel.giveRepository().vehicleSelectedByManager =
                                                item.data as VehicleList
                                        }
                                    }
                                )
                            }
                            buttonActionOne.apply {
                                text = "Submit"
                                setOnClickListener {
                                    checkForBreak()
                                    dialogChooseVehicle.dismiss()
                                }
                            }
                            buttonActionTwo.apply {
                                text = "Cancel"
                                setOnClickListener {
                                    dialogChooseVehicle.dismiss()
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    private fun setUpViewPager() {
        intent
        adapter = DashboardFragmentsViewpagerAdapter(
            supportFragmentManager,
            this,
            this
        )
        binding.content.viewPager.adapter = adapter
        binding.content.viewPager.offscreenPageLimit = 4

        binding.content.bottomNav.registerForCallback(
            object : BottomNav.Callback {
                override fun selectedTab(tabs: BottomNav.TABS) {
                    when (tabs) {
                        BottomNav.TABS.HOME -> {
                            viewModel.giveRepository().getHomeGridItems()
                            binding.content.viewPager.currentItem = 0
                        }
                        BottomNav.TABS.FAQ ->
                            binding.content.viewPager.currentItem = 1
                        BottomNav.TABS.REPORT ->
                            binding.content.viewPager.currentItem = 2
                        BottomNav.TABS.PROFILE ->
                            binding.content.viewPager.currentItem = 3
                    }
                }
            }
        )

        binding.content.viewPager.addOnPageChangeListener(
            object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                }

                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> {
                            viewModel.giveRepository().getHomeGridItems()
                            binding.content.bottomNav.selectHome(false)
                        }
                        1 ->
                            binding.content.bottomNav.selectFAQ(false)
                        2 ->
                            binding.content.bottomNav.selectedReport(false)
                        3 ->
                            binding.content.bottomNav.selectedProfile(false)
                    }
                }
            }
        )
    }

    override fun logout() {
        super.logout()

        if (!isConnectedToNetwork()) {
            showMessageInDialog("Please connect to internet to logout")
            return
        }

        showMessageWithTwoButton(
            message = "Are you sure you want to logout?",
            callback = object : DialogUtil.MultiButtonCallBack {
                override fun buttonOneClicked() {
                    super.buttonOneClicked()
                    showProgress()
                    if (viewModel.giveRepository().shiftStarted()) {
                        adapter.home.endShiftForLogout()
                    } else {
                        viewModel.logout()
                    }
                }
            },
            cancellable = true,
            buttonOneText = "Yes",
            buttonTwoText = "No"
        )
    }

    fun proceedToLogout() {
        viewModel.logout()
    }

    fun loadReportFragment() {
        viewModel.getShiftReportRequest()
        binding.content.viewPager.currentItem = 2
    }

    override fun notification() {
        super.notification()
        startActivity(Intent(this, NotificationListActivity::class.java))
    }

    override fun addIncident() {
        super.addIncident()
        startActivity(
            Intent(this, AddIncidentDataActivity::class.java)
                .putExtra(KeyWordsAndConstants.OPERATION_MODE, OperationMode.ADD)
        )
    }

    override fun goBack() {
        if (binding.content.viewPager.currentItem != 0) {
            binding.content.viewPager.currentItem = 0
        } else {
            askForAppExit()
        }
    }

    fun initiateCaptureForProfilePicture() {
        takeProfilePictureWithPermissionCheck()
    }

    @NeedsPermission(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun takeProfilePicture() {
        ImagePickerUtil.pickImage(
            context = this,
            reqCode = KeyWordsAndConstants.REQUEST_CODE_PICK_IMAGE
        )
    }

    @OnShowRationale(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun showRationale(request: PermissionRequest) {
        showMessageWithTwoButton(
            message = "We need some permission to take pictures.",
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
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun permissionDenied() {
        showMessageWithOneButton(
            "You will not be able to take picture. you can again accept the permission by going directly to permission section in settings.",
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
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun neverAskAgain() {
        showMessageWithOneButton(
            "You will not be able to take picture picture. you can again accept the permission by going directly to permission section in settings.",
            object : DialogUtil.CallBack {
                override fun dialogCancelled() {

                }

                override fun buttonClicked() {
                    finish()
                }

            }
        )
    }

    @NeedsPermission(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun requestForLocationPermissions() {
    }

    @OnShowRationale(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun requestForLocationPermissionsShowRationale(request: PermissionRequest) {
        showMessageWithTwoButton(
            message = "We need some permission to send location updates to server.",
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
    fun requestForLocationPermissionsPermissionDenied() {
        showMessageWithOneButton(
            "You will not be able to start trip. you can again accept the permission by going directly to permission section in settings.",
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
    fun requestForLocationPermissionsNeverAskAgain() {
        showMessageWithOneButton(
            "You will not be able to start trip. you can again accept the permission by going directly to permission section in settings.",
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
        if (requestCode == KeyWordsAndConstants.REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            intentData?.let {
                it.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.let { images ->
                    logUtil.logV("got image $images")
                    adapter.profile.gotNewImage(images[0])
                }
            }
        }
    }

    override fun editProfile() {
        super.editProfile()
        editingProfile()
        adapter.profile.changeEditableMode(true)
    }

    override fun saveProfile() {
        super.saveProfile()
        adapter.profile.saveData()
    }

    fun profileSaved() {
        doneSavingProfile()
    }

    fun refreshHomeUserName() {
        Handler().postDelayed(
            {
                adapter.home.setUserData()
            },
            1000
        )
    }

    fun checkForBreak() {
        if (
            viewModel.giveRepository().giveBreakTimeId() != ""
        ) {
            if (
                viewModel.giveRepository().role_name == KeyWordsAndConstants.ROLE_NAME_MANAGER
            ) {
                try {
                    if (viewModel.giveRepository().vehicleSelectedByManager.vehicle_id == "") {
                        showProgress()
                        showVehiclesToChoose = true
                        viewModel.getVehicleList()
                        return
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    showProgress()
                    showVehiclesToChoose = true
                    viewModel.getVehicleList()
                    return
                }
            }
            showVehiclesToChoose = false
            showMessageWithOneButton(
                message = "On Break.",
                cancellable = false,
                buttonText = "End Break",
                callback = object : DialogUtil.CallBack {
                    override fun buttonClicked() {
                        super.buttonClicked()

                        showProgress()
                        viewModel.endBreak()
                    }
                }
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener {
                it?.let { location ->
                    adapter.home.gotLocation(
                        location.latitude.toString(),
                        location.longitude.toString()
                    )
                } ?: run {
                    adapter.home.gotLocation(
                        "2432343242",
                        "@34234"
                    )
                }
            }
    }
}
