package com.met.atims_reporter.ui.add_inspection.step_three

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.databinding.ActivityAddInspectionStepThreeBinding
import com.met.atims_reporter.model.InspectionFinalRequest
import com.met.atims_reporter.model.ToolListResponceInsp
import com.met.atims_reporter.ui.add_inspection.step_four.AddInspectionStepFour
import com.met.atims_reporter.ui.add_inspection.step_three.adapter.InspectionThreeAdapter
import com.met.atims_reporter.ui.add_inspection.step_three.adapter.InspectionToolsAdapter
import com.met.atims_reporter.ui.inspection_list.InspectionList
import com.met.atims_reporter.util.DialogUtil

import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AddInspectionStepThree : AtimsSuperActivity(), KodeinAware {

    private lateinit var binding: ActivityAddInspectionStepThreeBinding
    private lateinit var mInspectionToolsAdapter: InspectionToolsAdapter
    private lateinit var adapter: InspectionThreeAdapter
    override val kodein: Kodein by kodein()
    private val viewModelProvider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_add_inspection_step_three)
        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)
        binding.context = this
        setPageTitle("INSPECTIONS - TOOLS")
        enableBackButton()
        willHandleBackNavigation()
        getToolList()
        bindToViewModel()
    }



    fun moveToNext() {
        startActivity(
            Intent(this, AddInspectionStepFour::class.java)
                .putExtra(
                    KeyWordsAndConstants.DATA,
                    toJson(
                        fromJson<InspectionFinalRequest>(
                            intent.getStringExtra(KeyWordsAndConstants.DATA)!!
                        ).apply {
                            inspectionsTools.clear()
                            inspectionsTools.addAll(
                                adapter.getList()
                            )
                        }
                    )
                )
        )
    }


    private fun getToolList() {
        showProgress()
        viewModel.getToolList()
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataToolList.observe(
            this,
            Observer<Event<ArrayList<ToolListResponceInsp>>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        setAdapter(t.getContent() ?: ArrayList())
                    }
                }
            }
        )
        viewModel.mediatorLiveDataToolListError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

    }


    private fun setAdapter(toolListResponce: ArrayList<ToolListResponceInsp>) {
        if (
            toolListResponce.size == 0
        ) {
            showMessageWithOneButton(
                message = "We are unable to get some data for you to proceed. Please contact admin.",
                cancellable = false,
                callback = object : DialogUtil.CallBack {
                    override fun buttonClicked() {
                        super.buttonClicked()

                        finish()
                    }
                }
            )
            return
        }
        binding.rvInspectionStep3List.layoutManager = LinearLayoutManager(this)
        adapter = InspectionThreeAdapter(toolListResponce)
        binding.rvInspectionStep3List.adapter = adapter
    }

    override fun goBack() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.back_alert_title_txt)
        builder.setMessage(R.string.back_alert_txt)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(Html.fromHtml("<font color='#FFC405'>Yes</font>")) { dialog, arg1 ->
            viewModel.giveRepository().getHomeGridItems()
            startActivity(
                Intent(this, InspectionList::class.java)
            )
            finish()
        }
        builder.setNegativeButton(Html.fromHtml("<font color='#FFC405'>No</font>")) { dialog, arg1 ->

        }
        builder.create()
        builder.show()
    }
}