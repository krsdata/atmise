package com.met.atims_reporter.ui.report

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.application.ApplicationClass
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.databinding.ActivityReportBinding
import com.met.atims_reporter.model.GetShiftReportResponse
import com.met.atims_reporter.ui.report.adapter.ShiftReportAdapter
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.activity.ShowMessageCallback
import com.met.atims_reporter.util.activity.ShowProgressCallback
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class Report : Fragment(), KodeinAware {

    private lateinit var binding: ActivityReportBinding
    private val viewModelProvider: ViewModelProvider by instance<ViewModelProvider>()
    private lateinit var viewModel: ViewModel
    override lateinit var kodein: Kodein
    private lateinit var requiredContext: Context
    private lateinit var showMessageCallback: ShowMessageCallback
    private lateinit var showProgressCallback: ShowProgressCallback

    fun setMessageCallback(showMessageCallback: ShowMessageCallback) {
        this.showMessageCallback = showMessageCallback
    }

    fun setProgressCallback(showProgressCallback: ShowProgressCallback) {
        this.showProgressCallback = showProgressCallback
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.requiredContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            ActivityReportBinding.inflate(
                inflater,
                container,
                false
            )
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        (activity as AtimsSuperActivity).showDashboardActionbar()
        (activity as AtimsSuperActivity).setPageTitle("REPORT")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        kodein = (requiredContext.applicationContext as ApplicationClass).kodein
        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            viewModelProvider
        )
            .get(
                ViewModel::class.java
            )


        getShiftReportRequest()
        setUpSwipeRefresh()
        bindToViewModel()
    }

    private fun setUpSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getShiftReportRequest()
        }
    }

    private fun getShiftReportRequest() {
        viewModel.getShiftReportRequest()
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataShiftReportList.observe(
            viewLifecycleOwner,
            Observer<Event<ArrayList<GetShiftReportResponse>>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        showProgressCallback.hideProgress()
                        binding.swipeRefreshLayout.isRefreshing = false
                        setAdapter(it.getContent()!!)
                    }

                }
            }
        )

        viewModel.mediatorLiveDataShiftReportListError.observe(
            viewLifecycleOwner,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        binding.swipeRefreshLayout.isRefreshing = false
                        processError(it.getContent()!!)
                    }

                }
            }
        )
    }

    private fun setAdapter(list: ArrayList<GetShiftReportResponse>) {
        if (list != null && list.size != 0) {
            binding.recyclerViewShiftReport.visibility = View.VISIBLE
            binding.textNoDataFound.visibility = View.GONE
            val mShiftAdapter = ShiftReportAdapter(list, requiredContext)
            binding.recyclerViewShiftReport.layoutManager = LinearLayoutManager(requiredContext)
            binding.recyclerViewShiftReport.adapter = mShiftAdapter
        } else {
            binding.textNoDataFound.visibility = View.VISIBLE
            binding.recyclerViewShiftReport.visibility = View.VISIBLE
        }

    }

    private fun processError(result: Result) {
        showProgressCallback.hideProgress()
        showMessageCallback.showMessageWithOneButton(
            message = result.getMessageToShow(),
            cancellable = true,
            buttonText = "Retry",
            callback = object : DialogUtil.CallBack {

                override fun buttonClicked() {
                    super.buttonClicked()
                    showProgressCallback.showProgress()

                }
            }
        )
    }
}
