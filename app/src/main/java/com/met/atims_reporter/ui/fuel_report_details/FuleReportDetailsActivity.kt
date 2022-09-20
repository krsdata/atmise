package com.met.atims_reporter.ui.fuel_report_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.databinding.ActivityFuleReportDetailsBinding
import com.met.atims_reporter.model.FuelListDetails

class FuleReportDetailsActivity : AtimsSuperActivity() {

    companion object {
        const val FUEL_DETAILS = "fuel_details"
    }

    private lateinit var binding: ActivityFuleReportDetailsBinding
    private lateinit var fuelDetails: FuelListDetails
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_fule_report_details)

        showFuelReportDeatilsActionBar()
        setPageTitle("FUEL REPORT")
        enableBackButton()
        initView()
    }

    override fun goBack() {
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        intent?.let {
            fuelDetails = fromJson(it.getStringExtra(FUEL_DETAILS)!!)
            binding.textViewVehicleId.text = fuelDetails.vehicleId
            binding.textFuelType.text = fuelDetails.fuel_type
            binding.textDate.text = fuelDetails.refueling_date
            binding.textTime.text = fuelDetails.refueling_time
            binding.textLat.text = fuelDetails.latitude
            binding.textLong.text = fuelDetails.longitude
            binding.textOdometerReading.text = fuelDetails.odo_meter_reading
            binding.textCostperGallon.text = fuelDetails.cost_per_galon
            binding.textTotalCost.text = fuelDetails.total_cost
            if (fuelDetails.fuel_taken_tank != "" || fuelDetails.fuel_taken_tank != "null") {
                binding.fuelTakenInTank.text = fuelDetails.fuel_taken_tank
                binding.llCan.visibility = View.GONE
            } else {
                binding.textFuelTakenCan.text = fuelDetails.fuel_taken_can
                binding.llTank.visibility = View.GONE
            }
            binding.textNote.text = fuelDetails.note
            if (fuelDetails.report_for == "reciept") {
                binding.imgReceipt.setImage(fuelDetails.reciept)
                binding.textViewReceiptOrPump.text = "Receipt"
            } else {
                binding.imgReceipt.setImage(fuelDetails.pumping)
                binding.textViewReceiptOrPump.text = "Pump"
            }
        }

    }


}
