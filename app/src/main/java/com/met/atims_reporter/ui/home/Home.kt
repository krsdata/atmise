package com.met.atims_reporter.ui.home


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.met.atims_reporter.R
import com.met.atims_reporter.application.ApplicationClass
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants.HOME_GRID_SPAN
import com.met.atims_reporter.core.KeyWordsAndConstants.HOME_PAGE_GRID_SPACING
import com.met.atims_reporter.core.KeyWordsAndConstants.MANAGER_WANTS_TO_GO_ON_PATROLLING
import com.met.atims_reporter.core.KeyWordsAndConstants.MANAGER_WANTS_TO_GO_ON_PATROLLING_ASKED
import com.met.atims_reporter.core.KeyWordsAndConstants.ROLE_NAME_MANAGER
import com.met.atims_reporter.databinding.DialogChooseVehicleBinding
import com.met.atims_reporter.databinding.DialogSendSurveryBinding
import com.met.atims_reporter.databinding.DialogStartPatrollingBinding
import com.met.atims_reporter.databinding.FragmentHomeBinding
import com.met.atims_reporter.enums.HomeGridItems
import com.met.atims_reporter.enums.SendSurveyVia
import com.met.atims_reporter.enums.StartShipEnum
import com.met.atims_reporter.model.HomeGridItem
import com.met.atims_reporter.model.VehicleList
import com.met.atims_reporter.ui.admin.AdminHelpActivity
import com.met.atims_reporter.ui.crash_report.CrashReportList
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.ui.extra_time_list.ExtraTimeList
import com.met.atims_reporter.ui.fuel_report.FuelReport
import com.met.atims_reporter.ui.home.adapter.GridItemsAdapter
import com.met.atims_reporter.ui.incidents.Incidents
import com.met.atims_reporter.ui.inspection_list.InspectionList
import com.met.atims_reporter.ui.maintenance_report.MaintenanceReport
import com.met.atims_reporter.ui.pre_ops.step_one.PreOpsStepOne
import com.met.atims_reporter.ui.sos.SOS
import com.met.atims_reporter.ui.startshift.list.StartShiftActivity
import com.met.atims_reporter.ui.startshift.list.StartShiftActivity.Companion.SHIFT_STEP
import com.met.atims_reporter.util.CustomTabHelper
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.SpacesItemDecoration
import com.met.atims_reporter.util.UiUtil
import com.met.atims_reporter.util.activity.ShowMessageCallback
import com.met.atims_reporter.util.activity.ShowProgressCallback
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import com.met.atims_reporter.widget.edittext.EditText
import com.met.atims_reporter.widget.edittext.EditTextInputMode
import com.met.atims_reporter.widget.spinner.Spinner
import com.met.atims_reporter.widget.spinner.SpinnerData
import com.sagar.android.logutilmaster.LogUtil
import net.glxn.qrgen.android.QRCode
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class Home : Fragment(), KodeinAware {

    enum class GOTO {
        INCIDENTS,
        FUEL,
        PREOPS,
        EXTRATIME,
        CRASHREPORT,
        EMERGENCY,
        HELP,
        MAINTENANCE,
        INSPECTION,
        SENDSURVEY,
        STARTBREAK,
        ENDBREAK
    }

    override lateinit var kodein: Kodein

    private lateinit var requiredContext: Context
    private val viewModelProvider: ViewModelProvider by instance<ViewModelProvider>()
    private val logUtil: LogUtil by instance<LogUtil>()
    private lateinit var viewModel: ViewModel
    private lateinit var binding: FragmentHomeBinding
    private val items: ArrayList<HomeGridItem> = ArrayList()
    private lateinit var gridItemsAdapter: GridItemsAdapter
    private  var showMessageCallback: ShowMessageCallback?=null
    private  var showProgressCallback: ShowProgressCallback?=null
    private var delegateToDashboardForLogout = false
    private var endingShift = false
    private var selectedDirection = ""
    private var selectedDescription = ""
    private lateinit var selectedSendSurveyVia: SendSurveyVia
    private var showVehiclesToChoose = false
    private var vehicleSelected = false
    private lateinit var goto: GOTO

    fun setMessageCallback(showMessageCallback: ShowMessageCallback) {
        this.showMessageCallback = showMessageCallback
    }

    fun setProgressCallback(showProgressCallback: ShowProgressCallback) {
        this.showProgressCallback = showProgressCallback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(
            inflater,
            null,
            false
        )
        binding.context = this
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        (activity as AtimsSuperActivity).showDashboardActionbar()
        (activity as AtimsSuperActivity).setPageTitle("DASHBOARD")


        binding.buttonBreakTime.visibility =
            if (viewModel.giveRepository().shiftStarted()) View.VISIBLE else View.GONE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        this.requiredContext = context
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kodein = (requiredContext.applicationContext as ApplicationClass).kodein
        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            viewModelProvider
        )
            .get(
                ViewModel::class.java
            )

        binding.recyclerView.layoutManager = GridLayoutManager(
            activity,
            HOME_GRID_SPAN
        )
        gridItemsAdapter = GridItemsAdapter(
            items,
            object : GridItemsAdapter.Callback {
                override fun clicked(homeGridItem: HomeGridItem) {
                    @Suppress("NON_EXHAUSTIVE_WHEN")
                    when (homeGridItem.tag) {
                        HomeGridItems.START_SHIFT -> {
                            if (viewModel.giveRepository().shiftStarted())
                                endShift()
                            else
                                startShift()
                        }
                        HomeGridItems.INCIDENTS -> {
                            startIncidents()
                        }
                        HomeGridItems.FUEL -> {
                            startFuelReport()
                        }
                        HomeGridItems.PRE_OPS -> {
                            startPreOps()
                        }
                        HomeGridItems.EXTRA_TIME -> {
                            startExtraTimeList()
                        }
                        HomeGridItems.CRASH_REPORT -> {
                            startCrashReportList()
                        }
                        HomeGridItems.SOS -> {
                            startSOS()
                        }
                        HomeGridItems.HELP -> {
                            adminHelp()
                        }
                        HomeGridItems.MAINTENANCE_REPORT -> {
                            startMaintenanceReportList()
                        }
                        HomeGridItems.INSPECTION -> {
                            startInspectionList()
                        }
                        HomeGridItems.SEND_SURVEY -> {
                            showSendSurveyDialog()
                        }
                        HomeGridItems.ADMIN -> {
                            gotoAdminSharedDoc(homeGridItem.shareUrl)
                        }
                    }
                }
            }
        )
        binding.recyclerView.adapter = gridItemsAdapter
        binding.recyclerView.addItemDecoration(
            SpacesItemDecoration(
                HOME_PAGE_GRID_SPACING
            )
        )

        bindToViewModel()

        setUserData()
    }

    @SuppressLint("SetTextI18n")

    fun setUserData() {
        try {
            if (
                viewModel.giveRepository().shiftStarted()
            ) {
                binding.textViewUserName.text = """
                ${viewModel.giveRepository()
                    .getUserData()?.first_name} ${viewModel.giveRepository()
                    .getUserData()?.last_name}
                ${viewModel.giveRepository().giveOperationAreaName()}
                Vehicle: ${viewModel.giveRepository().giveVehicleIdToShow()}
            """.trimIndent()
            } else
                binding.textViewUserName.text =
                    "${viewModel.giveRepository()
                        .getUserData()?.first_name} ${viewModel.giveRepository()
                        .getUserData()?.last_name}"
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        if (
            viewModel.giveRepository().getUserData()?.profile_image != "" ||
            viewModel.giveRepository().getUserData()?.profile_image != null ||
            viewModel.giveRepository().getUserData()?.profile_image != "null"
        ) {

            binding.appcompatImageViewUserInitialsBG.setImage(
                viewModel.giveRepository().getUserData()?.profile_image ?: "",
                isCircularImage = true,
                needBorderWithCircularImage = true
            )

            binding.appcompatTextViewViewUserInitials.visibility = View.GONE
            binding.appcompatTextPlaceHolder.visibility = View.GONE

        } else {
            binding.appcompatTextPlaceHolder.visibility = View.VISIBLE
            binding.appcompatTextViewViewUserInitials.visibility = View.VISIBLE
            binding.appcompatImageViewUserInitialsBG.visibility = View.INVISIBLE
            binding.appcompatTextViewViewUserInitials.text =
                viewModel.giveRepository().getUserData()!!.first_name.substring(0, 1)
                    .toUpperCase(Locale.getDefault())
        }

    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataDashboardMenus.observe(
            viewLifecycleOwner,
            Observer<Event<ArrayList<HomeGridItem>>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        processGridItems(it.getContent()!!)

                        (requiredContext as Dashboard).setNotificationCount(
                            viewModel.giveRepository().giveUnReadNotificationCount().toString()
                        )

                        (requiredContext as Dashboard).checkForBreak()
                    }
                }
            }
        )

        viewModel.mediatorLiveDataDashboardMenusError.observe(
            viewLifecycleOwner,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        processError(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataEndShift.observe(
            viewLifecycleOwner,
            Observer<Event<String>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        it.readContent()
                        endingShift = true
                        if (delegateToDashboardForLogout) {
                            delegateToDashboardForLogout = false
                            (activity as Dashboard).proceedToLogout()
                        } else {
                            viewModel.getHomeGridItems()
                            showProgressCallback?.hideProgress()
                            (activity as Dashboard).loadReportFragment()
                        }
                    }
                }
            }
        )

        viewModel.mediatorLiveDataEndShiftError.observe(
            viewLifecycleOwner,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        showMessageCallback?.handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )

        viewModel.mediatorLiveDataPermissionToEndShift.observe(
            viewLifecycleOwner,
            Observer<Event<String>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        it.readContent()
                        viewModel.endShift()
                    }
                }
            }
        )

        viewModel.mediatorLiveDataPermissionToEndShiftError.observe(
            viewLifecycleOwner,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        showMessageCallback?.handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )

        viewModel.mediatorLiveDataPatrollingStatusChange.observe(
            viewLifecycleOwner,
            Observer<Event<String>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        it.readContent()
                        showProgressCallback?.hideProgress()
                    }
                }
            }
        )

        viewModel.mediatorLiveDataPatrollingStatusChangeError.observe(
            viewLifecycleOwner,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        showMessageCallback?.handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )

        viewModel.mediatorLiveDataSendSurvey.observe(
            viewLifecycleOwner,
            Observer { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        showProgressCallback?.hideProgress()
                        val link = it.getContent()!!.surveyLink
                        if (link != "") {
                            //todo show qr code here
                            if (
                                selectedSendSurveyVia == SendSurveyVia.QR_CODE
                            ) {
                                showQRCode(link)
                                return@Observer
                            }
                            CustomTabsIntent.Builder().apply {
                                setToolbarColor(
                                    ResourcesCompat.getColor(
                                        resources,
                                        R.color.colorPrimary,
                                        null
                                    )
                                )
                                setShowTitle(true)
                                setStartAnimations(
                                    requiredContext,
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out
                                )
                                setExitAnimations(
                                    requiredContext,
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out
                                )
                                CustomTabHelper().getPackageNameToUse(
                                    requiredContext,
                                    link
                                )?.let {
                                    val customTabIntent = build()
                                    customTabIntent.apply {
                                        intent.setPackage(it)
                                        launchUrl(
                                            requiredContext,
                                            Uri.parse(link)
                                        )
                                    }
                                } ?: run {
                                    startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(link)
                                        )
                                    )
                                }
                            }
                        } else {
                            showMessageCallback?.showMessageInDialog(
                                "Survey link sent to email."
                            )
                        }
                    }
                }
            }
        )

        viewModel.mediatorLiveDataSendSurveyError.observe(
            viewLifecycleOwner,
            Observer { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        showMessageCallback?.handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )
        viewModel.mediatorLiveDataVehicelListError.observe(
            viewLifecycleOwner,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        showMessageCallback?.handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )
        viewModel.mediatorLiveDataVehicelList.observe(
            viewLifecycleOwner,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        if (!showVehiclesToChoose)
                            return@Observer

                        showVehiclesToChoose = false
                        showProgressCallback?.hideProgress()
                        val bindingDialogChooseVehicle = DialogChooseVehicleBinding.inflate(
                            LayoutInflater.from(requiredContext),
                            null,
                            false
                        )
                        val dialogChooseVehicle = Dialog(requiredContext)
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
                                    vehicleSelected = true
                                    when (goto) {
                                        GOTO.INCIDENTS -> {
                                            startIncidents()
                                        }
                                        GOTO.FUEL -> {
                                            startFuelReport()
                                        }
                                        GOTO.PREOPS -> {
                                            startPreOps()
                                        }
                                        GOTO.EXTRATIME -> {
                                            startExtraTimeList()
                                        }
                                        GOTO.CRASHREPORT -> {
                                            startCrashReportList()
                                        }
                                        GOTO.EMERGENCY -> {
                                            startSOS()
                                        }
                                        GOTO.HELP -> {
                                            adminHelp()
                                        }
                                        GOTO.MAINTENANCE -> {
                                            startMaintenanceReportList()
                                        }
                                        GOTO.INSPECTION -> {
                                            startInspectionList()
                                        }
                                        GOTO.SENDSURVEY -> {
                                            showSendSurveyDialog()
                                        }
                                        GOTO.STARTBREAK -> {
                                            startBeakTime()
                                        }
                                    }
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

    private var proceed = true;

    private fun processGridItems(items: ArrayList<HomeGridItem>) {
        if (!proceed)
            return

        proceed = false

        Handler().postDelayed(
            {
                proceed = true
            },
            1000
        )
        try {
            showProgressCallback?.hideProgress()
        }catch (e: Exception){
            e.printStackTrace()
        }

        this.items.apply {
            clear()
            addAll(items)
            gridItemsAdapter.shiftStarted = viewModel.giveRepository().shiftStarted()
            gridItemsAdapter.notifyDataSetChanged()
            if (
                !viewModel.giveRepository().shiftStarted() &&
                !endingShift &&
                viewModel.giveRepository().role == "3"
            ) {
                startShift()
            } else if (
                !viewModel.giveRepository().ifPreOpsDone() &&
                !endingShift &&
                viewModel.giveRepository().role == "3"
            )
                gotoPreOps()
            else if (
                viewModel.giveRepository().role_name == ROLE_NAME_MANAGER &&
                !endingShift
            ) {
                if (
                    !viewModel.giveRepository().shiftStarted()
                ) {
                    if (
                        viewModel.giveRepository().getSharedPref().getBoolean(
                            MANAGER_WANTS_TO_GO_ON_PATROLLING_ASKED, false
                        )
                    ) {
                        return
                    }

                    viewModel.giveRepository().getSharedPref().edit()
                        .putBoolean(
                            MANAGER_WANTS_TO_GO_ON_PATROLLING_ASKED,
                            true
                        )
                        .apply()

                    showMessageCallback?.showMessageWithTwoButton(
                        message = "Do you want to start shift.",
                        cancellable = false,
                        buttonOneText = "yes",
                        buttonTwoText = "No",
                        callback = object : DialogUtil.MultiButtonCallBack {

                            override fun buttonOneClicked() {
                                super.buttonOneClicked()

                                viewModel.giveRepository().getSharedPref().edit()
                                    .putBoolean(
                                        MANAGER_WANTS_TO_GO_ON_PATROLLING,
                                        true
                                    )
                                    .apply()

                                startShift()
                            }
                        }
                    )
                } else if (
                    viewModel.giveRepository().shiftStarted() &&
                    !viewModel.giveRepository().ifPreOpsDone()
                ) {
                    startPreOps()
                }
            }


        }
        setUserData()
    }

    private fun processError(result: Result) {
        showProgressCallback?.hideProgress()
        showMessageCallback?.showMessageWithOneButton(
            message = result.getMessageToShow(),
            cancellable = true,
            buttonText = "Retry",
            callback = object : DialogUtil.CallBack {

                override fun buttonClicked() {
                    super.buttonClicked()

                    showProgressCallback?.showProgress()
                    viewModel.getHomeGridItems()
                }
            }
        )
    }

    private fun startShift() {
        val intent = if (
            viewModel.giveRepository().role_name == ROLE_NAME_MANAGER
        ) {
            Intent(
                activity,
                StartShiftActivity::class.java
            ).putExtra(SHIFT_STEP, StartShipEnum.STATE)
        } else {
            UiUtil.clearStackAndStartNewActivity(
                Intent(
                    activity,
                    StartShiftActivity::class.java
                ).putExtra(SHIFT_STEP, StartShipEnum.STATE)
            )
        }
        startActivity(
            intent
        )
        if (
            viewModel.giveRepository().role_name != ROLE_NAME_MANAGER
        ) {
            (requiredContext as AppCompatActivity).finish()
        }
    }

    private fun gotoPreOps() {
        startActivity(
            UiUtil.clearStackAndStartNewActivity(
                Intent(
                    activity,
                    PreOpsStepOne::class.java
                )
            )
        )
        (requiredContext as AppCompatActivity).finish()
    }

    private fun endShift() {
        showMessageCallback?.showMessageWithTwoButton(
            message = "Are you sure you want to end shift?",
            cancellable = true,
            buttonOneText = "Yes",
            buttonTwoText = "No",
            callback = object : DialogUtil.MultiButtonCallBack {

                override fun buttonOneClicked() {
                    super.buttonOneClicked()

                    showProgressCallback?.showProgress()
                    viewModel.permissionToEndShift()
                }
            }
        )
    }

    fun endShiftForLogout() {
        showProgressCallback?.showProgress()
        viewModel.permissionToEndShift()
        delegateToDashboardForLogout = true
    }

    private fun changeStatusForStatusIndicator() {
        goto = GOTO.INCIDENTS
        if (isAllowedToProceed()) {
            if (viewModel.giveRepository().giveIndicatorStatus() == "1") {
                val bindingPatrolling = DialogStartPatrollingBinding.inflate(
                    LayoutInflater.from(requiredContext),
                    null,
                    false
                )
                val dialogStartPatrolling = Dialog(requiredContext)
                dialogStartPatrolling.apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setContentView(bindingPatrolling.root)
                    window
                        ?.setLayout(
                            CoordinatorLayout.LayoutParams.MATCH_PARENT,
                            CoordinatorLayout.LayoutParams.WRAP_CONTENT
                        )
                    window?.setBackgroundDrawableResource(android.R.color.transparent)
                    setCancelable(true)
                    show()
                }
                bindingPatrolling.apply {
                    spinnerDirection.apply {
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
                                    selectedDirection = item.data.toString()
                                }
                            }
                        )
                    }
                    spinnerDescription.apply {
                        heading("Description")
                        addItems(
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
                                    selectedDescription = item.data.toString()
                                }
                            }
                        )
                    }
                    buttonActionOne.apply {
                        text = "Submit"
                        setOnClickListener {
                            dialogStartPatrolling.dismiss()
                            showProgressCallback?.showProgress()
                            (activity as Dashboard).getLocation()
                        }
                    }
                    buttonActionTwo.apply {
                        text = "Cancel"
                        setOnClickListener {
                            dialogStartPatrolling.dismiss()
                        }
                    }
                }
            } else {
                showMessageCallback?.showMessageWithOneButton(
                    message = "are you sure want to change you patrolling status?",
                    cancellable = true,
                    buttonText = "Yes",
                    callback = object : DialogUtil.CallBack {
                        override fun buttonClicked() {
                            super.buttonClicked()

                            showProgressCallback?.showProgress()
                            (activity as Dashboard).getLocation()
                        }
                    }
                )
            }
        }
    }

    fun gotLocation(latitude: String, longitude: String) {
        viewModel.changePatrollingStatus(
            latitude,
            longitude,
            selectedDirection,
            selectedDescription
        )
        selectedDirection = ""
        selectedDescription = ""
    }

    /**
     * this method not being used as this feature is from reporter app and not active on
     * tracker app.
     */
    @Suppress("unused")
    private fun startIncidents() {
        goto = GOTO.INCIDENTS
        if (isAllowedToProceed()) {
            startActivity(
                Intent(
                    activity,
                    Incidents::class.java
                )
            )
            (requiredContext as AppCompatActivity).finish()
        }
    }

    private fun startFuelReport() {
        goto = GOTO.FUEL
        if (isAllowedToProceed())
            startActivity(
                Intent(
                    activity,
                    FuelReport::class.java
                )
            )
    }

    private fun startPreOps() {
        goto = GOTO.PREOPS
        if (isAllowedToProceed())
            startActivity(
                Intent(
                    activity,
                    PreOpsStepOne::class.java
                )
            )
    }

    private fun startExtraTimeList() {
        goto = GOTO.EXTRATIME
        if (isAllowedToProceed())
            startActivity(
                Intent(
                    activity,
                    ExtraTimeList::class.java
                )
            )
    }

    private fun startCrashReportList() {
        goto = GOTO.CRASHREPORT
        if (isAllowedToProceed())
            startActivity(
                Intent(
                    activity,
                    CrashReportList::class.java
                )
            )
    }

    private fun startSOS() {
        goto = GOTO.EMERGENCY
        if (isAllowedToProceed())
            startActivity(
                Intent(
                    activity,
                    SOS::class.java
                )
            )
    }

    private fun adminHelp() {
        goto = GOTO.HELP
        if (isAllowedToProceed())
            startActivity(
                Intent(
                    activity,
                    AdminHelpActivity::class.java
                )
            )
    }

    private fun startMaintenanceReportList() {
        goto = GOTO.MAINTENANCE
        if (isAllowedToProceed())
            startActivity(
                Intent(
                    activity,
                    MaintenanceReport::class.java
                )
            )
    }

    private fun startInspectionList() {
        goto = GOTO.INSPECTION
        if (isAllowedToProceed())
            startActivity(
                Intent(
                    activity,
                    InspectionList::class.java
                )
            )
    }

    private fun isAllowedToProceed(dependentOnlyOnShift: Boolean = false): Boolean {
        if (viewModel.giveRepository().shiftStarted())
            return true

        if (dependentOnlyOnShift) {
            showMessageCallback?.showMessageInDialog(
                "You have not started your shift yet. Please start your shift to proceed."
            )
            return false
        }

        if (
            viewModel.giveRepository().role_name == ROLE_NAME_MANAGER
        ) {
            if (vehicleSelected) {
                vehicleSelected = false
                return true
            } else
                showVehicleList()
        } else {
            showMessageCallback?.showMessageInDialog(
                "You have not started your shift yet. Please start your shift to proceed."
            )
        }

        return false
    }

    fun startBeakTime() {
        goto = GOTO.STARTBREAK
        if (!isAllowedToProceed(true)) {
            return
        }

        showMessageCallback?.showMessageWithTwoButton(
            message = "Do you want to start break.",
            cancellable = true,
            buttonOneText = "Yes",
            buttonTwoText = "No",
            callback = object : DialogUtil.MultiButtonCallBack {
                override fun buttonOneClicked() {
                    super.buttonOneClicked()

                    showProgressCallback?.showProgress()
                    viewModel.startBreak()
                }
            }
        )
    }

    private fun showSendSurveyDialog() {
        goto = GOTO.SENDSURVEY
        if (!isAllowedToProceed())
            return

        val bindingDialogSurvey = DialogSendSurveryBinding.inflate(
            LayoutInflater.from(requiredContext),
            null,
            false
        )
        val dialogSurvey = Dialog(requiredContext)
        dialogSurvey.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(bindingDialogSurvey.root)
            window
                ?.setLayout(
                    CoordinatorLayout.LayoutParams.MATCH_PARENT,
                    CoordinatorLayout.LayoutParams.WRAP_CONTENT
                )
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(true)
            show()
        }
        bindingDialogSurvey.apply {
            editTextUserName.apply {
                heading("Name")
                inputMode(
                    EditTextInputMode.INPUT_TEXT
                )
            }
            editTextEmail.apply {
                heading("Email")
                inputMode(
                    EditTextInputMode.EMAIL
                )
                watchTextChange(
                    object : EditText.TextWatcher {
                        override fun textChanged(text: String) {
                            hideError()
                        }
                    }
                )
            }
            spinnerVia.apply {
                heading("Survey Via")
                addItems(
                    arrayListOf(
                        SpinnerData(
                            "Mobile",
                            SendSurveyVia.MOBILE
                        ),
                        SpinnerData(
                            "Email",
                            SendSurveyVia.WEB
                        ),
                        SpinnerData(
                            "QR Code",
                            SendSurveyVia.QR_CODE
                        )
                    ),
                    object : Spinner.OnItemSelectedListener {
                        override fun <T> selected(item: SpinnerData<T>) {
                            selectedSendSurveyVia = item.data as SendSurveyVia
                        }
                    }
                )
                selectedSendSurveyVia = SendSurveyVia.MOBILE
            }
            buttonActionOne.apply {
                text = "Submit"
                setOnClickListener {
                    if (
                        bindingDialogSurvey.editTextEmail.getText().isEmpty()
                    ) {
                        bindingDialogSurvey.editTextEmail.showError(
                            "Enter email"
                        )
                    } else if (
                        !Patterns.EMAIL_ADDRESS.matcher(
                            bindingDialogSurvey.editTextEmail.getText()
                        ).matches()
                    ) {
                        bindingDialogSurvey.editTextEmail.showError(
                            "Invalid email"
                        )
                    } else {
                        dialogSurvey.dismiss()
                        showProgressCallback?.showProgress()
                        viewModel.sendSurvey(
                            name = if (bindingDialogSurvey.editTextUserName.getText()
                                    .isEmpty()
                            ) null else bindingDialogSurvey.editTextUserName.getText(),
                            email = bindingDialogSurvey.editTextEmail.getText(),
                            sendSurveyVia = selectedSendSurveyVia
                        )
                    }
                }
            }
            buttonActionTwo.apply {
                text = "Cancel"
                setOnClickListener {
                    dialogSurvey.dismiss()
                }
            }
            appcompatImageViewQRCode.visibility = View.GONE
            buttonActionCloseQrCode.visibility = View.GONE
        }
    }

    private fun showQRCode(url: String) {
        val bindingDialogSurvey = DialogSendSurveryBinding.inflate(
            LayoutInflater.from(requiredContext),
            null,
            false
        )
        val dialogSurvey = Dialog(requiredContext)
        dialogSurvey.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(bindingDialogSurvey.root)
            window
                ?.setLayout(
                    CoordinatorLayout.LayoutParams.MATCH_PARENT,
                    CoordinatorLayout.LayoutParams.WRAP_CONTENT
                )
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(true)
            show()
        }
        bindingDialogSurvey.apply {
            editTextUserName.apply {
                visibility = View.GONE
            }
            editTextEmail.apply {
                visibility = View.GONE
            }
            spinnerVia.apply {
                visibility = View.GONE
            }
            buttonActionOne.apply {
                visibility = View.GONE
            }
            buttonActionTwo.apply {
                visibility = View.GONE
            }
            appcompatImageViewQRCode.setImageBitmap(
                QRCode.from(url).bitmap()
            )
            buttonActionCloseQrCode.setOnClickListener {
                dialogSurvey.dismiss()
            }
        }
    }

    private fun showVehicleList() {
        showProgressCallback?.showProgress()
        showVehiclesToChoose = true
        viewModel.getVehicleList()
    }

    private fun gotoAdminSharedDoc(url: String) {
        CustomTabsIntent.Builder().apply {
            setToolbarColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.colorPrimary,
                    null
                )
            )
            setShowTitle(true)
            setStartAnimations(
                requiredContext,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            setExitAnimations(
                requiredContext,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            CustomTabHelper().getPackageNameToUse(
                requiredContext,
                url
            )?.let {
                val customTabIntent = build()
                customTabIntent.apply {
                    intent.setPackage(it)
                    launchUrl(
                        requiredContext,
                        Uri.parse(url)
                    )
                }
            } ?: run {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url)
                    )
                )
            }
        }
    }
}
