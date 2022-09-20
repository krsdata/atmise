package com.met.atims_reporter.ui.add_inspection.step_two

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.databinding.ActivityAddInspectionStepTwoBinding
import com.met.atims_reporter.model.InsectionQuestionLisResponse
import com.met.atims_reporter.model.InspectionFinalRequest
import com.met.atims_reporter.ui.add_inspection.step_three.AddInspectionStepThree
import com.met.atims_reporter.ui.add_inspection.step_two.adapter.InspectionTwoAdapter
import com.met.atims_reporter.ui.add_inspection.step_two.adapter.InspectionVehicleAdapter
import com.met.atims_reporter.ui.inspection_list.InspectionList
import com.met.atims_reporter.ui.pre_ops.step_three.PreOpsStepThree
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.ImagePickerUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import com.sagar.android.logutilmaster.LogUtil
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import permissions.dispatcher.*

@RuntimePermissions
class AddInspectionStepTwo : AtimsSuperActivity(), KodeinAware {

    private lateinit var binding: ActivityAddInspectionStepTwoBinding
    private lateinit var mInspectionVehicleAdapter: InspectionVehicleAdapter
    override val kodein: Kodein by kodein()
    private val viewModelProvider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    private lateinit var adapter: InspectionTwoAdapter
    private val logUtil: LogUtil by instance()
    private lateinit var image: String
    private lateinit var questionLisResponseForImageCapture: InsectionQuestionLisResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_add_inspection_step_two)
        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)
        binding.context = this
        setPageTitle("INSPECTIONS - VEHICLE")
        enableBackButton()
        willHandleBackNavigation()

        getQuestionList()
        initView()
        bindToViewModel()
    }


    fun moveToNext() {
        if (adapter.valid())
            startActivity(
                Intent(this, AddInspectionStepThree::class.java)
                    .putExtra(
                        KeyWordsAndConstants.DATA,
                        toJson(
                            fromJson<InspectionFinalRequest>(
                                intent.getStringExtra(
                                    KeyWordsAndConstants.DATA
                                )!!
                            )
                                .apply {
                                    inspectionsVehicleQuestions.clear()
                                    inspectionsVehicleQuestions.addAll(
                                        adapter.getList()
                                    )
                                }
                        )
                    )
            )
    }


    private fun getQuestionList() {
        showProgress()
        viewModel.getQuestionList()
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataQueestionList.observe(
            this,
            Observer<Event<ArrayList<InsectionQuestionLisResponse>>> { t ->
                if (t.shouldReadContent()) {
                    hideProgress()
                    setAdapter(t.getContent() ?: ArrayList())
                }
            }
        )
        viewModel.mediatorLiveDataQueestionError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

    }

    private fun initView() {
        binding.btnNext.setOnClickListener {
            gotoNext()
        }
    }

    private fun gotoNext() {
        if (adapter.valid())
            startActivity(
                Intent(this, PreOpsStepThree::class.java)
                    .putExtra(
                        KeyWordsAndConstants.DATA,
                        toJson(
                            fromJson<InspectionFinalRequest>(
                                intent.getStringExtra(
                                    KeyWordsAndConstants.DATA
                                )!!
                            )
                                .apply {
                                    inspectionsVehicleQuestions.clear()
                                    inspectionsVehicleQuestions.addAll(
                                        adapter.getList()
                                    )
                                }
                        )
                    )
            )
    }

    private fun setAdapter(questionList: ArrayList<InsectionQuestionLisResponse>) {
        if (
            questionList.size == 0
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
            return
        }
        binding.rvInspectionStep2List.layoutManager = LinearLayoutManager(this)
        adapter = InspectionTwoAdapter(
            questionList,
            object : InspectionTwoAdapter.Callback {
                override fun takePicture(questionLisResponse: InsectionQuestionLisResponse) {
                    questionLisResponseForImageCapture = questionLisResponse
                    takeProfilePictureWithPermissionCheck()
                }


            },
            this
        )
        binding.rvInspectionStep2List.adapter = adapter
    }

    override fun goBack() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.back_alert_title_txt)
        builder.setMessage(R.string.back_alert_txt)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(Html.fromHtml("<font color='#FFC405'>Yes</font>")) { dialog, arg1 ->
            viewModel.giveRepository().getHomeGridItems()
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
                    gotImage()
                }
            }
        }
    }

    private fun gotImage() {
        adapter.getList().forEach {
            if (it.inspection_vehicle_question == questionLisResponseForImageCapture.inspection_vehicle_question) {
                it.imagePath = this.image
            }
        }
        adapter.notifyDataSetChanged()
    }
}