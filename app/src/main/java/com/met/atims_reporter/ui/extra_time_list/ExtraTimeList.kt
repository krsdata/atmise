package com.met.atims_reporter.ui.extra_time_list

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.databinding.ActivityExtraTimeListBinding
import com.met.atims_reporter.databinding.ExtraTimeRequestReasonDialogBinding
import com.met.atims_reporter.model.*
import com.met.atims_reporter.ui.extra_time_list.adapter.ExtraTimeListAdapter
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import com.met.atims_reporter.widget.spinner.Spinner
import com.met.atims_reporter.widget.spinner.SpinnerData
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ExtraTimeList : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()

    private val provider: ViewModelProvider by instance<ViewModelProvider>()
    private lateinit var viewModel: ViewModel
    private lateinit var binding: ActivityExtraTimeListBinding
    private lateinit var adapter: ExtraTimeListAdapter
    private val listForAdapter: ArrayList<ExtraTime> = ArrayList()
    private val extraTimeCancelReasons: ArrayList<ExtraTimeCancelReason> = ArrayList()
    private lateinit var selectedReason: ExtraTimeCancelReason
    private lateinit var startTime: CallAtTimeSpinnerData
    private lateinit var endTime: CallAtTimeSpinnerData
    private lateinit var  dialogBinding:ExtraTimeRequestReasonDialogBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_extra_time_list)

        showExtraTimeListActionBar()
        setPageTitle("EXTRA TIME REPORT")
        enableBackButton()
        setUpAdapter()

        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            provider
        )
            .get(ViewModel::class.java)
        bindToViewModel()

        showProgress()
        viewModel.getExtraTimeList(
            ExtraTimeListRequest(
                companyId = viewModel.giveRepository().getUserData()?.company_id.toString(),
                userId = viewModel.giveRepository().getUserData()?.user_id.toString()
            )
        )
        viewModel.getExtraTimeCancelReasons()

        viewModel.giveRepository().checkIfLoggedIn()
    }

    override fun goBack() {
        finish()
    }

    private fun setUpAdapter() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ExtraTimeList)
            this@ExtraTimeList.adapter = ExtraTimeListAdapter(
                listForAdapter,
                object : ExtraTimeListAdapter.Callback {
                    override fun request(extraTime: ExtraTime) {
                        openRequestDialog(extraTime)
                    }
                }
            )
            adapter = this@ExtraTimeList.adapter
        }
    }

    override fun filterExtraTimeList(startDate: String, endDate: String) {
        super.filterExtraTimeList(startDate, endDate)

        showProgress()
        viewModel.getExtraTimeList(
            ExtraTimeListRequest(
                userId = viewModel.giveRepository().getUserData()?.user_id.toString(),
                companyId = viewModel.giveRepository().getUserData()?.company_id.toString(),
                startDate = startDate,
                endDate = endDate
            )
        )
    }

    private fun openRequestDialog(extraTime: ExtraTime) {

        dialogBinding = ExtraTimeRequestReasonDialogBinding.inflate(
            LayoutInflater.from(this),
            null,
            false
        )
        var selectYourReasonOn = true

        val customDialog = Dialog(this)

        customDialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(dialogBinding.root)
            window
                ?.setLayout(
                    CoordinatorLayout.LayoutParams.MATCH_PARENT,
                    CoordinatorLayout.LayoutParams.WRAP_CONTENT
                )
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(true)
            show()
        }

        dialogBinding.appcompatImageViewClose.setOnClickListener {
            customDialog.dismiss()
        }
        dialogBinding.btnSubmit.setOnClickListener {

            if (dialogBinding.textTMCAutorization.text.toString().isEmpty()) {
                showMessageInDialog("Please provide TMC Authorisation")
                return@setOnClickListener
            }
            if (selectYourReasonOn && !this::selectedReason.isInitialized) {
                showMessageInDialog("Please select cancel reason")
                return@setOnClickListener
            }
            if (!selectYourReasonOn && dialogBinding.textWriteReason.text.toString().isEmpty()) {
                showMessageInDialog("Please provide cancel reason")
                return@setOnClickListener
            }

            showProgress()

            viewModel.updateExtraTime(
                ExtraTimeRequest(
                    viewModel.giveRepository().getUserData()?.user_id?.toString()?:"",
                    viewModel.giveRepository().getUserData()?.user_id?.toString()?:"",
                    viewModel.giveRepository().getUserData()?.company_id?.toString()?:"",
                    dialogBinding.textIncidentNumber.text.toString().trim(),
                    extraTime.shiftId,
                    "", startTime.data, endTime.data,
                    dialogBinding.tvTotalExtraTime.text.toString(),
                    dialogBinding.textTMCAutorization.text.toString(),
                    if (selectYourReasonOn) selectedReason.extra_time_reason_id else "",
                    if (!selectYourReasonOn) dialogBinding.textWriteReason.text.toString() else "",
                    viewModel.giveRepository().giveVehicleId()
                )
            )

            customDialog.dismiss()
        }
        dialogBinding.tvSelectReason.setOnClickListener {
            if (selectYourReasonOn)
                return@setOnClickListener

            selectYourReasonOn = true

            dialogBinding.textSelectBackground.background = null
            dialogBinding.tvSelectReason.background = getDrawable(R.drawable.button_yellow_reason)
            dialogBinding.spinnerCancelReason.visibility = View.VISIBLE
            dialogBinding.textWriteReason.visibility = View.INVISIBLE

            val list: ArrayList<SpinnerData<ExtraTimeCancelReason>> = ArrayList()
            extraTimeCancelReasons.forEach { reason ->
                list.add(
                    SpinnerData(
                        reason.extra_time_reason,
                        reason
                    )
                )
            }

            dialogBinding.spinnerCancelReason.addItems(
                list,
                object : Spinner.OnItemSelectedListener {
                    override fun <T> selected(item: SpinnerData<T>) {
                        selectedReason = item.data as ExtraTimeCancelReason
                    }
                }
            )

            if (this::selectedReason.isInitialized) {
                dialogBinding.spinnerCancelReason.select<SpinnerData<ExtraTimeCancelReason>>(
                    selectedReason.extra_time_reason
                )
            } else {
                if(extraTimeCancelReasons.size>0)
                selectedReason = extraTimeCancelReasons[0]
            }
        }
        dialogBinding.textSelectBackground.setOnClickListener {
            if (!selectYourReasonOn)
                return@setOnClickListener

            selectYourReasonOn = false

            dialogBinding.tvSelectReason.background = null
            dialogBinding.textSelectBackground.background =
                getDrawable(R.drawable.button_yellow_reason)
            dialogBinding.spinnerCancelReason.visibility = View.GONE
            dialogBinding.textWriteReason.visibility = View.VISIBLE
        }

        dialogBinding.spinnerCancelReason.visibility = View.VISIBLE
        dialogBinding.textWriteReason.visibility = View.INVISIBLE

        val list: ArrayList<SpinnerData<ExtraTimeCancelReason>> = ArrayList()
        extraTimeCancelReasons.forEach { reason ->
            list.add(
                SpinnerData(
                    reason.extra_time_reason,
                    reason
                )
            )
        }

        dialogBinding.spinnerCancelReason.addItems(
            list,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    selectedReason = item.data as ExtraTimeCancelReason
                }
            }
        )

        if (this::selectedReason.isInitialized) {
            dialogBinding.spinnerCancelReason.select<SpinnerData<ExtraTimeCancelReason>>(
                selectedReason.extra_time_reason
            )
        } else {
            if(extraTimeCancelReasons.size>0)
            selectedReason = extraTimeCancelReasons.get(0)
        }
        viewModel.getCallList()

    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataExtraTimeList.observe(
            this,
            Observer<Event<ArrayList<ExtraTime>>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        val content = it.getContent()!!

                        listForAdapter.apply {
                            clear()
                            addAll(content)
                        }
                        adapter.notifyDataSetChanged()
                        if (listForAdapter.size == 0)
                            showMessageInDialog("There is no data to show.")

                        if (
                            listForAdapter.size == 0
                        ) {
                            binding.textViewNoContent.visibility = View.VISIBLE
                        } else {
                            binding.textViewNoContent.visibility = View.GONE
                        }
                    }
                }
            }
        )

        viewModel.mediatorLiveDataExtraTimeListError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                        binding.textViewNoContent.visibility = View.VISIBLE
                    }
                }
            }
        )

        viewModel.mediatorLiveDataExtraTimeCancelReasons.observe(
            this,
            Observer<Event<ArrayList<ExtraTimeCancelReason>>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        extraTimeCancelReasons.clear()
                        extraTimeCancelReasons.addAll(it.getContent()!!)
                    }
                }
            }
        )

        viewModel.mediatorLiveDataExtraTimeCancelReasonsError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )

        viewModel.mediatorLiveDataExtraTimeRequest.observe(
            this,
            Observer<Event<String>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        it.readContent()
                        showMessageInDialog("Extra time is requested.")
                        viewModel.getExtraTimeList(
                            ExtraTimeListRequest(
                                companyId = viewModel.giveRepository()
                                    .getUserData()!!.company_id.toString(),
                                userId = viewModel.giveRepository()
                                    .getUserData()!!.user_id.toString()
                            )
                        )
                    }
                }
            }
        )

        viewModel.mediatorLiveDataExtraTimeRequestError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )
        viewModel.mediatorLiveDataCallList.observe(
            this,
            Observer<Event<ArrayList<CallAtTimeSpinnerData>>> { t ->
                hideProgress()
                if (t.shouldReadContent())
                    loadSpinnerCallAtListData(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDatCallListError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )
    }

   private fun loadSpinnerCallAtListData(callListData: ArrayList<CallAtTimeSpinnerData>){
            if (callListData.size > 0) {
                startTime = callListData[0]
                endTime =  callListData[0]
            }

            val list: ArrayList<SpinnerData<CallAtTimeSpinnerData>> = ArrayList()
            for (i in 0 until callListData.size) {
                list.add(
                    SpinnerData(
                        callListData[i].data, callListData[i]
                    )
                )
            }


       dialogBinding.apply {
       spinnerStartTime.heading("Start Time")
       spinnerStartTime.spinnerWidthPercent(50)
       spinnerStartTime.addItems(
           list,
           object : Spinner.OnItemSelectedListener {
               override fun <T> selected(item: SpinnerData<T>) {
                   startTime = item.data as CallAtTimeSpinnerData
                   loadCallEndTime()
               }
           }
        )


       }
    }
    private fun loadCallEndTime() {
        val callstartTime = startTime.data
        val spinnerDataForCallCompleted: ArrayList<SpinnerData<CallAtTimeSpinnerData>> = ArrayList()
        viewModel.giveRepository().getNextFutureValuesListForCallEndSpinner(
            callstartTime
        )?.forEach { data ->
            spinnerDataForCallCompleted.add(
                SpinnerData(
                    data.data,
                    data
                )
            )
        }
        dialogBinding.spinnerEndTime.addItems(
            spinnerDataForCallCompleted,
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    endTime = item.data as CallAtTimeSpinnerData
                    val df = SimpleDateFormat("dd/MM/yyyy hh:mm a")
                    val df2 = SimpleDateFormat("dd/MM/yyyy")
                    val startDate = df2.format( Date().time)
                    val startT = df.parse(startDate+" "+startTime.data)
                    val endT: Date? = df.parse(startDate+" "+endTime.data)
                    loadDuration(startT, endT)
                }
            }
        )
        dialogBinding.spinnerEndTime.heading("End Time")
    }
    private fun loadDuration(startDate: Date?, endDate: Date?) {
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        var difference = (endDate?.time ?: Date().time) - ((startDate?.time ?: Date().time))
        if (difference < 0) {
            val dateMax = simpleDateFormat.parse("24:00:00")
            val dateMin = simpleDateFormat.parse("00:00:00")
            difference = dateMax.time - ((startDate?.time ?: Date().time) + ((endDate?.time
                ?: Date().time) - (dateMin?.time ?: Date().time)))
        }
        val days = (difference / (1000 * 60 * 60 * 24)).toInt()
        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
        val min =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)
        val sec =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours - 1000 * 60 * min).toInt() / 1000

        //Log.i("log_tag", "Hours: $hours, Mins: $min, Secs: $sec")
        var diffTime = "$hours h $min m"
        dialogBinding.tvTotalExtraTime.text = diffTime

    }
}