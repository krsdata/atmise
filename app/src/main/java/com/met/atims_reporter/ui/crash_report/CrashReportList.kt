package com.met.atims_reporter.ui.crash_report

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityCrashReportListBinding
import com.met.atims_reporter.model.CrashReport
import com.met.atims_reporter.ui.add_crash_report.step_one.AddCrashReportStepOne
import com.met.atims_reporter.ui.crash_report.adapter.CrashReportListAdapter
import com.met.atims_reporter.ui.crash_report_details.CrashReportDetails
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class CrashReportList : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()

    private val provider: ViewModelProvider by instance<ViewModelProvider>()
    private lateinit var viewModel: ViewModel
    private lateinit var binding: ActivityCrashReportListBinding
    private lateinit var mCrashReportListAdapter: CrashReportListAdapter
    private val list: ArrayList<CrashReport> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_crash_report_list)

        showCrashReportListActionBar()
        setPageTitle("Crash Report")
        enableBackButton()
        willHandleBackNavigation()
        initView()
        setUpSwipeRefresh()

        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            provider
        )
            .get(ViewModel::class.java)
        bindToViewModel()

        viewModel.giveRepository().checkIfLoggedIn()
    }

    private fun setUpSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            showProgress()
            viewModel.getCrashReportList()
        }
    }

    override fun onResume() {
        super.onResume()

        showProgress()
        viewModel.getCrashReportList()
    }

    override fun goBack() {
        viewModel.giveRepository().getHomeGridItems()
        startActivity(
            Intent(this, Dashboard::class.java)
        )
        finish()
    }

    override fun addCrashReport() {
        super.addCrashReport()

        startActivity(
            Intent(
                this,
                AddCrashReportStepOne::class.java
            )
        )
    }

    private fun initView() {
        mCrashReportListAdapter = CrashReportListAdapter(
            list,
            object : CrashReportListAdapter.Callback {
                override fun viewDetails(crashReport: CrashReport) {
                    startActivity(
                        Intent(this@CrashReportList, CrashReportDetails::class.java)
                            .putExtra(
                                DATA,
                                toJson(crashReport)
                            )
                    )
                }
            }
        )
        binding.rvCrashReportList.layoutManager = LinearLayoutManager(this)
        binding.rvCrashReportList.adapter = mCrashReportListAdapter
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataCrashReportList.observe(
            this,
            Observer<Event<ArrayList<CrashReport>>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        val content = it.getContent()!!
                        binding.swipeRefreshLayout.isRefreshing = false
                        list.apply {
                            clear()
                            addAll(content)
                        }
                        mCrashReportListAdapter.notifyDataSetChanged()

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

        viewModel.mediatorLiveDataCrashReportListError.observe(
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