package com.met.atims_reporter.ui.faq


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.R
import com.met.atims_reporter.application.ApplicationClass
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.databinding.FragmentFaqBinding
import com.met.atims_reporter.model.FaqResponce
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.ui.faq.adapter.FAQAdapter
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.activity.ShowMessageCallback
import com.met.atims_reporter.util.activity.ShowProgressCallback
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class FAQFragment : Fragment(), KodeinAware {

    private lateinit var binding: FragmentFaqBinding
    private val viewModelProvider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    override lateinit var kodein: Kodein
    private lateinit var requiredContext: Context
    private  var showMessageCallback: ShowMessageCallback?=null
    private  var showProgressCallback: ShowProgressCallback?=null

    fun setMessageCallback(showMessageCallback: ShowMessageCallback?) {
        this.showMessageCallback = showMessageCallback
    }

    fun setProgressCallback(showProgressCallback: ShowProgressCallback?) {
        this.showProgressCallback = showProgressCallback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_faq, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.requiredContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        kodein = (requiredContext.applicationContext as ApplicationClass).kodein
        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            viewModelProvider
        )
            .get(
                com.met.atims_reporter.ui.faq.ViewModel::class.java
            )

        getFaqList()
        bindToViewModel()
    }

    override fun onResume() {
        super.onResume()

        (activity as AtimsSuperActivity).showDashboardActionbar()
        (activity as AtimsSuperActivity).setPageTitle("FAQ")
    }

    private fun getFaqList(){
        viewModel.getFaqList()
    }
    private fun bindToViewModel() {
        viewModel.mediatorLiveDataFaqList.observe(
            this@FAQFragment,
            Observer<Event<ArrayList<FaqResponce>>> { t ->
                t?.let {
                    if (it.shouldReadContent()){
                        showProgressCallback?.hideProgress()
                        setAdapter(it.getContent()!!)
                    }

                }
            }
        )

        viewModel.mediatorLiveDataFaqListError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        processError(it.getContent()!!)
                }
            }
        )
    }

    private fun setAdapter(faqList:ArrayList<FaqResponce>) {
        val mFAQAdapter = FAQAdapter(activity as Dashboard,faqList)
        val layoutManager = binding.rvFaq.layoutManager as LinearLayoutManager
        binding.rvFaq.layoutManager = layoutManager
        binding.rvFaq.adapter = mFAQAdapter
        binding.rvFaq.isNestedScrollingEnabled = false
    }

    private fun processError(result: Result) {
        showProgressCallback?.hideProgress()
        showMessageCallback?.showMessageWithOneButton(
            message = result.getMessageToShow(),
            cancellable = true,
            buttonText = "Retry",
            callback = object : DialogUtil.CallBack {

                override fun buttonClicked() {
                    super.buttonClicked()
                    showProgressCallback?.showProgress()

                }
            }
        )
    }


}
