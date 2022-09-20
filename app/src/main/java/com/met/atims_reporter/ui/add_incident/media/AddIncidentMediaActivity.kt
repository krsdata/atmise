package com.met.atims_reporter.ui.add_incident.media

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.Observer
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.met.atims_reporter.R
import com.met.atims_reporter.application.ApplicationClass
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.core.KeyWordsAndConstants.MAX_VIDEO_LENGTH_SECS
import com.met.atims_reporter.core.KeyWordsAndConstants.REQUEST_CODE_PICK_IMAGE
import com.met.atims_reporter.core.KeyWordsAndConstants.REQUEST_CODE_RECORD_VIDEO
import com.met.atims_reporter.databinding.ActivityAddIncidentBinding
import com.met.atims_reporter.enums.OperationMode
import com.met.atims_reporter.model.AddIncidentRequest
import com.met.atims_reporter.model.IncidentFieldItem
import com.met.atims_reporter.ui.incidents.Incidents
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.ImagePickerUtil
import com.met.atims_reporter.util.UiUtil
import com.met.atims_reporter.util.repository.Event
import com.sagar.android.logutilmaster.LogUtil
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import permissions.dispatcher.*
import java.io.File

@RuntimePermissions
class AddIncidentMediaActivity : AtimsSuperActivity(), KodeinAware {
    companion object {
        const val FIELDS = "FIELDS"
    }

    override val kodein: Kodein by kodein()

    private val logUtil: LogUtil by instance()
    private val provider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    private lateinit var binding: ActivityAddIncidentBinding
    private var image: String = ""
    private var videoUri: Uri? = null
    private lateinit var addIncidentRequest: AddIncidentRequest
    private var fields: ArrayList<IncidentFieldItem> = ArrayList()
    private var photoReq = false
    private var videoReq = false
    private var noteReq = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_add_incident)
        binding.context = this
        setPageTitle("ADD INCIDENT")
        enableBackButton()
        willHandleBackNavigation()

        intent.getStringExtra(DATA)?.let {
            addIncidentRequest = fromJson(it)
        }

        intent.getStringExtra(FIELDS)?.let {
            fields = fromJson(it)
        }
        intent.getSerializableExtra(KeyWordsAndConstants.OPERATION_MODE)?.let {
            val action = it as OperationMode
            if (action == OperationMode.EDIT){
                setPageTitle("EDIT INCIDENT")
            }else{
                setPageTitle("ADD INCIDENT")
            }
        }

        binding.editTextNotes.setText(addIncidentRequest.note)
        fields.forEach {
            when (it.id) {
                "24" -> {
                    photoReq = it.show == "1"
                    if (!photoReq)
                        binding.cardViewPictureContainer.visibility = View.GONE
                }
                "25" -> {
                    videoReq = it.show == "1"
                    if (!videoReq)
                        binding.cardViewVideoContainer.visibility = View.GONE
                }
                "26" -> {
                    noteReq = it.show == "1"
                    if (!noteReq) {
                        binding.textViewNotes.visibility = View.GONE
                        binding.asterisk.visibility = View.GONE
                        binding.editTextNotes.visibility = View.GONE
                    }
                }
            }
        }

        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            provider
        )
            .get(ViewModel::class.java)

        bindToViewModel()

        if (
            !photoReq &&
            !videoReq &&
            !noteReq
        ) {
            binding.buttonSubmit.visibility = View.GONE
            processFinished()
        }
    }


    override fun goBack() {
        viewModel.giveRepository().getHomeGridItems()
        startActivity(Intent(this, Incidents::class.java))
        finish()
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataAddOrUpdateIncident.observe(
            this,
            androidx.lifecycle.Observer<Event<String>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        it.readContent()
                        showMessageWithOneButton(
                            message = "Incident report submitted.",
                            cancellable = false,
                            buttonText = "Okay",
                            callback = object : DialogUtil.CallBack {

                                override fun buttonClicked() {
                                    super.buttonClicked()
                                    viewModel.giveRepository().getHomeGridItems()
                                    (this@AddIncidentMediaActivity.applicationContext as ApplicationClass).mAddIncidentRequest = null
                                    endAllActivities()
                                    startActivity(
                                        UiUtil.clearStackAndStartNewActivity(
                                            Intent(
                                                this@AddIncidentMediaActivity,
                                                Incidents::class.java
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

        viewModel.mediatorLiveDataAddOrUpdateIncidentError.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )

        viewModel.mediatorLiveDataUpdateWazeInformation.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()){
                        it.readContent()
                        sendIncidentDataToServer()
                    }
                }
            }
        )

        viewModel.mediatorLiveDataUpdateWazeInformationError.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )
    }

    fun initiatePictureCapture() {
        takeProfilePictureWithPermissionCheck()
    }

    fun initiateVideoCapture() {
        recordVideoWithPermissionCheck()
    }

    @NeedsPermission(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun takeProfilePicture() {
        ImagePickerUtil.pickImage(
            context = this,
            reqCode = REQUEST_CODE_PICK_IMAGE,
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
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            intentData?.let {
                it.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.let { images ->
                    logUtil.logV("got image $images")
                    image = images[0]
                    setImageToUI()
                }
            }
        } else if (requestCode == REQUEST_CODE_RECORD_VIDEO && resultCode == Activity.RESULT_OK) {
            intentData?.let {
                val uri: Uri? = it.data
                videoUri = uri
                setVideoToUI()
            }
        }
    }

    private fun getRealPathFromUri(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index =
                cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }

    private fun setImageToUI() {
        binding.imageRL.visibility = View.VISIBLE
        binding.imageCamera.setImage(
            File(
                image
            )
        )
    }

    @NeedsPermission(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )
    fun recordVideo() {
        showMessageWithOneButton(
            message = "Videos are best captured in landscape mode. Simply click ok and tilt your phone to start recording in landscape mode.",
            image = getDrawable(R.drawable.rotate_device),
            buttonText = "Ok",
            cancellable = true,
            callback = object : DialogUtil.CallBack {
                override fun buttonClicked() {
                    super.buttonClicked()
                    Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                        .putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_LENGTH_SECS)
                        .also { takeVideoIntent ->
                            takeVideoIntent.resolveActivity(packageManager)?.also {
                                startActivityForResult(takeVideoIntent, REQUEST_CODE_RECORD_VIDEO)
                            }
                        }
                }
            }
        )
    }

    @OnShowRationale(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )
    fun showRationaleRecordVideo(request: PermissionRequest) {
        showMessageWithTwoButton(
            message = "We need some permission to record video.",
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
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )
    fun permissionDeniedRecordVideo() {
        showMessageWithOneButton(
            "You will not be able to record video. you can again accept the permission by going directly to permission section in settings.",
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
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )
    fun neverAskAgainRecordVideo() {
        showMessageWithOneButton(
            "You will not be able to record video. you can again accept the permission by going directly to permission section in settings.",
            object : DialogUtil.CallBack {
                override fun dialogCancelled() {

                }

                override fun buttonClicked() {
                    finish()
                }

            }
        )
    }

    private fun setVideoToUI() {
        videoUri?.let {
            //binding.video.visibility = View.VISIBLE
            binding.videoRL.visibility = View.VISIBLE
            binding.video.setVideoUri(it)
        }
    }

    fun processFinished() {
        if (!validate())
            return

        showProgress()
        sendIncidentDataToServer()
    }

    private fun sendIncidentDataToServer() {
        viewModel.addOrUpdateIncident(
            intent.getSerializableExtra(KeyWordsAndConstants.OPERATION_MODE)?.let {
                val action = it as OperationMode
                if (action == OperationMode.EDIT)
                    addIncidentRequest.id
                else
                    null
            } ?: run {
                null
            },
            addIncidentRequest.latitude,
            addIncidentRequest.longitude,
            addIncidentRequest.callAt,
            addIncidentRequest.callStarted,
            addIncidentRequest.callComplete,
            addIncidentRequest.incidentTime,
            addIncidentRequest.incidentType,
            addIncidentRequest.trafficDirection,
            addIncidentRequest.mileMaker,
            addIncidentRequest.propertyDamage,
            addIncidentRequest.crashInvolced,
            addIncidentRequest.firstResponder,
            addIncidentRequest.firstResponderUnit,
            addIncidentRequest.roadSurver,
            addIncidentRequest.laneLocation,
            addIncidentRequest.personTransported,
            addIncidentRequest.companyColor,
            addIncidentRequest.vehicleType,
            addIncidentRequest.assistType,
            addIncidentRequest.comments,
            intent.getSerializableExtra(KeyWordsAndConstants.OPERATION_MODE)?.let {
                val action = it as OperationMode
                if (action == OperationMode.EDIT)
                    "update"
                else
                    "insert"
            } ?: run {
                ""
            },
            viewModel.giveRepository().getUserData()!!.user_id.toString(),
            viewModel.giveRepository().getUserData()!!.company_id.toString(),
            "MOB",
            if (image == "") null else File(image),
            if (videoUri == null) null else File(getRealPathFromUri(this, videoUri)!!),
            viewModel.giveRepository().giveShiftId(),
            addIncidentRequest.plate_no,
            binding.editTextNotes.text.toString(),
            addIncidentRequest.incident_status,
            addIncidentRequest.direction,
            addIncidentRequest.description,
            addIncidentRequest.companyRoute,
            addIncidentRequest.vehicleQty,
            addIncidentRequest.vehicle_id,
            addIncidentRequest.vehicleInformation,
            addIncidentRequest.vendor,
            addIncidentRequest.contract,
            addIncidentRequest.ramp_lane,
            addIncidentRequest.travel_lanes_blocked,
            addIncidentRequest.lane_restoration_time,
            addIncidentRequest.incident_no
        )
    }

    private fun validate(): Boolean {
        /*if (
            videoUri == null
        ) {
            showMessageInDialog("Please record video.")
            return false
        }*/
        if (
            noteReq &&
            binding.editTextNotes.text.toString().isEmpty()
        ) {
            showMessageInDialog("Please provide notes")
            return false
        }

        return true
    }
}