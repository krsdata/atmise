package com.met.atims_reporter.ui.pre_ops.step_three

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityPreOpsStepThreeBinding
import com.met.atims_reporter.model.PreOpsFinalRequest
import com.met.atims_reporter.model.ToolListResponce
import com.met.atims_reporter.ui.pre_ops.step_four.PreOpsStepFour
import com.met.atims_reporter.ui.pre_ops.step_three.adapter.PreOpsThreeAdapter
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PreOpsStepThree : AtimsSuperActivity(), KodeinAware {

    private lateinit var binding: ActivityPreOpsStepThreeBinding
    private lateinit var adapter: PreOpsThreeAdapter
    override val kodein: Kodein by kodein()
    private val viewModelProvider: ViewModelProvider by instance<ViewModelProvider>()
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_pre_ops_step_three)

        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)
        setPageTitle("PRE OPS - TOOLS")
        enableBackButton()
        initView()
        getToolList()
        bindToViewModel()
    }

    private fun initView() {
        binding.btnNext.setOnClickListener {
            gotoNext()
        }

    }

    private fun getToolList() {
        showProgress()
        viewModel.getToolList()
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataToolList.observe(
            this,
            Observer<Event<ArrayList<ToolListResponce>>> { t ->
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

    private fun gotoNext() {
        startActivity(
            Intent(this, PreOpsStepFour::class.java)
                .putExtra(
                    DATA,
                    toJson(
                        fromJson<PreOpsFinalRequest>(
                            intent.getStringExtra(DATA)!!
                        ).apply {
                            preOpsTools.clear()
                            preOpsTools.addAll(
                                adapter.getList()
                            )
                        }
                    )
                )
        )
    }

    private fun setAdapter(toolListResponce: ArrayList<ToolListResponce>) {
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
        binding.preOpsTools.layoutManager = LinearLayoutManager(this)
        adapter = PreOpsThreeAdapter(toolListResponce)
        binding.preOpsTools.adapter = adapter
    }

    override fun goBack() {
        finish()
    }
}