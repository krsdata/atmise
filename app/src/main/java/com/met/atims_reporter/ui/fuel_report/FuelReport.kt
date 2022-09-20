package com.met.atims_reporter.ui.fuel_report

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityFuelReportBinding
import com.met.atims_reporter.enums.OperationMode
import com.met.atims_reporter.model.FuelListDetails
import com.met.atims_reporter.ui.add_fuel_report.AddFuelReport
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.ui.fuel_report.adapter.FuelReportAdapter
import com.met.atims_reporter.ui.fuel_report_details.FuleReportDetailsActivity
import com.met.atims_reporter.ui.fuel_report_details.FuleReportDetailsActivity.Companion.FUEL_DETAILS
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class FuelReport : AtimsSuperActivity(), KodeinAware {

    private lateinit var binding: ActivityFuelReportBinding
    private lateinit var adapter: FuelReportAdapter
    override val kodein: Kodein by kodein()
    private val viewModelProvider: ViewModelProvider by instance<ViewModelProvider>()
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_fuel_report)

        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)
        showFuelReportActionBar()
        setPageTitle("FUEL REPORT")
        enableBackButton()
        setUpSwipeRefresh()
        bindToViewModel()

        viewModel.giveRepository().checkIfLoggedIn()
    }

    override fun onResume() {
        super.onResume()
        getFuelList()
    }

    override fun goBack() {
        viewModel.giveRepository().getHomeGridItems()
        startActivity(
            Intent(this, Dashboard::class.java)
        )
        finish()
    }

    private fun getFuelList() {
        showProgress()
        viewModel.getFuelList()
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataFuelList.observe(
            this,
            Observer<Event<ArrayList<FuelListDetails>>> { t ->
                hideProgress()
                setAdapter(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDataFuelListError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                        binding.textNoDataFound.visibility = View.VISIBLE
                    }
                }
            }
        )

    }

    private fun setAdapter(fuelList: ArrayList<FuelListDetails>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FuelReportAdapter(fuelList,
            object : FuelReportAdapter.Callback {
                override fun gotoFuelDeatils(fuelListDetails: FuelListDetails) {
                    gotoDetails(fuelListDetails)
                }
            }
        )
        binding.recyclerView.adapter = adapter

        if (
            fuelList.size == 0
        ) {
            binding.textNoDataFound.visibility = View.VISIBLE
        } else {
            binding.textViewNoContent.visibility = View.GONE
            binding.textNoDataFound.visibility = View.GONE
        }
    }

    private fun setUpSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            Handler().postDelayed(
                {
                    getFuelList()
                    binding.swipeRefreshLayout.isRefreshing = false
                },
                1000
            )
        }
    }

    override fun addFuelReport() {
        super.addFuelReport()
        startActivity(
            Intent(
                this,
                AddFuelReport::class.java
            )
                .putExtra(
                    KeyWordsAndConstants.OPERATION_MODE,
                    OperationMode.ADD
                )
        )
    }

    fun editFuelReport(fuelListDetails: FuelListDetails) {
        startActivity(
            Intent(
                this,
                AddFuelReport::class.java
            )
                .putExtra(
                    KeyWordsAndConstants.OPERATION_MODE,
                    OperationMode.EDIT
                )
                .putExtra(
                    DATA,
                    toJson(fuelListDetails)
                )
        )
    }

    private fun gotoDetails(fuelListDetails: FuelListDetails) {
        startActivity(
            Intent(this, FuleReportDetailsActivity::class.java).putExtra(
                FUEL_DETAILS,
                toJson(fuelListDetails)
            )
        )
    }
}