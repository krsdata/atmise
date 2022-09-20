package com.met.atims_reporter.ui.add_extra_time

import android.content.Intent
import android.os.Bundle
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.databinding.ActivityAddExtraTimeBinding
import com.met.atims_reporter.ui.add_fuel_report.ViewModel
import com.met.atims_reporter.ui.add_fuel_report.ViewModelProvider
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.android.kodein



class AddExtraTimeActivity : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()
    private lateinit var viewModel: ViewModel
    private lateinit var binding: ActivityAddExtraTimeBinding
    private val viewModelProvider: ViewModelProvider by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_add_extra_time)
        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)
        showExtraTimeListActionBar()
        setPageTitle("ADD EXTRA TIME")
        enableBackButton()
        willHandleBackNavigation()
    }

    override fun addExtraTimeReport() {
        super.addExtraTimeReport()
        val intent = Intent(this, AddExtraTimeActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0,0)
    }
}