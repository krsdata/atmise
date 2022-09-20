package com.met.atims_reporter.ui.add_maintenance_report

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.databinding.ActivityAddMaintenanceReportBinding
import com.met.atims_reporter.model.*
import com.met.atims_reporter.ui.crash_report.CrashReportList
import com.met.atims_reporter.ui.maintenance_report.MaintenanceReport
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.ImagePickerUtil
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
import java.io.File

@RuntimePermissions
class AddMaintenanceReport : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()

    private val logUtil: LogUtil by instance()
    private val provider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    private lateinit var binding: ActivityAddMaintenanceReportBinding
    private lateinit var image: String
    private lateinit var selectedState: StateList
    private lateinit var selectedMaintenanceType: MaintenanceRequestTypeList
    private lateinit var selectedVehicle: String
    private lateinit var selectedVinNo: String
    private lateinit var selectedServiceType: ServiceType
    private lateinit var selectedVendor: Vendor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_add_maintenance_report)
        binding.context = this

        setPageTitle("Add Maintenance Request")
        enableBackButton()
        willHandleBackNavigation()
        setUpUI()

        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            provider
        )
            .get(ViewModel::class.java)
        showProgress()
        bindToViewModel()
    }

    override fun goBack() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.back_alert_title_txt)
        builder.setMessage(R.string.back_alert_txt)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(Html.fromHtml("<font color='#FFC405'>Yes</font>")) { dialog, arg1 ->
            viewModel.giveRepository().getHomeGridItems()
            startActivity(
                Intent(this, MaintenanceReport::class.java)
            )
            finish()
        }
        builder.setNegativeButton(Html.fromHtml("<font color='#FFC405'>No</font>")) { dialog, arg1 ->

        }
        builder.create()
        builder.show()
    }

    private fun setUpUI() {
        binding.maintenanceRequestTypeSpinner.apply {
            heading("Request Type")
            spinnerWidthPercent(60)
        }
        binding.stateSpinner.apply {
            heading("Alias State")
            spinnerWidthPercent(60)
        }
        binding.contractEditText.apply {
            heading("Contract")
            editTextWidthPercent(60)
        }
        binding.vehicleSpinner.apply {
            heading("Vehicle")
            editable(false)
            spinnerWidthPercent(60)
        }
        binding.vinNoEditText.apply {
            heading("Vehicle VIN No")
            editable(false)
            editTextWidthPercent(60)
        }
        binding.editTextDate.apply {
            heading("Date")
            editTextWidthPercent(60)
            inputMode(
                EditTextInputMode.DATE
            )
        }
        binding.spinnerServiceType.apply {
            heading("Service Type")
            spinnerWidthPercent(60)
        }
        binding.editTextDescriptionOfRepair.apply {
            heading("Description of Repair")
            inputMode(
                EditTextInputMode.INPUT_TEXT_MULTI_LINE,
                MultiLineConfig(
                    3, 5
                )
            )
        }
        binding.vendorSpinner.apply {
            heading("Vendor")
            spinnerWidthPercent(60)
        }
        binding.editTextMileage.apply {
            editTextWidthPercent(60)
            heading("Mileage")
            inputMode(
                EditTextInputMode.NUMBER
            )
        }
        binding.editTextServiceCost.apply {
            editTextWidthPercent(60)
            heading("Service Cost")
            getDrawable(
                R.drawable.dollar
            )?.let { drawable ->
                drawableStart(drawable)
            }
            inputMode(
                EditTextInputMode.DECIMAL
            )
        }
        binding.editTextLabourHours.apply {
            editTextWidthPercent(60)
            heading("Labor Hours")
            inputMode(
                EditTextInputMode.DECIMAL
            )
        }
        binding.editTextLabourMin.apply {
            editTextWidthPercent(60)
            heading("Labor Min")
            inputMode(
                EditTextInputMode.DECIMAL
            )
        }
        binding.editTextNotes.apply {
            heading("Note")
            mandatory(false)
            inputMode(
                EditTextInputMode.INPUT_TEXT_MULTI_LINE,
                MultiLineConfig(
                    3, 5
                )
            )
        }
    }

    fun initiatePictureCapture() {
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
            reqCode = KeyWordsAndConstants.REQUEST_CODE_PICK_IMAGE,
            takePictureFrom = Options.TakePictureFrom.GALLERY_AND_CAMERA,
            orientation = Options.SCREEN_ORIENTATION_LANDSCAPE
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
                    image = images[0]
                    setImageToUI()
                }
            }
        }
    }

    private fun setImageToUI() {
        binding.imageCamera.visibility = View.VISIBLE
        binding.tvReTakePicture.visibility = View.VISIBLE
        binding.imageCamera.setImage(
            File(
                image
            )
        )
    }

    fun submit() {
        if (binding.contractEditText.getText().isEmpty()) {
            showMessageInDialog("Please provide contract information")
            return
        }

        if (binding.editTextDate.getText().isEmpty()) {
            showMessageInDialog("Please provide date")
            return
        }
        if (binding.editTextDescriptionOfRepair.getText().isEmpty()) {
            showMessageInDialog("Please provide description of repair")
            return
        }
        if (binding.editTextMileage.getText().isEmpty()) {
            showMessageInDialog("Please provide mileage")
            return
        } else if (binding.editTextMileage.getText().toString().trim().toDouble() < 1.0) {
            showMessageInDialog("Please provide valid mileage")
            return
        }

        if (binding.editTextServiceCost.getText().isEmpty()) {
            showMessageInDialog("Please provide service cost")
            return
        } else if (binding.editTextServiceCost.getText().toString().trim().toDouble() < 1.0) {
            showMessageInDialog("Please provide valid service cost")
            return
        }

        if (binding.editTextLabourHours.getText().isEmpty()) {
            showMessageInDialog("Please provide labour hours")
            return
        }
        if (binding.editTextLabourMin.getText().isEmpty()) {
            showMessageInDialog("Please provide labour minutes")
            return
        }

        if (binding.editTextLabourHours.getText().toString().trim().toDouble() < 1.0 &&
            binding.editTextLabourMin.getText().toString().trim().toDouble() < 1.0) {
            showMessageInDialog("Please provide valid labour hours/minutes")
            return
        }
        if (binding.editTextLabourMin.getText().toString().toInt() > 59) {
            showMessageInDialog("Please provide valid labour minutes")
            return
        }

        showProgress()
        viewModel.addMaintenanceReport(
            if (this::image.isInitialized) image else "",
            viewModel.giveRepository().giveVehicleId(),
            selectedVinNo,
            selectedMaintenanceType.request_type_id,
            selectedState.state_id,
            selectedState.state_name,
            binding.contractEditText.getText(),
            binding.editTextDate.getText(),
            selectedServiceType.service_type_id,
            binding.editTextMileage.getText(),
            binding.editTextServiceCost.getText(),
            binding.editTextLabourHours.getText(),
            binding.editTextLabourMin.getText(),
            selectedVendor.vendor_id,
            selectedVendor.vendor_name,
            binding.editTextDescriptionOfRepair.getText(),
            binding.editTextNotes.getText(),
        )
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataStateList.observe(
            this,
            Observer<Event<ArrayList<StateList>>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        val items: ArrayList<SpinnerData<StateList>> = ArrayList()
                        it.getContent()!!.forEach { state ->
                            items.add(
                                SpinnerData(
                                    state.state_name,
                                    state
                                )
                            )
                        }
                        if (
                            items.size == 0
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
                            if (items.size > 0)
                                selectedState = items[0].data
                            binding.stateSpinner.addItems(
                                items,
                                object : Spinner.OnItemSelectedListener {
                                    override fun <T> selected(item: SpinnerData<T>) {
                                        selectedState = item.data as StateList
                                    }
                                }
                            )
                            binding.stateSpinner.select<SpinnerData<StateList>>(
                                toShowString = viewModel.giveRepository().giveStateName()
                            )
                        }
                    }
                }
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


        viewModel.mediatorLiveDataMaintenanceRequestTypeList.observe(
            this,
            Observer<Event<ArrayList<MaintenanceRequestTypeList>>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        val items: ArrayList<SpinnerData<MaintenanceRequestTypeList>> = ArrayList()
                        it.getContent()!!.forEach { MaintenanceRequestTypeList ->
                            items.add(
                                SpinnerData(
                                    MaintenanceRequestTypeList.request_name,
                                    MaintenanceRequestTypeList
                                )
                            )
                        }
                        if (
                            items.size == 0
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
                            if (items.size > 0)
                                selectedMaintenanceType = items[0].data
                            binding.maintenanceRequestTypeSpinner.addItems(
                                items,
                                object : Spinner.OnItemSelectedListener {
                                    override fun <T> selected(item: SpinnerData<T>) {
                                        selectedMaintenanceType = item.data as MaintenanceRequestTypeList
                                    }
                                }
                            )
                            binding.maintenanceRequestTypeSpinner.select<SpinnerData<MaintenanceRequestTypeList>>(
                                toShowString = viewModel.giveRepository().giveStateName()
                            )
                        }
                    }
                }
            }
        )
        viewModel.mediatorLiveDataAddMaintenanceReportError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )


        viewModel.mediatorLiveDataVendors.observe(
            this,
            Observer<Event<ArrayList<Vendor>>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        val items: ArrayList<SpinnerData<Vendor>> = ArrayList()
                        it.getContent()!!.forEach { Vendor ->
                            items.add(
                                SpinnerData(
                                    Vendor.vendor_name,
                                    Vendor
                                )
                            )
                        }
                        if (
                            items.size == 0
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
                            if (items.size > 0)
                                selectedVendor = items[0].data
                            binding.vendorSpinner.addItems(
                                items,
                                object : Spinner.OnItemSelectedListener {
                                    override fun <T> selected(item: SpinnerData<T>) {
                                        selectedVendor = item.data as Vendor
                                    }
                                }
                            )
                            binding.vendorSpinner.select<SpinnerData<Vendor>>(
                                toShowString = viewModel.giveRepository().giveStateName()
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

        viewModel.mediatorLiveDataVehicles.observe(
            this,
            Observer<Event<ArrayList<VehicleList>>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        val items: ArrayList<SpinnerData<String>> = ArrayList()
                        val vehicleVinNo: ArrayList<String> = ArrayList()
                        it.getContent()!!.forEach { vehicle ->

                        }
                        items.add(
                            SpinnerData(
                                viewModel.giveRepository().giveVehicleIdToShow(),
                                viewModel.giveRepository().giveVehicleIdToShow()
                            )
                        )
                        vehicleVinNo.add(
                            viewModel.giveRepository().giveVehicleVinNumberToShow()
                        )
                        if (
                            items.size == 0
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
                            if (items.size > 0){
                                selectedVehicle = items[0].data
                                selectedVinNo = vehicleVinNo[0]
                            }
                            binding.vehicleSpinner.addItems(
                                items,
                                object : Spinner.OnItemSelectedListener {
                                    override fun <T> selected(item: SpinnerData<T>) {
                                        selectedVehicle = item.data as String

                                        selectedVinNo = vehicleVinNo[0]
                                        binding.vinNoEditText.setText(selectedVinNo)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        )

        viewModel.mediatorLiveDataVehiclesError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataServiceTypes.observe(
            this,
            Observer<Event<ArrayList<ServiceType>>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        val items: ArrayList<SpinnerData<ServiceType>> = ArrayList()
                        it.getContent()!!.forEach { serviceType ->
                            items.add(
                                SpinnerData(
                                    serviceType.service_type,
                                    serviceType
                                )
                            )
                        }
                        if (
                            items.size == 0
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
                            if (items.size > 0)
                                selectedServiceType = items[0].data
                            binding.spinnerServiceType.addItems(
                                items,
                                object : Spinner.OnItemSelectedListener {
                                    override fun <T> selected(item: SpinnerData<T>) {
                                        selectedServiceType = item.data as ServiceType
                                    }
                                }
                            )
                        }
                    }
                }
            }
        )

        viewModel.mediatorLiveDataServiceTypesError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )

        viewModel.mediatorLiveDataAddMaintenanceReport.observe(
            this,
            Observer<Event<AddMaintenanceReportResponse>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        it.readContent()
                        showMessageWithOneButton(
                            message = "Added Maintenance Report.",
                            cancellable = false,
                            callback = object : DialogUtil.CallBack {
                                override fun buttonClicked() {
                                    super.buttonClicked()

                                    viewModel.giveRepository().getHomeGridItems()
                                    finish()
                                }
                            }
                        )
                    }
                }
            }
        )

        viewModel.mediatorLiveDataAddMaintenanceReportError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )
    }
}