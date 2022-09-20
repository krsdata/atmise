package com.met.atims_reporter.ui.pre_ops.step_two

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityPreOpsStepTwoBinding
import com.met.atims_reporter.model.PreOpsFinalRequest
import com.met.atims_reporter.model.QuestionLisResponse
import com.met.atims_reporter.ui.pre_ops.step_three.PreOpsStepThree
import com.met.atims_reporter.ui.pre_ops.step_two.adapter.PreOpsTwoAdapter
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
class PreOpsStepTwo : AtimsSuperActivity(), KodeinAware {

    private lateinit var binding: ActivityPreOpsStepTwoBinding
    private lateinit var adapter: PreOpsTwoAdapter
    private val logUtil: LogUtil by instance()
    private lateinit var image: String
    override val kodein: Kodein by kodein()
    private val viewModelProvider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    private lateinit var questionLisResponseForImageCapture: QuestionLisResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_pre_ops_step_two)
        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)
        setPageTitle("PRE OPS - VEHICLE")
        enableBackButton()
        getQuestionList()
        initView()
        bindToViewModel()
    }

    private fun getQuestionList() {
        showProgress()
        viewModel.getQuestionList()
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataQueestionList.observe(
            this,
            Observer<Event<ArrayList<QuestionLisResponse>>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        setAdapter(t.getContent() ?: ArrayList())
                    }
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
//        if (adapter.valid())
        startActivity(
            Intent(this, PreOpsStepThree::class.java)
                .putExtra(
                    DATA,
                    toJson(
                        fromJson<PreOpsFinalRequest>(intent.getStringExtra(DATA)!!)
                            .apply {
                                preOpsVehicleQuestions.clear()
                                preOpsVehicleQuestions.addAll(
                                    adapter.getList()
                                )
                            }
                    )
                )
        )
    }

    private fun setAdapter(questionList: ArrayList<QuestionLisResponse>) {
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
        binding.preOpsVehicle.layoutManager = LinearLayoutManager(this)
        adapter = PreOpsTwoAdapter(
            questionList,
            object : PreOpsTwoAdapter.Callback {
                override fun takePicture(questionLisResponse: QuestionLisResponse) {
                    questionLisResponseForImageCapture = questionLisResponse
                    takeProfilePictureWithPermissionCheck()
                }
            },
            this,
            object :PreOpsTwoAdapter.OnItemClick{
                override fun onClick(view: View, position: Int) {
                    when(view.id){
                        R.id.appcompatImageViewMessage->{
                            questionList[position].isCommentVisible=true
                            adapter.notifyDataSetChanged()
                        }
                    }
                }

            }
        )
        binding.preOpsVehicle.adapter = adapter
    }

    override fun goBack() {
        finish()
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
            if (it.preops_vehicle_question_id == questionLisResponseForImageCapture.preops_vehicle_question_id) {
                it.imagePath = this.image
            }
        }
        adapter.notifyDataSetChanged()
    }
}