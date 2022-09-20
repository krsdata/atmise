package com.met.atims_reporter.ui.maintenance_report

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityMaintenanceReportBinding
import com.met.atims_reporter.model.MaintenanceReport
import com.met.atims_reporter.ui.add_maintenance_report.AddMaintenanceReport
import com.met.atims_reporter.ui.crash_report.CrashReportList
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.ui.maintenance_report.adapter.MaintenanceReportAdapter
import com.met.atims_reporter.ui.maintenance_report_detail.MaintenanceReportDetail
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MaintenanceReport : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()

    private val provider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    private lateinit var binding: ActivityMaintenanceReportBinding
    private val list: ArrayList<MaintenanceReport> = ArrayList()
    private lateinit var adapter: MaintenanceReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_maintenance_report)

        showMaintenanceReportListListActionBar()
        setPageTitle("Maintenance Request")
        enableBackButton()
        willHandleBackNavigation()
        setUpAdapter()

        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            provider
        )
            .get(ViewModel::class.java)
        bindToViewModel()
        showProgress()

        viewModel.giveRepository().checkIfLoggedIn()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMaintenanceReportList()
    }

    override fun goBack() {
        viewModel.giveRepository().getHomeGridItems()
        startActivity(
            Intent(this, Dashboard::class.java)
        )
        finish()
    }

    private fun setUpAdapter() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MaintenanceReport)
            this@MaintenanceReport.adapter = MaintenanceReportAdapter(
                list,
                object : MaintenanceReportAdapter.Callback {
                    override fun gotoDetails(report: MaintenanceReport) {
                        startActivity(
                            Intent(
                                this@MaintenanceReport,
                                MaintenanceReportDetail::class.java
                            )
                                .putExtra(
                                    DATA,
                                    toJson(report)
                                )
                        )
                    }
                }
            )
            adapter = this@MaintenanceReport.adapter
        }
    }

    override fun addMaintenanceReport() {
        viewModel.giveRepository().getServiceTypes()
        viewModel.giveRepository().getStateList()
        viewModel.giveRepository().getVehicleList()
        viewModel.giveRepository().getMaintenanceRequestTypeList()
        viewModel.giveRepository().getVendorsList()

        super.addMaintenanceReport()
        startActivity(
            Intent(
                this@MaintenanceReport,
                AddMaintenanceReport::class.java
            )
        )
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataMaintenanceReportList.observe(
            this,
            Observer<Event<ArrayList<MaintenanceReport>>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        val content = it.getContent()!!
                        list.clear()
                        list.addAll(content)
                        adapter.notifyDataSetChanged()

                        if (
                            content.size == 0
                        ) {
                            binding.textViewNoContent.visibility = View.VISIBLE
                        } else {
                            binding.textViewNoContent.visibility = View.GONE
                        }
                    }
                }
            }
        )

        viewModel.mediatorLiveDataMaintenanceReportListError.observe(
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
    }
}