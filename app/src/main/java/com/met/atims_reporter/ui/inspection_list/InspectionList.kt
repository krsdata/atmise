package com.met.atims_reporter.ui.inspection_list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.databinding.ActivityInspectionListBinding
import com.met.atims_reporter.model.InspectionListResponce
import com.met.atims_reporter.ui.add_inspection.step_one.AddInspectionStepOne
import com.met.atims_reporter.ui.crash_report.CrashReportList
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.ui.inspection_details.one.InspectionDetailsOne
import com.met.atims_reporter.ui.inspection_list.adapter.InspectionListAdapter
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class InspectionList : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()
    private val viewModelProvider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    private lateinit var binding: ActivityInspectionListBinding
    private lateinit var mInspectionListAdapter: InspectionListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_inspection_list)
        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)
        showInspectionListActionBar()
        setPageTitle("INSPECTIONS")
        enableBackButton()
        willHandleBackNavigation()
        bindToViewModel()

        viewModel.giveRepository().checkIfLoggedIn()
    }

    override fun onResume() {
        super.onResume()

        getInspectionList()
    }

    override fun goBack() {
        viewModel.giveRepository().getHomeGridItems()
        startActivity(
            Intent(this, Dashboard::class.java)
        )
        finish()
    }

    override fun addInspection() {
        super.addInspection()

        viewModel.giveRepository().clearInspectionData()

        startActivity(
            Intent(
                this,
                AddInspectionStepOne::class.java
            )
        )
    }

    private fun getInspectionList() {
        showProgress()
        viewModel.getInspectionList()
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataInspectionList.observe(
            this,
            Observer<Event<ArrayList<InspectionListResponce>>> { t ->
                hideProgress()
                setAdapter(t.getContent() ?: ArrayList())
            }
        )
        viewModel.mediatorLiveDataInspectionListError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                        binding.textViewNoContent.visibility = View.GONE
                    }
                }
            }
        )

    }

    private fun setAdapter(insepectionList: ArrayList<InspectionListResponce>) {
        mInspectionListAdapter = InspectionListAdapter(object : InspectionListAdapter.Callback {
            override fun gotoFuelDeatils(inspectionListResponce: InspectionListResponce) {
                startActivity(
                    Intent(
                        this@InspectionList,
                        InspectionDetailsOne::class.java
                    ).putExtra(
                        KeyWordsAndConstants.DATA,
                        toJson(inspectionListResponce)
                    )
                )
            }

        }, insepectionList)
        binding.rvInspectionList.setHasFixedSize(true)
        binding.rvInspectionList.layoutManager = LinearLayoutManager(this)
        binding.rvInspectionList.adapter = mInspectionListAdapter
        binding.rvInspectionList.isNestedScrollingEnabled = false



        if (
            insepectionList.size == 0
        ) {
            binding.textViewNoContent.visibility = View.VISIBLE
        } else {
            binding.textViewNoContent.visibility = View.GONE
        }
    }
}