package com.met.atims_reporter.ui.add_fuel_report

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.core.KeyWordsAndConstants.OPERATION_MODE
import com.met.atims_reporter.core.KeyWordsAndConstants.REQUEST_CHECK_SETTINGS_FOR_LOCATION
import com.met.atims_reporter.databinding.ActivityAddFuelReportBinding
import com.met.atims_reporter.enums.OperationMode
import com.met.atims_reporter.model.FuelListDetails
import com.met.atims_reporter.model.FuelType
import com.met.atims_reporter.ui.fuel_report.FuelReport
import com.met.atims_reporter.util.DateUtil
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.ImagePickerUtil
import com.met.atims_reporter.util.NumberUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import com.met.atims_reporter.widget.edittext.EditText
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
class AddFuelReport : AtimsSuperActivity(), KodeinAware {

    private lateinit var binding: ActivityAddFuelReportBinding
    override val kodein: Kodein by kodein()
    private val logUtil: LogUtil by instance<LogUtil>()
    private val provider: ViewModelProvider by instance<ViewModelProvider>()
    private lateinit var viewModel: ViewModel
    private lateinit var image: String
    private var receipt: File? = null
    private lateinit var fuelType: FuelType
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var TankType = ""
    private var costPerGallon = "0"
    private var fuelQty = "0"
    private var wentForCostPerGallonRounding = false
    private var isPictureOfReceipt = true
    private var willChangetotCost=true
    private var willChangeCostPerLitre =true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_add_fuel_report)

        setPageTitle("ADD FUEL REPORT test")
        enableBackButton()
        willHandleBackNavigation()

        binding.clAddReceipt.setOnClickListener {
            initiatePictureCapture()
        }

        binding.tvReTakePicture.setOnClickListener {
            initiatePictureCapture()
        }

        binding.btnSubmit.setOnClickListener {
            startLocationIfNotEnabled()
        }


        binding.textVehicelId.apply {
            heading("Vehicle ID")
            editTextWidthPercent(60)
            editable(false)
        }


        binding.spinnerFuelTypes.apply {
            heading("Fuel Type")
            spinnerWidthPercent(60)
        }

        binding.editTextOdoMeterReadings.apply {
            heading("Odometer Readings")
            editTextWidthPercent(60)
            inputMode(
                EditTextInputMode.DECIMAL
            )
        }

        binding.textCostPerLtr.apply {
            heading("Price/Gallon")
            getDrawable(
                R.drawable.dollar
            )?.let { drawable ->
                drawableStart(drawable)
            }
            editTextWidthPercent(60)
            inputMode(
                EditTextInputMode.DECIMAL
            )
            watchTextChange(
                object : EditText.TextWatcher {
                    override fun textChanged(text: String) {
                        /*if (text != "" && !wentForCostPerGallonRounding) {
//                            binding.textCostPerLtr.setText(NumberUtil.roundToTwoDecimal(text.toFloat()))
                            wentForCostPerGallonRounding = true
                            return
                        } else
                            costPerGallon = text
                        wentForCostPerGallonRounding = false*/
                        costPerGallon = text
                        if (fuelQty != "" && costPerGallon != "" && willChangetotCost) {
                            val totalCost = fuelQty.toDouble() * costPerGallon.toDouble()
                            val totCost = NumberUtil.roundToThreeDecimal(totalCost.toFloat())
                            binding.textTotalCost.setText(totCost)
                            willChangeCostPerLitre=true
                            willChangetotCost = false
                        }
                    }
                }
            )
            registerForFocusChange {
                try {
                    if (!it && binding.textCostPerLtr.getText() != "")
                        binding.textCostPerLtr.setText(
                            NumberUtil.roundToThreeDecimal(
                                binding.textCostPerLtr.getText().toFloat()
                            )
                        )
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }

        binding.edFuelQuantity.apply {
            heading("Fuel Quantity")
            editTextWidthPercent(60)
            inputMode(
                EditTextInputMode.DECIMAL
            )
            watchTextChange(
                object : EditText.TextWatcher {
                    override fun textChanged(text: String) {
                        try {
                            fuelQty = text
                            willChangetotCost = true
                            willChangeCostPerLitre = true
                            if (fuelQty != "" && costPerGallon != "") {
                                var totalCost = fuelQty.toDouble() * costPerGallon.toDouble()
                                binding.textTotalCost.setText(NumberUtil.roundToThreeDecimal(totalCost.toFloat()))
                            } /*else {
                                binding.textTotalCost.setText("0")
                            }*/
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }
                }
            )
        }

        binding.textTotalCost.apply {
            heading("Total Cost")
            editable(true)
            getDrawable(
                R.drawable.dollar
            )?.let { drawable ->
                drawableStart(drawable)
            }
            editTextWidthPercent(60)
            inputMode(
                EditTextInputMode.DECIMAL
            )

            watchTextChange(object:EditText.TextWatcher{
                override fun textChanged(text: String) {
                    try {
                        val totalCost = text
                        if(fuelQty!="" && totalCost!="" && fuelQty.toDouble()!=0.0 && willChangeCostPerLitre) {
                            val costPGallon  = totalCost.toDouble() / fuelQty.toDouble()
                            costPerGallon   =  NumberUtil.roundToThreeDecimal(costPGallon.toFloat())
                            binding.textCostPerLtr.setText(costPerGallon)
                            willChangetotCost = false
                            willChangeCostPerLitre = true
                        }/*else{
                           // binding.textCostPerLtr.setText(text)
                        }*/
                    }catch (ex:Exception){
                        ex.printStackTrace()
                    }
                }

            }

            )
        }




        binding.spinnerTankType.apply {
            heading("Fuel for Truck/Can?")
            spinnerWidthPercent(60)
            TankType = "Tank"
            addItems(
                arrayListOf(
                    SpinnerData(
                        "Tank", "Tank"
                    ),
                    SpinnerData(
                        "Can", "Can"
                    )

                ),
                object : Spinner.OnItemSelectedListener {
                    override fun <T> selected(item: SpinnerData<T>) {
                        TankType = item.data as String
                    }
                }
            )
        }

        binding.edtComments.apply {
            heading("Note")
            inputMode(
                EditTextInputMode.INPUT_TEXT_MULTI_LINE,
                MultiLineConfig(
                    3, 5
                )
            )
            mandatory(false)
        }

        binding.spinnerReceiptOrPump.apply {
            heading("Upload picture of")
            spinnerWidthPercent(60)
            addItems(
                arrayListOf(
                    SpinnerData(
                        "Receipt",
                        1
                    ),
                    SpinnerData(
                        "Pump",
                        2
                    )
                ),
                object : Spinner.OnItemSelectedListener {
                    override fun <T> selected(item: SpinnerData<T>) {
                        isPictureOfReceipt = item.data as Int == 1
                    }
                }
            )
        }

        viewModel = androidx.lifecycle.ViewModelProvider(this, provider)
            .get(ViewModel::class.java)

        bindToViewModel()

        showProgress(1)
        viewModel.getFuelTypes()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getLocationPermissionWithPermissionCheck()
        binding.textVehicelId.setText(
            viewModel.giveRepository().giveVehicleIdToShow()
        )

        tryPreFillingData()
    }


    private fun bindToViewModel() {

        viewModel.mediatorLiveDataVehiclesError.observe(
            this,
            Observer<Event<com.met.atims_reporter.util.model.Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        showMessageWithOneButton(
                            message = "We are unable to get vehicles. Please try again later",
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
        )
        viewModel.mediatorLiveDataFuelTypes.observe(
            this,
            Observer<Event<ArrayList<FuelType>>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        val list: ArrayList<SpinnerData<FuelType>> = ArrayList()
                        it.getContent()!!.forEach { item ->
                            list.add(
                                SpinnerData(
                                    item.name,
                                    item
                                )
                            )
                        }
                        binding.spinnerFuelTypes.addItems(
                            list,
                            object : Spinner.OnItemSelectedListener {
                                override fun <T> selected(item: SpinnerData<T>) {
                                    Log.i("FuleType", item.toShow)
                                    fuelType = item.data as FuelType
                                }
                            }
                        )
                    }
                }
            }
        )
        viewModel.mediatorLiveDataFuelTypesError.observe(
            this,
            Observer<Event<com.met.atims_reporter.util.model.Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        showMessageWithOneButton(
                            message = "We are unable to get some data. Please try again later",
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
        )

        viewModel.mediatorLiveDataAddFuelReport.observe(
            this,
            Observer<Event<com.met.atims_reporter.util.model.Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        it.readContent()
                        hideProgress()
                        showMessageWithOneButton(
                            message = "Your Fuel Report submitted successfully",
                            cancellable = false,
                            buttonText = "Okay",
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
        viewModel.mediatorLiveDataAddFuelReportError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        showMessageWithOneButton(
                            message = it.getContent()!!.getMessageToShow(),
                            cancellable = false,
                            buttonText = "Okay",
                            callback = object : DialogUtil.CallBack {
                                override fun buttonClicked() {
                                    super.buttonClicked()
                                }
                            }
                        )
                    }
                }
            }
        )
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
            addFuelReportAPICalled()
        }

        task.addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(
                        this,
                        REQUEST_CHECK_SETTINGS_FOR_LOCATION
                    )
                } catch (sendEx: SendIntentException) {
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
    private fun addFuelReportAPICalled() {
        if (!validate())
            return

        var totalCost =
            binding.edFuelQuantity.getText().toDouble() * binding.textCostPerLtr.getText()
                .toDouble()
        binding.textTotalCost.setText(totalCost.toString())

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener {
                it?.let { location ->
                    showProgress()
                    viewModel.AddFuelReportApi(
                        vehicle_id = viewModel.giveRepository().giveVehicleId(),
                        cost_per_galon = binding.textCostPerLtr.getText(),
                        total_cost = binding.textTotalCost.getText(),
                        fuel_quantity = binding.edFuelQuantity.getText(),
                        refueling_date = DateUtil.getDateStringForServerMDY(),
                        refueling_time = DateUtil.getTimeStringForServer(),
                        fuel_type = fuelType.name,
                        odo_meter_reading = binding.editTextOdoMeterReadings.getText(),
                        fuel_taken_tank = if (TankType == "Tank") binding.edFuelQuantity.getText() else "",
                        fuel_taken_can = if (TankType == "Can") binding.edFuelQuantity.getText() else "",
                        latitude = location.latitude.toString(),
                        longitude = location.longitude.toString(),
                        note = binding.edtComments.getText(),
                        status = "1",
                        image = receipt,
                        isReceipt = isPictureOfReceipt
                    )
                }
            }
    }

    override fun goBack() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.back_alert_title_txt)
        builder.setMessage(R.string.back_alert_txt)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(Html.fromHtml("<font color='#FFC405'>Yes</font>")) { dialog, arg1 ->
            viewModel.giveRepository().getHomeGridItems()
            startActivity(
                Intent(this, FuelReport::class.java)
            )
            finish()
        }
        builder.setNegativeButton(Html.fromHtml("<font color='#FFC405'>No</font>")) { dialog, arg1 ->

        }
        builder.create()
        builder.show()
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
            orientation = Options.SCREEN_ORIENTATION_PORTRAIT
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
    fun getLocationPermission() {
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
        if (requestCode == KeyWordsAndConstants.REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            intentData?.let {
                it.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.let { images ->
                    logUtil.logV("got image $images")
                    image = images[0]
                    this.receipt = File(image)
                    setImageToUI()
                }
            }
        }
        if (requestCode == REQUEST_CHECK_SETTINGS_FOR_LOCATION) {
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

    private fun setImageToUI() {
        binding.imageCameraReceipt.visibility = View.VISIBLE
        binding.tvReTakePicture.visibility = View.VISIBLE
        binding.imageCameraReceipt.setImage(
            File(
                image
            )
        )
    }

    private fun validate(): Boolean {
        if (binding.editTextOdoMeterReadings.getText().isEmpty()) {
            showMessageInDialog("Please provide Odometer Readings.")
            return false
        } else if (binding.editTextOdoMeterReadings.getText().toString().trim().toDouble() < 1.0) {
            showMessageInDialog("Please provide valid Odometer Readings.")
            return false
        }

        if (binding.textCostPerLtr.getText().isEmpty()) {
            showMessageInDialog("Please provide Cost/Gallon.")
            return false
        } else if (binding.textCostPerLtr.getText().toString().toDouble() < 1.0) {
            showMessageInDialog("Please provide valid Cost/Gallon.")
            return false
        }

        if (binding.edFuelQuantity.getText().isEmpty()) {
            showMessageInDialog("Please provide Fuel Quantity.")
            return false
        } else if (binding.edFuelQuantity.getText().toString().trim().toDouble() < 1.0) {
            showMessageInDialog("Please provide valid Fuel Quantity.")
            return false
        }

        if (binding.textTotalCost.getText().isEmpty()) {
            showMessageInDialog("Please provide Total Cost.")
            return false
        } else if (binding.textTotalCost.getText().toString().trim().toDouble() < 1.0) {
            showMessageInDialog("Please provide valid Total Cost.")
            return false
        }

        /*
        if (
            TankType == "Tank" && binding.edFuelTakenInTank.getText().isEmpty()
        ) {
            showMessageInDialog("Please provide Fuel Taken in Tank.")
            return false
        }

        if (
            TankType == "Can" && binding.edFuelTakenInCan.getText().isEmpty()
        ) {
            showMessageInDialog("Please provide Fuel Taken in Can.")
            return false
        }

        if (
            TankType == "Tank" && binding.edFuelTakenInTank.getText()
                .toFloat() > binding.edFuelQuantity.getText().toFloat()
        ) {
            showMessageInDialog("Fuel taken can not be greater then fuel quantity.")
            return false
        }

        if (
            TankType == "Can" && binding.edFuelTakenInCan.getText()
                .toFloat() > binding.edFuelQuantity.getText().toFloat()
        ) {
            showMessageInDialog("Fuel taken can not be greater then fuel quantity.")
            return false
        }*/

        /*if (
            binding.edtComments.getText().isEmpty()
        ) {
            showMessageInDialog("Please provide a Note.")
            return false
        }*/

        if (receipt == null && !binding.ckNoReceipt.isChecked) {
            showMessageInDialog("Please provide Receipt or pump Image.")
            return false
        }
        return true
    }

    private fun tryPreFillingData() {
        intent.getSerializableExtra(OPERATION_MODE)?.let {
            if (
                (it as OperationMode) == OperationMode.EDIT
            ) {
                val fuelListDetails: FuelListDetails = fromJson(intent.getStringExtra(DATA)!!)
                binding.editTextOdoMeterReadings.setText(
                    fuelListDetails.odo_meter_reading
                )
            }
        }
    }
}