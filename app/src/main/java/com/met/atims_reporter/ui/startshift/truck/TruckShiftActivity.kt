package com.met.atims_reporter.ui.startshift.truck

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityTruckShiftBinding
import com.met.atims_reporter.model.StartShiftRequest
import com.met.atims_reporter.model.StartShiftResponse
import com.met.atims_reporter.model.VehicleList
import com.met.atims_reporter.ui.pre_ops.step_one.PreOpsStepOne
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.UiUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import com.met.atims_reporter.widget.spinner.SpinnerData
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class TruckShiftActivity : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()

    private lateinit var binding: ActivityTruckShiftBinding
    private val viewModelProvider: ViewModelProvider by instance<ViewModelProvider>()
    private lateinit var viewModel: ViewModel
    private lateinit var selectedVehicle: VehicleList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(
            R.layout.activity_truck_shift
        )

        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)

        setPageTitle("START SHIFT")
        enableBackButton()
        initView()
        bindToViewModel()
        getVehicleList()
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataVehicelList.observe(
            this,
            Observer<Event<ArrayList<VehicleList>>> { t ->
                hideProgress()
                loadSpinnerData(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDataVehicelListError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )
        viewModel.mediatorLiveDataStartShift.observe(
            this,
            Observer<Event<StartShiftResponse>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        it.readContent()
                        hideProgress()
                        showMessageWithOneButton(
                            "Shift Started", callback = object : DialogUtil.CallBack {
                                override fun buttonClicked() {
                                    goToNext()
                                }
                            }, cancellable = false, buttonText = "OK"
                        )
                    }
                }
            }
        )

        viewModel.mediatorLiveDataStartShiftError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )
    }

    private fun loadSpinnerData(vehicleList: ArrayList<VehicleList>) {
        if (vehicleList.size > 0)
            selectedVehicle = vehicleList[0]
        val list: ArrayList<SpinnerData<VehicleList>> = ArrayList()
        for (i in 0 until vehicleList.size) {
            list.add(
                SpinnerData(
                    vehicleList[i].ID, vehicleList[i]
                )
            )
        }
        binding.truckList.heading("Vehicle")
        binding.truckList.addItems(
            list,
            object : com.met.atims_reporter.widget.spinner.Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    selectedVehicle = item.data as VehicleList
                }
            }
        )
    }

    private fun initView() {
        binding.tvStartShift.setOnClickListener {
            if (!this::selectedVehicle.isInitialized) {
                showMessageInDialog("Please select vehicle.")
                return@setOnClickListener
            }

            intent.getStringExtra(DATA)?.let {
                val startShiftRequest: StartShiftRequest = fromJson(it)
                startShiftRequest.apply {
                    user_id = viewModel.giveRepository().getUserData()!!.user_id.toString()
                    companyId = viewModel.giveRepository().getUserData()!!.company_id.toString()
                    vehicle_id = selectedVehicle.vehicle_id
                }

                showProgress()
                viewModel.startShift(
                    startShiftRequest
                )
            }
        }
    }

    private fun getVehicleList() {
        intent.getStringExtra(DATA)?.let {
            val startShiftRequest: StartShiftRequest = fromJson(it)

            showProgress()
            viewModel.getVehicleList(
                startShiftRequest.state_id,
                startShiftRequest.operationarea_id,
                startShiftRequest.beats_zone_id
            )
        }
    }

    private fun goToNext() {
        endAllActivities()
        startActivity(
            UiUtil.clearStackAndStartNewActivity(Intent(this, PreOpsStepOne::class.java))
        )
        finish()
    }
}