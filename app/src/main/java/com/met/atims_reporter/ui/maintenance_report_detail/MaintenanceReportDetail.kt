package com.met.atims_reporter.ui.maintenance_report_detail

import android.os.Bundle
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityMaintenanceReportDetailBinding
import com.met.atims_reporter.model.MaintenanceReport
import com.met.atims_reporter.widget.image_view.ImageView

class MaintenanceReportDetail : AtimsSuperActivity() {

    private lateinit var binding: ActivityMaintenanceReportDetailBinding
    private lateinit var report: MaintenanceReport

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_maintenance_report_detail)

        setPageTitle("MAINTENANCE REPORT")
        enableBackButton()

        intent.getStringExtra(DATA)?.let {
            report = fromJson(it)
        }

        populateUI()
    }

    override fun goBack() {
        finish()
    }

    private fun populateUI() {
        binding.apply {
            state.text = report.state_name
            contract.text = report.contract_period
            vehicle.text = report.vehicleId
            date.text = report.report_date
            serviceType.text = report.service_type
            descriptionOfRepair.text = report.repair_description
            vendor.text = report.vendor_name
            milage.text = "${report.mileage} mile/hr"
            serviceCost.text = report.service_cost
            labourHours.text = report.used_labour_hour
            note.text = report.note
            receipt.setImage(report.reciept)
            receipt.configureZoom(ImageView.ZoomEnabled.YES)
        }
    }
}