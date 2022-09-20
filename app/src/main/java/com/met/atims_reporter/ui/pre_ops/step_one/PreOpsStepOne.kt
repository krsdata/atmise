package com.met.atims_reporter.ui.pre_ops.step_one

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityPreOpsStepOneBinding
import com.met.atims_reporter.model.PreOpsFinalRequest
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.ui.pre_ops.step_two.PreOpsStepTwo
import com.met.atims_reporter.util.DateUtil
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PreOpsStepOne : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()

    private val repository: Repository by instance<Repository>()
    private lateinit var binding: ActivityPreOpsStepOneBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_pre_ops_step_one)

        setPageTitle("PRE OPS")
        enableBackButton()
        binding.btnStatPreOps.setOnClickListener {
            gotoNext()
        }

        val vehicleIdd = repository.giveVehicleIdToShow()
        binding.textVehicelInfovalue.text = "#$vehicleIdd"
        binding.tvDateInfo.text = DateUtil.getDateStringForServerYMD()
        binding.tvTimeInfoValue.text = DateUtil.getTimeStringForServer(isTwelveHoursFormat = true)
        binding.tvOperatorValue.text =
            "${repository.getUserData()!!.first_name} ${repository.getUserData()!!.last_name}"
    }

    private fun gotoNext() {
        if (binding.editTextOdoMeterReading.text.toString().isEmpty()) {
            showMessageInDialog("Please provide Odometer reading.")
            return
        }
        if (binding.editTextOdoMeterReading.text.toString().trim().toInt() < 1) {
            showMessageInDialog("Please provide correct Odometer reading.")
            return
        }

        startActivity(
            Intent(this, PreOpsStepTwo::class.java)
                .putExtra(
                    DATA,
                    toJson(
                        PreOpsFinalRequest(
                            companyId = repository.getUserData()!!.company_id.toString(),
                            date = binding.tvDateInfo.text.toString(),
                            time = binding.tvTimeInfoValue.text.toString(),
                            operatorShiftTimeDetailsId = repository.giveShiftId(),
                            userId = repository.getUserData()!!.user_id.toString(),
                            user_id = repository.getUserData()!!.user_id.toString(),
                            vehicleId = repository.giveVehicleId(),
                            vehicle_id = repository.giveVehicleId(),
                            odometerReading = binding.editTextOdoMeterReading.text.toString()
                        )
                    )
                )
        )
    }
}