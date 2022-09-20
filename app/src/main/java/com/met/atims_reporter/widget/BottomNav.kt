package com.met.atims_reporter.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.met.atims_reporter.R
import com.met.atims_reporter.databinding.BottomNavBinding

class BottomNav @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    enum class TABS {
        HOME, FAQ, REPORT, PROFILE
    }

    interface Callback {
        fun selectedTab(tabs: TABS)
    }

    private lateinit var binding: BottomNavBinding
    private lateinit var selectedTab: TABS
    private lateinit var callback: Callback

    fun registerForCallback(callback: Callback) {
        this.callback = callback
    }

    init {
        context?.let {
            binding = DataBindingUtil.inflate(
                it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                R.layout.bottom_nav,
                this,
                true
            )
            binding.context = this

            initialiseView()
            selectHome()
        }
    }

    private fun initialiseView() {
        hideAllSelectedBG()
        unSelectAllTabs()
    }

    private fun hideAllSelectedBG() {
        binding.viewHomeSelectedBG.visibility = View.GONE
        binding.viewFAQSelectedBG.visibility = View.GONE
        binding.viewReportSelectedBG.visibility = View.GONE
        binding.viewProfileSelectedBG.visibility = View.GONE
    }

    private fun unSelectAllTabs() {
        unSelectHome()
        unSelectFAQ()
        unSelectReport()
        unSelectProfile()
    }

    private fun unSelectHome() {
        binding.appcompatImageViewHomeUnSelected.visibility = View.VISIBLE
        binding.appcompatImageViewHomeSelected.visibility = View.INVISIBLE
        binding.viewHomeSelectedBG.visibility = View.GONE
        binding.textViewHome.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorGrey,
                null
            )
        )
    }

    fun selectHome(propagateEventInCallback: Boolean = true) {
        if (this::selectedTab.isInitialized) {
            if (selectedTab == TABS.HOME)
                return
        }
        unSelectCurrentTab()
        selectedTab = TABS.HOME
        if (propagateEventInCallback)
            selectedATab()

        binding.viewHomeSelectedBG.visibility = View.VISIBLE
        binding.appcompatImageViewHomeUnSelected.visibility = View.INVISIBLE
        binding.appcompatImageViewHomeSelected.visibility = View.VISIBLE
        binding.textViewHome.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorNewYellow,
                null
            )
        )
    }

    private fun unSelectFAQ() {
        binding.appcompatImageViewFAQUnSelected.visibility = View.VISIBLE
        binding.appcompatImageViewFAQSelected.visibility = View.INVISIBLE
        binding.viewFAQSelectedBG.visibility = View.GONE
        binding.textViewFAQ.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorGrey,
                null
            )
        )
    }

    fun selectFAQ(propagateEventInCallback: Boolean = true) {
        if (this::selectedTab.isInitialized) {
            if (selectedTab == TABS.FAQ)
                return
        }
        unSelectCurrentTab()
        selectedTab = TABS.FAQ
        if (propagateEventInCallback)
            selectedATab()

        binding.viewFAQSelectedBG.visibility = View.VISIBLE
        binding.appcompatImageViewFAQUnSelected.visibility = View.INVISIBLE
        binding.appcompatImageViewFAQSelected.visibility = View.VISIBLE
        binding.textViewFAQ.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorNewYellow,
                null
            )
        )
    }

    private fun unSelectReport() {
        binding.appcompatImageViewReportUnSelected.visibility = View.VISIBLE
        binding.appcompatImageViewReportSelected.visibility = View.INVISIBLE
        binding.viewReportSelectedBG.visibility = View.GONE
        binding.textViewReport.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorGrey,
                null
            )
        )
    }

    fun selectedReport(propagateEventInCallback: Boolean = true) {
        if (this::selectedTab.isInitialized) {
            if (selectedTab == TABS.REPORT)
                return
        }
        unSelectCurrentTab()
        selectedTab = TABS.REPORT
        if (propagateEventInCallback)
            selectedATab()

        binding.viewReportSelectedBG.visibility = View.VISIBLE
        binding.appcompatImageViewReportUnSelected.visibility = View.INVISIBLE
        binding.appcompatImageViewReportSelected.visibility = View.VISIBLE
        binding.textViewReport.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorNewYellow,
                null
            )
        )
    }

    private fun unSelectProfile() {
        binding.appcompatImageViewProfileUnSelected.visibility = View.VISIBLE
        binding.appcompatImageViewProfileSelected.visibility = View.INVISIBLE
        binding.viewProfileSelectedBG.visibility = View.GONE
        binding.textViewProfile.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorGrey,
                null
            )
        )
    }

    fun selectedProfile(propagateEventInCallback: Boolean = true) {
        if (this::selectedTab.isInitialized) {
            if (selectedTab == TABS.PROFILE)
                return
        }
        unSelectCurrentTab()
        selectedTab = TABS.PROFILE
        if (propagateEventInCallback)
            selectedATab()

        binding.viewProfileSelectedBG.visibility = View.VISIBLE
        binding.appcompatImageViewProfileUnSelected.visibility = View.INVISIBLE
        binding.appcompatImageViewProfileSelected.visibility = View.VISIBLE
        binding.textViewProfile.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorNewYellow,
                null
            )
        )
    }

    private fun selectedATab() {
        if (this::callback.isInitialized)
            callback.selectedTab(selectedTab)
    }

    private fun unSelectCurrentTab() {
        if (this::selectedTab.isInitialized)
            when (selectedTab) {
                TABS.HOME ->
                    unSelectHome()
                TABS.FAQ ->
                    unSelectFAQ()
                TABS.REPORT ->
                    unSelectReport()
                TABS.PROFILE ->
                    unSelectProfile()
            }
    }
}