package com.met.atims_reporter.ui.inspection_details.one

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.databinding.ActivityInspectionDetailsBinding
import com.met.atims_reporter.model.InspectionListDetailsResponce
import com.met.atims_reporter.model.InspectionListResponce
import com.met.atims_reporter.model.QuestionList
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.ui.inspection_details.one.adapter.InspectionVehicelAdapter
import com.met.atims_reporter.ui.inspection_details.two.InspectionDetailsTwo
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class InspectionDetailsOne : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()
    private val viewModelProvider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    private lateinit var binding: ActivityInspectionDetailsBinding
    private lateinit var inspectionListResponce: InspectionListResponce
    private lateinit var inspectionVehicelAdapter: InspectionVehicelAdapter
    private lateinit var inspectionListDetailsResponce: InspectionListDetailsResponce

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_inspection_details)
        binding.context = this

        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)

        intent.getStringExtra(KeyWordsAndConstants.DATA)?.let {
            inspectionListResponce = fromJson(it)
        }
        setPageTitle("INSPECTION DETAILS")
        enableBackButton()
        getInspectionList()
        bindToViewModel()
    }

    override fun goBack() {
        viewModel.giveRepository().getHomeGridItems()
        startActivity(
            Intent(
                this,
                Dashboard::class.java
            )
        )
        finish()
    }

    fun gotoSummary() {
        startActivity(
            Intent(
                this,
                InspectionDetailsTwo::class.java
            ).putExtra(
                KeyWordsAndConstants.DATA,
                toJson(inspectionListDetailsResponce)
            )
        )
    }


    private fun getInspectionList() {
        showProgress()
        viewModel.getInspectionDetails(inspectionListResponce.inspection_reports_id)
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataInspectionDetailsList.observe(
            this,
            Observer<Event<InspectionListDetailsResponce>> { t ->
                if (t.shouldReadContent()) {
                    hideProgress()
                    loadDetailsData(inspectionListDetailsResponce = t.getContent()!!)
                }
            }
        )
        viewModel.mediatorLiveDataInspectionDeatilsError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

    }

    private fun loadDetailsData(inspectionListDetailsResponce: InspectionListDetailsResponce) {
        this.inspectionListDetailsResponce = inspectionListDetailsResponce
        binding.apply {
            textNameVehicelId.text =
                inspectionListDetailsResponce.reportUserData.first_name + " " + inspectionListDetailsResponce.reportUserData.last_name + " " + inspectionListDetailsResponce.reportUserData.vehicleId
            textDateTime.text =
                inspectionListDetailsResponce.reportUserData.inspection_date + " | " + inspectionListDetailsResponce.reportUserData.inspection_time
            textName.text =
                inspectionListDetailsResponce.reportUserData.first_name + " " + inspectionListDetailsResponce.reportUserData.last_name
            textVehicleId.text = inspectionListDetailsResponce.reportUserData.vehicleId
            textDate.text = inspectionListDetailsResponce.reportUserData.inspection_date
            textTime.text = inspectionListDetailsResponce.reportUserData.inspection_time
            textOdmeter.text = inspectionListDetailsResponce.reportUserData.odometer_reading
            textInsurance.text = inspectionListDetailsResponce.reportUserData.insurance_expiary_date
            textRegisExp.text =
                inspectionListDetailsResponce.reportUserData.registration_expiry_date
            textStateExp.text =
                inspectionListDetailsResponce.reportUserData.state_inspection_expiry_date

            binding.imageInsurance.setImage(
                inspectionListDetailsResponce.reportUserData.insurance_image
            )
            binding.imageRegistration.setImage(
                inspectionListDetailsResponce.reportUserData.registration_image
            )
            binding.imageStateInspection.setImage(
                inspectionListDetailsResponce.reportUserData.inspection_image
            )
            binding.imagePlateNumber.setImage(
                inspectionListDetailsResponce.reportUserData.plate_image
            )
        }
        setAdapter(inspectionListDetailsResponce.questionsResult)
    }

    private fun setAdapter(insepectionList: ArrayList<QuestionList>) {
        inspectionVehicelAdapter = InspectionVehicelAdapter(insepectionList)
        binding.rlVehicel.setHasFixedSize(true)
        binding.rlVehicel.layoutManager = LinearLayoutManager(this)
        binding.rlVehicel.adapter = inspectionVehicelAdapter
        binding.rlVehicel.isNestedScrollingEnabled = false
    }
}
