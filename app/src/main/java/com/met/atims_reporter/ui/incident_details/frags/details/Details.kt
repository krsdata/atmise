package com.met.atims_reporter.ui.incident_details.frags.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.databinding.FragmentDetailsBinding
import com.met.atims_reporter.model.IncidentDetails
import com.met.atims_reporter.model.IncidentDetailsListItem
import com.met.atims_reporter.ui.incident_details.adapter.IncidentDeatilsListAdapter

/**
 * A simple [Fragment] subclass.
 */
class Details : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var incidentDetailsList: ArrayList<IncidentDetailsListItem>
    private lateinit var incidentDetailsListAdapter: IncidentDeatilsListAdapter
     var incidentDetails: IncidentDetails? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDataIntoList()
        setAdapter()
    }

    private fun setAdapter() {
        binding.recycleViewIncidentDetails.layoutManager = LinearLayoutManager(activity)
        incidentDetailsListAdapter = IncidentDeatilsListAdapter(incidentDetailsList)
        binding.recycleViewIncidentDetails.adapter = incidentDetailsListAdapter
    }

    private fun setDataIntoList() {
        val vehicleInformationList: ArrayList<IncidentDetailsListItem> = ArrayList()
        incidentDetails?.vehicleInformation?.let { _ ->
            incidentDetails?.vehicleInformation?.forEachIndexed { index, it ->
                if (it.plate_no.isNotEmpty() || it.color.isNotEmpty() || it.vehicle_type.isNotEmpty()) {
                    vehicleInformationList.add(
                        IncidentDetailsListItem(
                            "Vehicle Involved ${index + 1}",
                            ""
                        )
                    )
                    if (it.plate_no.isNotEmpty())
                        vehicleInformationList.add(
                            IncidentDetailsListItem(
                                "   Plate Number:",
                                it.plate_no
                            )
                        )
                    if (it.color.isNotEmpty())
                        vehicleInformationList.add(
                            IncidentDetailsListItem(
                                "   Color:",
                                it.color
                            )
                        )
                    if (it.vehicle_type.isNotEmpty())
                        vehicleInformationList.add(
                            IncidentDetailsListItem(
                                "   Vehicle Type:",
                                it.vehicle_type
                            )
                        )
                }
            }
        }
        incidentDetailsList = ArrayList()
        incidentDetails?.Incidents_report_data_id?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "IND#:",
                    it
                )
            )
        }
        incidentDetails?.first_name?.let { firstName ->
            incidentDetails?.last_name?.let { lastName ->
                if (firstName.isNotEmpty() && lastName.isNotEmpty())
                    incidentDetailsList.add(
                        IncidentDetailsListItem(
                            "Operator Name:",
                            "$firstName $lastName"
                        )
                    )
            }
        }
        incidentDetails?.phone?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Phone:",
                    it
                )
            )
        }
        incidentDetails?.ID?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Vehicle ID:",
                    it
                )
            )
        }
        incidentDetails?.call_at?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Call At:",
                    it
                )
            )
        }
        incidentDetails?.call_started?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Call Started:",
                    it
                )
            )
        }
        incidentDetails?.call_completed?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Call Completed:",
                    it
                )
            )
        }
        incidentDetails?.incedent_time?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Incident Time:",
                    it
                )
            )
        }
        incidentDetails?.incedent_type_id?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Incident Type Id:",
                    it
                )
            )
        }
        incidentDetails?.incident_type_name?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Incident Type:",
                    it
                )
            )
        }
        incidentDetails?.incident_no?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Incident No:",
                    "${incidentDetails?.incident_no_prefix}"+it
                )
            )
        }
        incidentDetails?.latitude?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Latitude:",
                    it
                )
            )
        }
        incidentDetails?.longitude?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Longitude:",
                    it
                )
            )
        }
        incidentDetails?.route_name?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Route:",
                    it
                )
            )
        }
        incidentDetails?.traffic_direction?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Traffic Direction:",
                    it
                )
            )
        }
        incidentDetails?.state_name?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "State:",
                    it
                )
            )
        }
        incidentDetails?.milage_in?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Mileage In:",
                    it
                )
            )
        }
        incidentDetails?.milage_out?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Mileage Out:",
                    it
                )
            )
        }
        incidentDetails?.mile_marker?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Mile Marker:",
                    it
                )
            )
        }
        incidentDetails?.property_damage?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Property Damage:",
                    it
                )
            )
        }
        incidentDetails?.crash_Inv?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Secondary crash involved:",
                    it
                )
            )
        }
        incidentDetails?.assist_type?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Assist Type:",
                    it
                )
            )
        }
        incidentDetails?.vendor_id?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Vendor id:",
                    it
                )
            )
        }
        incidentDetails?.contract_id?.let {
            if (it.isEmpty())
                return@let
            incidentDetailsList.add(
                IncidentDetailsListItem(
                    "Contract id:",
                    it
                )
            )
        }
        incidentDetailsList.addAll(vehicleInformationList)
    }
}
