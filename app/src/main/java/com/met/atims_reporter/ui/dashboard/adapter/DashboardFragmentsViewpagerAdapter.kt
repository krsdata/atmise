package com.met.atims_reporter.ui.dashboard.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.met.atims_reporter.ui.faq.FAQFragment
import com.met.atims_reporter.ui.home.Home
import com.met.atims_reporter.ui.profile.ProfileFragment
import com.met.atims_reporter.ui.report.Report
import com.met.atims_reporter.util.activity.ShowMessageCallback
import com.met.atims_reporter.util.activity.ShowProgressCallback

class DashboardFragmentsViewpagerAdapter(
    fragmentManager: FragmentManager,
    showMessageCallback: ShowMessageCallback,
    showProgressCallback: ShowProgressCallback
) :
    FragmentPagerAdapter(
        fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    val home = Home()
    val faq = FAQFragment()
    val report = Report()
    val profile = ProfileFragment()

    init {
        home.setMessageCallback(showMessageCallback)
        home.setProgressCallback(showProgressCallback)
        faq.setMessageCallback(showMessageCallback)
        faq.setProgressCallback(showProgressCallback)
        profile.apply {
            this.showMessageCallback = showMessageCallback
            this.showProgressCallback = showProgressCallback
        }
        report.setMessageCallback(showMessageCallback)
        report.setProgressCallback(showProgressCallback)
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> home
            1 -> faq
            2 -> report
            3 -> profile
            else -> home
        }
    }

    override fun getCount(): Int {
        return 4
    }
}