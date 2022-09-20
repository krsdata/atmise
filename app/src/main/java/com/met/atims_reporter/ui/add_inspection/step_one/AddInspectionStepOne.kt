package com.met.atims_reporter.ui.add_inspection.step_one

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.databinding.ActivityAddInspectionStepOneBinding
import com.met.atims_reporter.model.InspectionFinalRequest
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.ui.add_inspection.step_two.AddInspectionStepTwo
import com.met.atims_reporter.ui.inspection_list.InspectionList
import com.met.atims_reporter.util.DateUtil
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.ImagePickerUtil
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import permissions.dispatcher.*
import java.io.File
import java.util.*

@RuntimePermissions
class AddInspectionStepOne : AtimsSuperActivity(), KodeinAware {

    enum class TakingPictureFor {
        INSURANCE,
        REGISTRATION,
        STATE_INSPECTION,
        LICENSE_PLATE
    }

    override val kodein: Kodein by kodein()

    private lateinit var binding: ActivityAddInspectionStepOneBinding
    private val repository: Repository by instance()
    private var insuranceExpDate: String = ""
    private var registrationExpDate: String = ""
    private var stateInspectionExpDate: String = ""
    private lateinit var takingPictureFor: TakingPictureFor
    private var imageInsurance: String = ""
    private var imageRegistration = ""
    private var imageStateInspection = ""
    private var imageLicense: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_add_inspection_step_one)
        binding.context = this
        setPageTitle("ADD INSPECTIONS")
        enableBackButton()
        willHandleBackNavigation()
        initView()

        binding.clAddInsurance.setOnClickListener {
            takingPictureFor = TakingPictureFor.INSURANCE
            initiatePictureCapture()
        }
        binding.tvReTakePictureInsurance.setOnClickListener {
            binding.clAddInsurance.performClick()
        }

        binding.clAddRegistration.setOnClickListener {
            takingPictureFor = TakingPictureFor.REGISTRATION
            initiatePictureCapture()
        }
        binding.tvReTakePictureRegistration.setOnClickListener {
            binding.clAddRegistration.performClick()
        }

        binding.clAddStateInspection.setOnClickListener {
            takingPictureFor = TakingPictureFor.STATE_INSPECTION
            initiatePictureCapture()
        }
        binding.tvReTakePictureInspector.setOnClickListener {
            binding.clAddStateInspection.performClick()
        }

        binding.clAddReceipt.setOnClickListener {
            takingPictureFor = TakingPictureFor.LICENSE_PLATE
            initiatePictureCapture()
        }
        binding.tvReTakePictureLicensePlate.setOnClickListener {
            binding.clAddReceipt.performClick()
        }
    }

    override fun goBack() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.back_alert_title_txt)
        builder.setMessage(R.string.back_alert_txt)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(Html.fromHtml("<font color='#FFC405'>Yes</font>")) { dialog, arg1 ->
            startActivity(
                Intent(this, InspectionList::class.java)
            )
            finish()
        }
        builder.setNegativeButton(Html.fromHtml("<font color='#FFC405'>No</font>")) { dialog, arg1 ->

        }
        builder.create()
        builder.show()
    }

    private fun initView() {
        binding.textTruckIdValue.text = "#" + repository.giveVehicleIdToShow()
        binding.textInsuranceExpValue.setOnClickListener {
            binding.edtCurrentMiles.clearFocus()
            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    binding.textInsuranceExpValue.setText(
                        "${monthOfYear + 1}-$dayOfMonth-$year"
                    )
                    insuranceExpDate = "${monthOfYear + 1}-$dayOfMonth-$year"
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }

        binding.textRegistrationExpValue.setOnClickListener {
            binding.edtCurrentMiles.clearFocus()
            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    binding.textRegistrationExpValue.setText(
                        "${monthOfYear + 1}-$dayOfMonth-$year"
                    )
                    registrationExpDate = "${monthOfYear + 1}-$dayOfMonth-$year"
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }

        binding.textStateInspectionExpValue.setOnClickListener {
            binding.edtCurrentMiles.clearFocus()
            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    binding.textStateInspectionExpValue.setText(
                        "${monthOfYear + 1}-$dayOfMonth-$year"
                    )
                    stateInspectionExpDate = "${monthOfYear + 1}-$dayOfMonth-$year"
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }
    }

    fun moveToNext() {
        if (binding.edtCurrentMiles.text.toString().isEmpty()) {
            showMessageInDialog("Please provide Current Miles.")
            return
        } else if (binding.edtCurrentMiles.text.toString().trim().toDouble() < 1.0) {
            showMessageInDialog("Please provide valid Current Miles.")
            return
        }

        if (binding.textInsuranceExpValue.text.toString().isEmpty()) {
            showMessageInDialog("Please provide Insurance Exp")
            return
        }

        if (binding.textRegistrationExpValue.text.toString().isEmpty()) {
            showMessageInDialog("Please provide Registration Exp")
            return
        }

        if (imageInsurance == "") {
            showMessageInDialog("Please provide Insurance image")
            return
        }

        if (imageRegistration == "") {
            showMessageInDialog("Please provide Registration image")
            return
        }

        if (imageStateInspection == "") {
            showMessageInDialog("Please provide State Inspection image")
            return
        }

        if (imageLicense == "") {
            showMessageInDialog("Please provide License Plate image")
            return
        }

        val intent = Intent(this, AddInspectionStepTwo::class.java)
            .putExtra(
                KeyWordsAndConstants.DATA,
                toJson(
                    InspectionFinalRequest(
                        companyId = repository.getUserData()!!.company_id.toString(),
                        date = DateUtil.getDateStringForServerMDY(),
                        time = DateUtil.getTimeStringForServer(isTwelveHoursFormat = true),
                        inspectedBy = repository.getUserData()!!.user_id.toString(),
                        stateInspectionExp = stateInspectionExpDate,
                        regExp = registrationExpDate,
                        insuranceExp = insuranceExpDate,
                        userId = repository.getUserData()!!.user_id.toString(),
                        user_id = repository.getUserData()!!.user_id.toString(),
                        vehicleId = repository.giveVehicleId(),
                        vehicle_id = repository.giveVehicleId(),
                        odoMeterData = binding.edtCurrentMiles.text.toString(),
                        operatorShiftTimeDetailsId = repository.giveShiftId(),
                        inspectionIns = imageInsurance,
                        registrationImg = imageRegistration,
                        inspectionState = imageStateInspection,
                        inspectionPlate = imageLicense,
                        plate_number = ""
                    )
                )
            )
        startActivity(intent)
        overridePendingTransition(0, 0)
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
        binding.edtCurrentMiles.clearFocus()
        saveToShared()
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
        getDataFromSavedRepo()
        if (requestCode == KeyWordsAndConstants.REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            intentData?.let {
                it.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.let { images ->
                    when (
                        takingPictureFor
                        ) {
                        TakingPictureFor.INSURANCE -> {
                            imageInsurance = images[0]
                        }
                        TakingPictureFor.REGISTRATION -> {
                            imageRegistration = images[0]
                        }
                        TakingPictureFor.STATE_INSPECTION -> {
                            imageStateInspection = images[0]
                        }
                        TakingPictureFor.LICENSE_PLATE -> {
                            imageLicense = images[0]
                        }
                    }
                    setImageToUI()
                }
            }
        }
    }

    private fun setImageToUI() {
        when (
            takingPictureFor
            ) {
            TakingPictureFor.INSURANCE -> {
                binding.imageCameraReceiptInsurance.visibility = View.VISIBLE
                binding.tvReTakePictureInsurance.visibility = View.VISIBLE
                binding.imageCameraReceiptInsurance.setImage(
                    File(
                        imageInsurance
                    )
                )
            }
            TakingPictureFor.REGISTRATION -> {
                binding.imageCameraRegistration.visibility = View.VISIBLE
                binding.tvReTakePictureRegistration.visibility = View.VISIBLE
                binding.imageCameraRegistration.setImage(
                    File(
                        imageRegistration
                    )
                )
            }
            TakingPictureFor.STATE_INSPECTION -> {
                binding.imageCameraStateInspection.visibility = View.VISIBLE
                binding.tvReTakePictureInspector.visibility = View.VISIBLE
                binding.imageCameraStateInspection.setImage(
                    File(
                        imageStateInspection
                    )
                )
            }
            TakingPictureFor.LICENSE_PLATE -> {
                binding.imageCameraReceipt.visibility = View.VISIBLE
                binding.tvReTakePictureLicensePlate.visibility = View.VISIBLE
                binding.imageCameraReceipt.setImage(
                    File(
                        imageLicense
                    )
                )
            }
        }
    }

    private fun saveToShared() {
        repository.saveInspectionData(
            InspectionFinalRequest(
                companyId = repository.getUserData()!!.company_id.toString(),
                date = DateUtil.getDateStringForServerMDY(),
                time = DateUtil.getTimeStringForServer(isTwelveHoursFormat = true),
                inspectedBy = repository.getUserData()!!.user_id.toString(),
                stateInspectionExp = stateInspectionExpDate,
                regExp = registrationExpDate,
                insuranceExp = insuranceExpDate,
                userId = repository.getUserData()!!.user_id.toString(),
                user_id = repository.getUserData()!!.user_id.toString(),
                vehicleId = repository.giveVehicleId(),
                vehicle_id = repository.giveVehicleId(),
                odoMeterData = binding.edtCurrentMiles.text.toString(),
                operatorShiftTimeDetailsId = repository.giveShiftId(),
                inspectionIns = imageInsurance,
                registrationImg = imageRegistration,
                inspectionState = imageStateInspection,
                inspectionPlate = imageLicense,
                plate_number = "",
                takingPictureFor = takingPictureFor
            )
        )
    }

    private fun getDataFromSavedRepo() {
        repository.getSavedInspectionData()?.let { savedData ->
            stateInspectionExpDate = savedData.stateInspectionExp
            binding.textStateInspectionExpValue.text = savedData.stateInspectionExp

            registrationExpDate = savedData.regExp
            binding.textRegistrationExpValue.text = savedData.regExp

            insuranceExpDate = savedData.insuranceExp
            binding.textInsuranceExpValue.text = savedData.insuranceExp

            binding.edtCurrentMiles.setText(savedData.odoMeterData)

            imageInsurance = savedData.inspectionIns

            imageRegistration = savedData.registrationImg

            imageStateInspection = savedData.inspectionState

            imageLicense = savedData.inspectionPlate

            takingPictureFor = savedData.takingPictureFor

            if (imageInsurance != "") {
                binding.imageCameraReceiptInsurance.visibility = View.VISIBLE
                binding.imageCameraReceiptInsurance.setImage(
                    File(
                        imageInsurance
                    )
                )
            }

            if (imageRegistration != "") {
                binding.imageCameraRegistration.visibility = View.VISIBLE
                binding.imageCameraRegistration.setImage(
                    File(
                        imageRegistration
                    )
                )
            }

            if (imageStateInspection != "") {
                binding.imageCameraStateInspection.visibility = View.VISIBLE
                binding.imageCameraStateInspection.setImage(
                    File(
                        imageStateInspection
                    )
                )
            }

            if (imageLicense != "") {
                binding.imageCameraReceipt.visibility = View.VISIBLE
                binding.imageCameraReceipt.setImage(
                    File(
                        imageLicense
                    )
                )
            }
        }
    }
}