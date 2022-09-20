package com.met.atims_reporter.ui.add_crash_report.step_two

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityAddCrashReportStepTwoBinding
import com.met.atims_reporter.model.CrashReport
import com.met.atims_reporter.ui.add_crash_report.step_one.ViewModel
import com.met.atims_reporter.ui.add_crash_report.step_one.ViewModelProvider
import com.met.atims_reporter.ui.add_crash_report.step_three.AddCrashReportStepThree
import com.met.atims_reporter.ui.add_crash_report.step_two.adapter.TakePictureAdapter
import com.met.atims_reporter.ui.crash_report.CrashReportList
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.ImagePickerUtil
import com.sagar.android.logutilmaster.LogUtil
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import permissions.dispatcher.*

@RuntimePermissions
class AddCrashReportStepTwo : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()

    private val logUtil: LogUtil by instance<LogUtil>()
    private var image: String = ""
    private lateinit var binding: ActivityAddCrashReportStepTwoBinding
    private lateinit var adapterExterior: TakePictureAdapter
    private lateinit var adapterInterior: TakePictureAdapter
    private lateinit var adapterVin: TakePictureAdapter
    private lateinit var adapterThirdParty: TakePictureAdapter
    private lateinit var imageCaptureFor: ImageCaptureFor
    private lateinit var crashreport: CrashReport
    private val provider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel

    private enum class ImageCaptureFor {
        EXTERIOR,
        INTERIOR,
        VIN,
        THIRD_PARTY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_add_crash_report_step_two)
        binding.context = this

        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            provider
        ).get(ViewModel::class.java)

        setPageTitle("ADD CRASH REPORT")
        enableBackButton()
        willHandleBackNavigation()
        initView()

        intent.getStringExtra(DATA)?.let {
            crashreport = fromJson(it)
        }

        if (!crashreport.anyPropertyDamaged) {
            val intent = Intent(this, AddCrashReportStepThree::class.java)
            intent.putExtra(
                DATA,
                toJson(
                    crashreport
                )
            )
            startActivity(intent)
            finish()
        } else if (!crashreport.thirdPartyVehicleAvailable) {
            binding.textViewThirdPartyVehiclePhoto.visibility = View.GONE
            binding.recyclerViewThirdParty.visibility = View.GONE
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
                Intent(this, CrashReportList::class.java)
            )
            finish()
        }
        builder.setNegativeButton(Html.fromHtml("<font color='#FFC405'>No</font>")) { dialog, arg1 ->

        }
        builder.create()
        builder.show()
    }

    private fun initView() {
        binding.recyclerViewExterior.apply {
            layoutManager = LinearLayoutManager(
                this@AddCrashReportStepTwo,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapterExterior = TakePictureAdapter(
                object : TakePictureAdapter.Callback {
                    override fun takePicture() {
                        imageCaptureFor = ImageCaptureFor.EXTERIOR
                        takePictureWithPermissionCheck()
                    }
                }
            )
            adapter = adapterExterior
        }
        binding.recyclerViewInterior.apply {
            layoutManager = LinearLayoutManager(
                this@AddCrashReportStepTwo,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapterInterior = TakePictureAdapter(
                object : TakePictureAdapter.Callback {
                    override fun takePicture() {
                        imageCaptureFor = ImageCaptureFor.INTERIOR
                        takePictureWithPermissionCheck()
                    }
                }
            )
            adapter = adapterInterior
        }
        binding.recyclerViewVin.apply {
            layoutManager = LinearLayoutManager(
                this@AddCrashReportStepTwo,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapterVin = TakePictureAdapter(
                object : TakePictureAdapter.Callback {
                    override fun takePicture() {
                        imageCaptureFor = ImageCaptureFor.VIN
                        takePictureWithPermissionCheck()
                    }
                }
            )
            adapter = adapterVin
        }
        binding.recyclerViewThirdParty.apply {
            layoutManager = LinearLayoutManager(
                this@AddCrashReportStepTwo,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapterThirdParty = TakePictureAdapter(
                object : TakePictureAdapter.Callback {
                    override fun takePicture() {
                        imageCaptureFor = ImageCaptureFor.THIRD_PARTY
                        takePictureWithPermissionCheck()
                    }
                }
            )
            adapter = adapterThirdParty
        }
    }

    @NeedsPermission(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun takePicture() {
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
        if (this::imageCaptureFor.isInitialized)
            when (imageCaptureFor) {
                ImageCaptureFor.EXTERIOR -> {
                    adapterExterior.pictureCaptured(image)
                }
                ImageCaptureFor.INTERIOR -> {
                    adapterInterior.pictureCaptured(image)
                }
                ImageCaptureFor.VIN -> {
                    adapterVin.pictureCaptured(image)
                }
                ImageCaptureFor.THIRD_PARTY -> {
                    adapterThirdParty.pictureCaptured(image)
                }
            }
    }

    fun moveToNext() {
        val intent = Intent(this, AddCrashReportStepThree::class.java)
        crashreport.imagePaths.apply {
            clear()
            add(
                adapterExterior.getImages()
            )
            add(
                adapterInterior.getImages()
            )
            add(
                adapterVin.getImages()
            )
            add(
                adapterThirdParty.getImages()
            )
        }
        if (
            crashreport.imagePaths[0].size == 0 ||
            crashreport.imagePaths[1].size == 0 ||
            crashreport.imagePaths[2].size == 0 ||
            (crashreport.thirdPartyVehicleAvailable && crashreport.imagePaths[3].size == 0)
        ) {
            showMessageInDialog("Please provide at least one image for each")
            return
        }
        intent.putExtra(
            DATA,
            toJson(
                crashreport
            )
        )
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}