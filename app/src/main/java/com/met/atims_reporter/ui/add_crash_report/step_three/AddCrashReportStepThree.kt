package com.met.atims_reporter.ui.add_crash_report.step_three

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityAddCrashReportStepThreeBinding
import com.met.atims_reporter.model.AddCrashReportResponse
import com.met.atims_reporter.model.CrashReport
import com.met.atims_reporter.ui.add_crash_report.step_three.adapter.DriverInformationAdapter
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.ui.incidents.Incidents
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.UiUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import com.met.atims_reporter.widget.edittext.EditTextInputMode
import com.met.atims_reporter.widget.edittext.MultiLineConfig
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import android.content.DialogInterface

import android.text.Html
import com.met.atims_reporter.ui.crash_report.CrashReportList

class AddCrashReportStepThree : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()

    private val provider: ViewModelProvider by instance<ViewModelProvider>()
    private lateinit var viewModel: ViewModel
    private lateinit var binding: ActivityAddCrashReportStepThreeBinding
    private lateinit var driverInformationAdapter: DriverInformationAdapter
    private lateinit var crashReport: CrashReport

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_add_crash_report_step_three)
        binding.context = this

        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            provider
        ).get(ViewModel::class.java)

        setPageTitle("ADD CRASH REPORT")
        enableBackButton()
        willHandleBackNavigation()

        intent.getStringExtra(DATA)?.let {
            crashReport = fromJson(it)
        }

        bindToViewModel()

        if (!crashReport.anyPropertyDamaged || !crashReport.thirdPartyVehicleAvailable) {
            binding.buttonAddMoreThirdPartyInformation.visibility = View.GONE
        }

        binding.edtComments.apply {
            heading("Please include a written statement below on what happened, please include any injuries")
            inputMode(
                EditTextInputMode.INPUT_TEXT_MULTI_LINE,
                MultiLineConfig(3, 5)
            )
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

    fun processFinished() {
        if (binding.edtComments.getText().isEmpty()) {
            showMessageInDialog("Please provide your comments.")
            return
        }
        crashReport.written_statement = binding.edtComments.getText()

        if (this::driverInformationAdapter.isInitialized) {
            if (driverInformationAdapter.valid()) {
                crashReport.driver_info = driverInformationAdapter.getList()
                showProgress()
                crashReport.driver_info.forEach { driver ->
                    driver.passengerAdapter = null
                }
                viewModel.addCrashReport(crashReport)
            }
        } else if (!crashReport.anyPropertyDamaged || !crashReport.thirdPartyVehicleAvailable) {
            showProgress()
            crashReport.driver_info.forEach { driver ->
                driver.passengerAdapter = null
            }
            viewModel.addCrashReport(crashReport)
        } else
            showMessageInDialog("Please provide driver information.")
    }

    fun addMoreThirdPartyInformation() {
        if (this::driverInformationAdapter.isInitialized) {
            driverInformationAdapter.addOneMore()
            return
        }

        binding.recyclerViewThirdPartyInformation.apply {
            layoutManager = LinearLayoutManager(this@AddCrashReportStepThree)
            driverInformationAdapter = DriverInformationAdapter(
                this@AddCrashReportStepThree,
                this@AddCrashReportStepThree
            )
            adapter = driverInformationAdapter
        }
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataAddCrashReport.observe(
            this,
            Observer<Event<AddCrashReportResponse>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        proceedToUploadImages(
                            it.getContent()!!.crash_report_data_id
                        )
                    }
                }
            }
        )

        viewModel.mediatorLiveDataAddCrashReportError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataCrashReportFilesUpload.observe(
            this,
            Observer<Event<String>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        it.readContent()
                        hideProgress()
                        showMessageWithOneButton(
                            message = "Crash Report Submitted Successfully.",
                            cancellable = false,
                            buttonText = "Okay",
                            callback = object : DialogUtil.CallBack {

                                override fun buttonClicked() {
                                    super.buttonClicked()
                                    viewModel.giveRepository().getHomeGridItems()
                                    endAllActivities()
                                    startActivity(
                                        UiUtil.clearStackAndStartNewActivity(
                                            Intent(
                                                this@AddCrashReportStepThree,
                                                Dashboard::class.java
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

        viewModel.mediatorLiveDataCrashReportFilesUploadError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )
    }

    private fun proceedToUploadImages(reportId: String) {
        viewModel.uploadCrashReportFiles(
            crashReport.imagePaths,
            reportId
        )
    }
}