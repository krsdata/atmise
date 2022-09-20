package com.met.atims_reporter.ui.pre_ops.step_four

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.jakewharton.rxbinding3.view.clicks
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityPreOpsStepFourBinding
import com.met.atims_reporter.model.PreOpsFileUploadResponse
import com.met.atims_reporter.model.PreOpsFinalRequest
import com.met.atims_reporter.model.ToolListResponce
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.ui.pre_ops.step_four.adapter.PreOpsSummaryAdapter
import com.met.atims_reporter.ui.pre_ops.step_four.adapter.PreOpsToolsAadpter
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
import java.io.File

@RuntimePermissions
class PreOpsStepFour : AtimsSuperActivity(), KodeinAware {

    private lateinit var binding: ActivityPreOpsStepFourBinding
    private lateinit var adapter: PreOpsSummaryAdapter
    override val kodein: Kodein by kodein()
    private val logUtil: LogUtil by instance<LogUtil>()
    private val provider: ViewModelProvider by instance<ViewModelProvider>()
    private lateinit var viewModel: ViewModel
    private lateinit var image: String
    private lateinit var preOpsFinalRequest: PreOpsFinalRequest
    private var uploadingSummaryImage: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_pre_ops_step_four)
        binding.context = this
        setPageTitle("PRE OPS - SUMMARY")
        enableBackButton()
        initView()

        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            provider
        )
            .get(ViewModel::class.java)
        bindToViewModel()

        viewModel.giveRepository().checkIfLoggedIn()
    }

    private fun initView() {
        setAdapter()
        binding.btnSubmit.setOnClickListener {
            validateAndSubmit()
        }
        binding.clImageView.setOnClickListener {
            initiatePictureCapture()
        }
        binding.tvReTakePicture.setOnClickListener {
            initiatePictureCapture()
        }
    }

    fun initiatePictureCapture() {
        takeProfilePictureWithPermissionCheck()
    }

    private fun setAdapter() {
        intent.getStringExtra(DATA)?.let {
            preOpsFinalRequest = fromJson(it)

            logUtil.logV("the data is  : $preOpsFinalRequest")

            adapter = PreOpsSummaryAdapter(
                preOpsFinalRequest.preOpsVehicleQuestions
            )

            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter

            val toolBroken = ArrayList<ToolListResponce>()
            val toolMissing = ArrayList<ToolListResponce>()
            val toolPresent = ArrayList<ToolListResponce>()

            binding.textViewTotalPresent.visibility = View.GONE
            binding.textViewTotalMissing.visibility = View.GONE
            binding.textViewTotalBroken.visibility = View.GONE

            for (i in 0 until preOpsFinalRequest.preOpsTools.size) {
                if (preOpsFinalRequest.preOpsTools.get(i).answer.equals("missing")) {
                    toolMissing.add(preOpsFinalRequest.preOpsTools.get(i))
                    binding.textViewTotalMissing.visibility = View.VISIBLE
                } else if (preOpsFinalRequest.preOpsTools.get(i).answer.equals("present")) {
                    binding.textViewTotalPresent.visibility = View.VISIBLE
                    toolPresent.add(preOpsFinalRequest.preOpsTools.get(i))
                } else if (preOpsFinalRequest.preOpsTools.get(i).answer.equals("broken")) {
                    binding.textViewTotalBroken.visibility = View.VISIBLE
                    toolBroken.add(preOpsFinalRequest.preOpsTools.get(i))
                }
            }

            setAdapterToolBroken(toolBroken)
            setAdapterToolMissing(toolMissing)
            setAdapterToolPresent(toolPresent)
        }
    }

    private fun setAdapterToolBroken(toolList: ArrayList<ToolListResponce>) {
        val toolAdapter = PreOpsToolsAadpter(toolList)
        binding.reToolBroken.setHasFixedSize(true)
        binding.reToolBroken.layoutManager = LinearLayoutManager(this)
        binding.reToolBroken.adapter = toolAdapter
        binding.reToolBroken.isNestedScrollingEnabled = false
    }

    private fun setAdapterToolMissing(toolList: ArrayList<ToolListResponce>) {
        val toolAdapter = PreOpsToolsAadpter(toolList)
        binding.reToolMisssing.setHasFixedSize(true)
        binding.reToolMisssing.layoutManager = LinearLayoutManager(this)
        binding.reToolMisssing.adapter = toolAdapter
        binding.reToolMisssing.isNestedScrollingEnabled = false
    }

    private fun setAdapterToolPresent(toolList: ArrayList<ToolListResponce>) {
        val toolAdapter = PreOpsToolsAadpter(toolList)
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
        binding.imageCamera1.visibility = View.VISIBLE
        binding.tvReTakePicture.visibility = View.VISIBLE
        binding.imageCamera1.setImage(
            File(
                image
            )
        )
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataPreOpsImageUpload.observe(
            this,
            Observer<Event<PreOpsFileUploadResponse>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        val name = it.getContent()!!.file_name
                        if (uploadingSummaryImage) {
                            uploadingSummaryImage = false
                            preOpsFinalRequest.reportsImage =
                                name
                        }
                        uploadingPreOpsQuestionsImage(name)
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
                        hideProgress()
                        it.readContent()
                        viewModel.giveRepository().preOpsDone()
                        showMessageWithOneButton(
                            message = "Preops report submitted.",
                            cancellable = false,
                            buttonText = "Okay",
                            callback = object : DialogUtil.CallBack {

                                override fun buttonClicked() {
                                    super.buttonClicked()

                                    viewModel.giveRepository().getHomeGridItems()
                                    endAllActivities()
                                    startActivity(
                                        UiUtil.clearStackAndStartNewActivity(
                                            Intent(
                                                this@PreOpsStepFour,
                                                Dashboard::class.java
                                            )
                                        )
                                    )
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
            !this::image.isInitialized
        ) {
            showMessageInDialog("Please provide image")
            return
        }

        showProgress()

        preOpsFinalRequest.reportsComment = binding.edtComments.text.toString()
        uploadingSummaryImage = true
        viewModel.uploadPreOpsImage(image)
    }

    private var preOpsImageOnProcess = -1

    private fun uploadingPreOpsQuestionsImage(name: String) {
        if (preOpsImageOnProcess != -1) {
            preOpsFinalRequest.preOpsVehicleQuestions[preOpsImageOnProcess].image = name
        }
        preOpsImageOnProcess = -1
        preOpsFinalRequest.preOpsVehicleQuestions.forEachIndexed { index, question ->
            if (preOpsImageOnProcess == -1)
                question.imagePath?.let {
                    if (question.image == "") {
                        preOpsImageOnProcess = index
                        viewModel.uploadPreOpsImage(it)
                    }
                }
        }

        if (preOpsImageOnProcess == -1)
            uploadFinalData()
    }

    private fun uploadFinalData() {
        viewModel.uploadFinalPreOpsData(preOpsFinalRequest)
    }
}