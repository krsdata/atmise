package com.met.atims_reporter.ui.crash_report_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityAddCrashReportDetailsBinding
import com.met.atims_reporter.model.CrashReport
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.ui.crash_report_details.adapter.CrashReportImageAdapter
import com.met.atims_reporter.ui.crash_report_details.adapter.DriverInfoAdapter
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class CrashReportDetails : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()

    private val repository: Repository by instance()
    private lateinit var binding: ActivityAddCrashReportDetailsBinding
    private lateinit var adapter: DriverInfoAdapter
    private lateinit var crashreport: CrashReport

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_add_crash_report_details)

        intent.getStringExtra(DATA)?.let {
            crashreport = fromJson(it)
        }

        showFuelReportDeatilsActionBar()
        setPageTitle("CRASH REPORT")
        enableBackButton()
        initView()
    }

    override fun goBack() {
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        if (
            crashreport.exterior_vehicle_photo1 == ""
        ) {
            binding.linearLayoutExteriorOfVehiclePhoto.visibility = View.GONE
        } else
            binding.imageCameraExteriorLarge.setImage(
                crashreport.exterior_vehicle_photo1
            )

        if (
            crashreport.interior_vehicle_photo1 == ""
        ) {
            binding.linearLayoutInteriorOfVehiclePhoto.visibility = View.GONE
        } else
            binding.imageCameraInteriorLarge.setImage(
                crashreport.interior_vehicle_photo1
            )

        if (
            crashreport.autotag_vin_photo1 == ""
        ) {
            binding.linearLayoutVinOfVehiclePhoto.visibility = View.GONE
        } else
            binding.imageCameraVinLarge.setImage(
                crashreport.autotag_vin_photo1
            )

        if (
            crashreport.third_party_vehicle_photo1 == ""
        ) {
            binding.linearLayoutThirdPartyVehiclePhoto.visibility = View.GONE
        } else
            binding.imageCameraThirdLarge.setImage(
                crashreport.third_party_vehicle_photo1
            )

        val exteriorImageList: ArrayList<String> = ArrayList()
        crashreport.apply {
            if (exterior_vehicle_photo1 != "")
                exteriorImageList.add(exterior_vehicle_photo1)
            if (exterior_vehicle_photo2 != "")
                exteriorImageList.add(exterior_vehicle_photo2)
            if (exterior_vehicle_photo3 != "")
                exteriorImageList.add(exterior_vehicle_photo3)
            if (exterior_vehicle_photo4 != "")
                exteriorImageList.add(exterior_vehicle_photo4)
        }
        binding.recyclerViewExteriorSampleImage.apply {
            layoutManager =
                LinearLayoutManager(
                    this@CrashReportDetails,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            adapter =
                CrashReportImageAdapter(
                    exteriorImageList,
                    object : CrashReportImageAdapter.Callback {
                        override fun putToMainView(imageUrl: String) {
                            binding.imageCameraExteriorLarge.setImage(
                                imageUrl
                            )
                        }
                    }
                )
        }

        val interiorImageList: ArrayList<String> = ArrayList()
        crashreport.apply {
            if (interior_vehicle_photo1 != "")
                interiorImageList.add(interior_vehicle_photo1)
            if (interior_vehicle_photo2 != "")
                interiorImageList.add(interior_vehicle_photo2)
            if (interior_vehicle_photo3 != "")
                interiorImageList.add(interior_vehicle_photo3)
            if (interior_vehicle_photo4 != "")
                interiorImageList.add(interior_vehicle_photo4)
        }
        binding.recyclerViewInteriorSampleImage.apply {
            layoutManager =
                LinearLayoutManager(
                    this@CrashReportDetails,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            adapter =
                CrashReportImageAdapter(
                    interiorImageList,
                    object : CrashReportImageAdapter.Callback {
                        override fun putToMainView(imageUrl: String) {
                            binding.imageCameraInteriorLarge.setImage(
                                imageUrl
                            )
                        }
                    }
                )
        }

        val vinImageList: ArrayList<String> = ArrayList()
        crashreport.apply {
            if (autotag_vin_photo1 != "")
                vinImageList.add(autotag_vin_photo1)
            if (autotag_vin_photo2 != "")
                vinImageList.add(autotag_vin_photo2)
            if (autotag_vin_photo3 != "")
                vinImageList.add(autotag_vin_photo3)
            if (autotag_vin_photo4 != "")
                vinImageList.add(autotag_vin_photo4)
        }
        binding.recyclerViewVinSampleImage.apply {
            layoutManager =
                LinearLayoutManager(
                    this@CrashReportDetails,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            adapter =
                CrashReportImageAdapter(
                    vinImageList,
                    object : CrashReportImageAdapter.Callback {
                        override fun putToMainView(imageUrl: String) {
                            binding.imageCameraVinLarge.setImage(
                                imageUrl
                            )
                        }
                    }
                )
        }

        val thirdPartyImageList: ArrayList<String> = ArrayList()
        crashreport.apply {
            if (third_party_vehicle_photo1 != "")
                thirdPartyImageList.add(third_party_vehicle_photo1)
            if (third_party_vehicle_photo2 != "")
                thirdPartyImageList.add(third_party_vehicle_photo2)
            if (third_party_vehicle_photo3 != "")
                thirdPartyImageList.add(third_party_vehicle_photo3)
            if (third_party_vehicle_photo4 != "")
                thirdPartyImageList.add(third_party_vehicle_photo4)
        }
        binding.recyclerViewThirdSampleImage.apply {
            layoutManager =
                LinearLayoutManager(
                    this@CrashReportDetails,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            adapter =
                CrashReportImageAdapter(
                    thirdPartyImageList,
                    object : CrashReportImageAdapter.Callback {
                        override fun putToMainView(imageUrl: String) {
                            binding.imageCameraThirdLarge.setImage(
                                imageUrl
                            )
                        }
                    }
                )
        }

        crashreport.apply {
            binding.number.text = "Crash Report - $crash_report_data_id"
            binding.topDate.text = crash_report_date
            binding.topTime.text = crash_report_time
            binding.name.text =
                "${repository.getUserData()!!.first_name} ${repository.getUserData()!!.last_name}"
            binding.vehicleId.text = vehicleId
            binding.state.text = state_name
            binding.latitude.text = latitude
            binding.longitude.text = longitude
            binding.date.text = crash_report_date
            binding.time.text = crash_report_time
            binding.injuries.text = self_injured
            binding.wasAnyOneElseInjured.text = other_injured
            binding.howManyOtherPeopleInjured.text = number_of_injured_people
            binding.didYouContactTMC.text = contacted_tmc
            binding.didYouContactYourSupervisor.text = contacted_supervisor
            binding.wereYouInYourTruck.text = you_inside_truck
            binding.wasYourSafetyBeltFastened.text = safety_belt
            binding.statement.text = written_statement
        }

        try {
            adapter = DriverInfoAdapter(
                crashreport.driver_info,
                this
            )
            binding.recyclerViewDriverInfo.apply {
                layoutManager = LinearLayoutManager(this@CrashReportDetails)
                adapter = this@CrashReportDetails.adapter
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }
}
