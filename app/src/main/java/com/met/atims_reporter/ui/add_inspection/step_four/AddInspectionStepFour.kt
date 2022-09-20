package com.met.atims_reporter.ui.add_inspection.step_four

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.databinding.ActivityAddInspectionStepFourBinding
import com.met.atims_reporter.model.InspectionFinalRequest
import com.met.atims_reporter.model.ToolListResponceInsp
import com.met.atims_reporter.ui.add_crash_report.step_two.adapter.TakePictureAdapter
import com.met.atims_reporter.ui.add_inspection.step_four.adapter.AddInspectionStepToolsFourAdapter
import com.met.atims_reporter.ui.add_inspection.step_four.adapter.InspectionSummaryAdapter
import com.met.atims_reporter.ui.crash_report.CrashReportList
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.ui.inspection_list.InspectionList
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.ImagePickerUtil
import com.met.atims_reporter.util.UiUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import com.sagar.android.logutilmaster.LogUtil
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import permissions.dispatcher.*

@RuntimePermissions
class AddInspectionStepFour : AtimsSuperActivity(), KodeinAware {

    private lateinit var binding: ActivityAddInspectionStepFourBinding
    private lateinit var adapter: InspectionSummaryAdapter

    override val kodein: Kodein by kodein()
    private val logUtil: LogUtil by instance()
    private val provider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    private lateinit var image: String
    private lateinit var inspectionFinalRequest: InspectionFinalRequest
    private var uploadingSummaryImage: Boolean = false
    private var uploadingSummaryImageIndex: Int = 0
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapterInspectionImages: TakePictureAdapter
    private var uploadingInsurance = false
    private var uploadingRegistration = false
    private var uploadingStateInspection = false
    private var uploadingLicensePlate = false
    private var doneUploadingInsurance = false
    private var doneUploadingRegistration = false
    private var doneUploadingStateInspection = false
    private var doneUploadingLicensePlate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_add_inspection_step_four)
        binding.context = this
        setPageTitle("INSPECTIONS - SUMMARY")
        enableBackButton()
        willHandleBackNavigation()

        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            provider
        )
            .get(ViewModel::class.java)
        initView()
        bindToViewModel()
    }


    private fun initView() {
        setAdapter()
        binding.btnSubmit.setOnClickListener {
            validateAndSubmit()
        }
        binding.recyclerViewInspectionImages.apply {
            layoutManager = LinearLayoutManager(
                this@AddInspectionStepFour,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapterInspectionImages = TakePictureAdapter(
                object : TakePictureAdapter.Callback {
                    override fun takePicture() {
                        initiatePictureCapture()
                    }
                }
            )
            adapterInspectionImages.max = 6
            adapter = adapterInspectionImages
        }
    }

    fun initiatePictureCapture() {
        takeProfilePictureWithPermissionCheck()
    }

    override fun goBack() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.back_alert_title_txt)
        builder.setMessage(R.string.back_alert_txt)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(Html.fromHtml("<font color='#FFC405'>Yes</font>")) { dialog, arg1 ->
            viewModel.giveRepository().getHomeGridItems()
            startActivity(Intent(this, InspectionList::class.java))
            finish()
        }
        builder.setNegativeButton(Html.fromHtml("<font color='#FFC405'>No</font>")) { dialog, arg1 ->

        }
        builder.create()
        builder.show()
    }

    private fun setAdapter() {
        intent.getStringExtra(KeyWordsAndConstants.DATA)?.let {
            inspectionFinalRequest = fromJson(it)

            logUtil.logV("the data is  : $inspectionFinalRequest")

            adapter = InspectionSummaryAdapter(inspectionFinalRequest.inspectionsVehicleQuestions)

            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter

            val toolBroken = ArrayList<ToolListResponceInsp>()
            val toolMissing = ArrayList<ToolListResponceInsp>()
            val toolPresent = ArrayList<ToolListResponceInsp>()

            binding.textViewTotalPresent.visibility = View.GONE
            binding.textViewTotalMissing.visibility = View.GONE
            binding.textViewTotalBroken.visibility = View.GONE

            for (i in 0 until inspectionFinalRequest.inspectionsTools.size) {
                if (inspectionFinalRequest.inspectionsTools.get(i).tool_status.equals("missing")) {
                    toolMissing.add(inspectionFinalRequest.inspectionsTools.get(i))
                    binding.textViewTotalMissing.visibility = View.VISIBLE
                } else if (inspectionFinalRequest.inspectionsTools.get(i).tool_status.equals("present")) {
                    binding.textViewTotalPresent.visibility = View.VISIBLE
                    toolPresent.add(inspectionFinalRequest.inspectionsTools.get(i))
                } else if (inspectionFinalRequest.inspectionsTools.get(i).tool_status.equals("broken")) {
                    binding.textViewTotalBroken.visibility = View.VISIBLE
                    toolBroken.add(inspectionFinalRequest.inspectionsTools.get(i))
                }
            }

            setAdapterToolBroken(toolBroken)
            setAdapterToolMissing(toolMissing)
            setAdapterToolPresent(toolPresent)
        }
    }

    private fun setAdapterToolBroken(toolList: ArrayList<ToolListResponceInsp>) {
        val toolAdapter = AddInspectionStepToolsFourAdapter(toolList)
        binding.reToolBroken.setHasFixedSize(true)
        binding.reToolBroken.layoutManager = LinearLayoutManager(this)
        binding.reToolBroken.adapter = toolAdapter
        binding.reToolBroken.isNestedScrollingEnabled = false
    }

    private fun setAdapterToolMissing(toolList: ArrayList<ToolListResponceInsp>) {
        val toolAdapter = AddInspectionStepToolsFourAdapter(toolList)
        binding.reToolMisssing.setHasFixedSize(true)
        binding.reToolMisssing.layoutManager = LinearLayoutManager(this)
        binding.reToolMisssing.adapter = toolAdapter
        binding.reToolMisssing.isNestedScrollingEnabled = false
    }

    private fun setAdapterToolPresent(toolList: ArrayList<ToolListResponceInsp>) {
        val toolAdapter = AddInspectionStepToolsFourAdapter(toolList)
        binding.reToolPresent.setHasFixedSize(true)
        binding.reToolPresent.layoutManager = LinearLayoutManager(this)
        binding.reToolPresent.adapter = toolAdapter
        binding.reToolPresent.isNestedScrollingEnabled = false
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
        adapterInspectionImages.pictureCaptured(image)
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataPreOpsImageUpload.observe(
            this,
            Observer<Event<ArrayList<String>>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        val name = it.getContent()!![0]
                        if (uploadingInsurance) {
                            doneUploadingInsurance = true
                            uploadingInsurance = false
                            inspectionFinalRequest.inspectionIns = name
                            initiateUploading()
                        } else if (uploadingRegistration) {
                            doneUploadingRegistration = true
                            uploadingRegistration = false
                            inspectionFinalRequest.registrationImg = name
                            initiateUploading()
                        } else if (uploadingStateInspection) {
                            doneUploadingStateInspection = true
                            uploadingStateInspection = false
                            inspectionFinalRequest.inspectionState = name
                            initiateUploading()
                        } else if (uploadingLicensePlate) {
                            doneUploadingLicensePlate = true
                            uploadingLicensePlate = false
                            inspectionFinalRequest.inspectionPlate = name
                            initiateUploading()
                        } else {
                            if (uploadingSummaryImageIndex < adapterInspectionImages.getImages().size) {
                                uploadingSummaryImage = false
                                inspectionFinalRequest.inspectionImg.add(name)
                                uploadingSummaryImageIndex++
                                if (uploadingSummaryImageIndex != adapterInspectionImages.getImages().size)
                                    viewModel.uploadInspectionImage(
                                        adapterInspectionImages.getImages()[uploadingSummaryImageIndex]
                                    )
                                else
                                    uploadingPreOpsQuestionsImage(name)
                            } else {
                                uploadingPreOpsQuestionsImage(name)
                            }
                        }
                    }
                }
            }
        )

        viewModel.mediatorLiveDataPreOpsImageUploadError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )

        viewModel.mediatorLiveDataFinalPreOps.observe(
            this,
            Observer<Event<String>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        it.readContent()
                        hideProgress()
                        viewModel.giveRepository().getHomeGridItems()
                        showMessageWithOneButton(
                            message = "Your Inspection Report submitted successfully",
                            cancellable = false,
                            buttonText = "Okay",
                            callback = object : DialogUtil.CallBack {
                                override fun buttonClicked() {
                                    super.buttonClicked()
                                    endAllActivities()
                                    startActivity(
                                        UiUtil.clearStackAndStartNewActivity(
                                            Intent(
                                                this@AddInspectionStepFour,
                                                Dashboard::class.java
                                            )
                                        )
                                    )
                                    viewModel.giveRepository().clearInspectionData()
                                    finish()
                                }
                            }
                        )
                    }
                }
            }
        )

        viewModel.mediatorLiveDataFinalPreOpsError.observe(
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

    private fun validateAndSubmit() {
        if (
            binding.edtComments.text.toString().isEmpty()
        ) {
            showMessageInDialog("Please provide comments.")
            return
        }
        if (
            !this::image.isInitialized
        ) {
            showMessageInDialog("Please provide image")
            return
        }

        showProgress()

        initiateUploading()
    }

    private fun initiateUploading() {
        if (!doneUploadingInsurance) {
            uploadingInsurance = true
            viewModel.uploadInspectionImage(
                inspectionFinalRequest.inspectionIns
            )
        } else if (!doneUploadingRegistration) {
            uploadingRegistration = true
            viewModel.uploadInspectionImage(
                inspectionFinalRequest.registrationImg
            )
        } else if (!doneUploadingStateInspection) {
            uploadingStateInspection = true
            viewModel.uploadInspectionImage(
                inspectionFinalRequest.inspectionState
            )
        } else if (!doneUploadingLicensePlate) {
            uploadingLicensePlate = true
            viewModel.uploadInspectionImage(
                inspectionFinalRequest.inspectionPlate
            )
        } else {
            inspectionFinalRequest.inspectionComment = binding.edtComments.text.toString()
            uploadingSummaryImage = true
            uploadingSummaryImageIndex = 0
            viewModel.uploadInspectionImage(
                adapterInspectionImages.getImages()[uploadingSummaryImageIndex]
            )
        }
    }

    private var preOpsImageOnProcess = -1

    private fun uploadingPreOpsQuestionsImage(name: String) {
        if (preOpsImageOnProcess != -1) {
            inspectionFinalRequest.inspectionsVehicleQuestions[preOpsImageOnProcess].image = name
        }
        preOpsImageOnProcess = -1
        inspectionFinalRequest.inspectionsVehicleQuestions.forEachIndexed { index, question ->
            if (preOpsImageOnProcess == -1)
                question.imagePath?.let {
                    if (question.image == "") {
                        preOpsImageOnProcess = index
                        viewModel.uploadInspectionImage(it)
                    }
                }
        }

        if (preOpsImageOnProcess == -1)
            uploadFinalData()
    }

    private fun uploadFinalData() {
        viewModel.uploadFinalInspectionData(inspectionFinalRequest)
    }
}