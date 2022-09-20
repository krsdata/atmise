package com.met.atims_reporter.ui.startshift.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityStartShiftBinding
import com.met.atims_reporter.enums.StartShipEnum
import com.met.atims_reporter.model.*
import com.met.atims_reporter.ui.startshift.list.adapter.StartShiftOperationAdapter
import com.met.atims_reporter.ui.startshift.list.adapter.StartShiftStateAdapter
import com.met.atims_reporter.ui.startshift.list.adapter.StartShiftTimeAdapter
import com.met.atims_reporter.ui.startshift.list.adapter.StartShiftZoneAdapter
import com.met.atims_reporter.ui.startshift.truck.TruckShiftActivity
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class StartShiftActivity : AtimsSuperActivity(), KodeinAware {
    override val kodein: Kodein by kodein()

    companion object {
        const val SHIFT_STEP = "shiftStep"
    }

    private lateinit var binding: ActivityStartShiftBinding
    private lateinit var startShipEnum: StartShipEnum
    private val viewModelProvider: ViewModelProvider by instance<ViewModelProvider>()
    private lateinit var viewModel: ViewModel
    private lateinit var startShiftRequest: StartShiftRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(
            R.layout.activity_start_shift
        )

        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)

        setPageTitle("START SHIFT")
        enableBackButton()

        intent.getSerializableExtra(SHIFT_STEP)?.let {
            startShipEnum = (it as StartShipEnum)
        }

        intent.getStringExtra(DATA)?.let {
            startShiftRequest = fromJson(it)
        } ?: run {
            startShiftRequest = StartShiftRequest()
        }

        setTitle()
        viewModel.giveRepository().checkIfLoggedIn()
    }

    override fun onResume() {
        super.onResume()
        binding.textNoDataFound.visibility = View.GONE
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataStateList.observe(
            this,
            Observer<Event<ArrayList<StateList>>> { t ->
                t?.let{
                    if(it.shouldReadContent()) {
                      //  hideProgress()
                        setStateAdapter(t.getContent() ?: ArrayList())
                    }
                }
            }
        )
        viewModel.mediatorLiveDataStateListError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

    }

    private fun bindToViewModelOperator() {
        viewModel.mediatorLiveDataOperationList.observe(
            this,
            Observer<Event<ArrayList<OperationList>>> { t ->
              //  hideProgress()
                t?.let {
                    if (it.shouldReadContent())
                        setOperationAdapter(t.getContent() ?: ArrayList())
                }
            }
        )
        viewModel.mediatorLiveDataOperationListError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )
    }


    private fun bindToViewModelZone() {
        viewModel.mediatorLiveDataZoneList.observe(
            this,
            Observer<Event<ArrayList<ZoneList>>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        setZoneAdapter(t.getContent() ?: ArrayList())
                }
            }
        )
        viewModel.mediatorLiveDataZoneListError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )
    }


    private fun bindToViewModelTime() {
        viewModel.mediatorLiveDataShiftType.observe(
            this,
            Observer<Event<ArrayList<ShiftType>>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        setTimeAdapter(t.getContent() ?: ArrayList())
                }
            }
        )
        viewModel.mediatorLiveDataShiftTypeError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )
    }

    private fun getStateList() {
        showProgress()
        viewModel.getStateList()
    }

    private fun getOperationList() {
        showProgress()
        viewModel.getOperationList(startShiftRequest.state_id)
    }

    private fun getZone() {
        showProgress()
        viewModel.getZoneList(startShiftRequest.state_id, startShiftRequest.operationarea_id)
    }

    private fun getTime() {
        showProgress()
        viewModel.getShiftTime()
    }

    private fun setTitle() {
        when (startShipEnum) {
            StartShipEnum.STATE -> {
                bindToViewModel()
                getStateList()
                binding.textViewHeading.text = startShipEnum.getTitle()
                binding.textNoDataFound.visibility = View.GONE
            }
            StartShipEnum.OPERATION -> {
                bindToViewModelOperator()
                getOperationList()
                binding.textViewHeading.text = startShipEnum.getTitle()
                binding.textNoDataFound.visibility = View.GONE
            }
            StartShipEnum.ZONE -> {
                bindToViewModelZone()
                getZone()
                binding.textViewHeading.text = startShipEnum.getTitle()
                binding.textNoDataFound.visibility = View.GONE
            }
            StartShipEnum.SHIFT_TIME -> {
                bindToViewModelTime()
                getTime()
                binding.textViewHeading.text = startShipEnum.getTitle()
                binding.textNoDataFound.visibility = View.GONE
            }
        }
    }

    private fun setStateAdapter(stateList: ArrayList<StateList>?) {
        val mStartShiftAdapter = StartShiftStateAdapter(
            this,
            stateList,
            object : StartShiftStateAdapter.Callback {
                override fun clicked(stateList: StateList?) {
                    startShiftRequest.state_id = stateList?.state_id?:""
                    moveToNextStep()
                }
            }
        )

        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = mStartShiftAdapter
        if (stateList != null && stateList.size != 0) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.textNoDataFound.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.textNoDataFound.visibility = View.VISIBLE
        }
        hideProgress()
    }

    private fun setOperationAdapter(operationList: ArrayList<OperationList>?) {
        val mStartShiftAdapter = StartShiftOperationAdapter(this, operationList,
            object : StartShiftOperationAdapter.Callback {
                override fun selected(operationList: OperationList?) {
                    startShiftRequest.operationarea_id = operationList?.operationarea_id?:""
                    moveToNextStep()
                }
            }
        )

        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = mStartShiftAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
        if (operationList != null && operationList.size != 0) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.textNoDataFound.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.textNoDataFound.visibility = View.VISIBLE
        }
        hideProgress()
    }


    private fun setZoneAdapter(zoneList: ArrayList<ZoneList>?) {
        val mStartShiftAdapter = StartShiftZoneAdapter(this, zoneList, object :
            StartShiftZoneAdapter.Callback {
            override fun clicked(ZoneList: ZoneList?) {
                startShiftRequest.beats_zone_id = ZoneList?.beats_zone_id?:""
                moveToNextStep()
            }
        })

        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = mStartShiftAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
        if (zoneList != null && zoneList.size != 0) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.textNoDataFound.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.textNoDataFound.visibility = View.VISIBLE
        }
        hideProgress()
    }


    private fun setTimeAdapter(list: ArrayList<ShiftType>?) {
        val mStartShiftAdapter = StartShiftTimeAdapter(this, list, object :
            StartShiftTimeAdapter.Callback {
            override fun clicked(item: ShiftType?) {
                startShiftRequest.operationShifttimeId = item?.operation_shifts_id?:""
                moveToNextStep()
            }
        })
        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = mStartShiftAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
        if (list != null && list.size != 0) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.textNoDataFound.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.textNoDataFound.visibility = View.VISIBLE
        }
        hideProgress()
    }

    private fun moveToNextStep() {
        val intent = Intent(this, StartShiftActivity::class.java)
        intent.putExtra(DATA, toJson(startShiftRequest))
        when (startShipEnum) {
            StartShipEnum.STATE -> {
                intent.putExtra(SHIFT_STEP, StartShipEnum.OPERATION)
                startActivity(intent)
            }
            StartShipEnum.OPERATION -> {
                intent.putExtra(SHIFT_STEP, StartShipEnum.ZONE)
                startActivity(intent)
            }
            StartShipEnum.ZONE -> {
                intent.putExtra(SHIFT_STEP, StartShipEnum.SHIFT_TIME)
                startActivity(intent)
            }
            StartShipEnum.SHIFT_TIME -> {
                moveToTruckShiftActivity()
            }
        }
    }

    private fun moveToTruckShiftActivity() {
        startActivity(
            Intent(this, TruckShiftActivity::class.java)
                .putExtra(DATA, toJson(startShiftRequest))
        )
    }
}