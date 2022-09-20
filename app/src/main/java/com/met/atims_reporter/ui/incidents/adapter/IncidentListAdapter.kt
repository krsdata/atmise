package com.met.atims_reporter.ui.incidents.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.R
import com.met.atims_reporter.databinding.ChildIncidentBinding
import com.met.atims_reporter.model.IncidentDetails


class IncidentListAdapter(
    private val incidentList: ArrayList<IncidentDetails>,
    private val callback: Callback
) :
    RecyclerView.Adapter<IncidentListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChildIncidentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return incidentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    inner class ViewHolder(private val binding: ChildIncidentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind() {
            val incidentDetails = incidentList[adapterPosition]
            binding.tvViewIncident.setOnClickListener {
                callback.gotoDetails(incidentDetails)
            }
            binding.buttonUpdateIncident.setOnClickListener {
                callback.updateIncident(incidentDetails)
            }
            binding.buttonAccept.setOnClickListener {
                callback.accept(incidentDetails)
            }
            binding.buttonDecline.setOnClickListener {
                callback.decline(incidentDetails)
            }
            binding.buttonClose.setOnClickListener {
                callback.close(incidentDetails)
            }
            if (incidentDetails.Incidents_report_data_id == null || incidentDetails.Incidents_report_data_id == "")
                binding.linearLayoutInd.visibility = View.GONE
            else
                binding.Ind.text = incidentDetails.Incidents_report_data_id
            if (incidentDetails.call_at == null || incidentDetails.call_at == "")
                binding.linearLayoutStartTime.visibility = View.GONE
            else
            binding.time.text = incidentDetails.call_at
            if (incidentDetails.ID == null || incidentDetails.ID == "")
                binding.linearLayoutVehicleId.visibility = View.GONE
            else
            binding.textVehicelId.text = incidentDetails.ID
            if (incidentDetails.state_name == null || incidentDetails.state_name == "")
                binding.linearLayoutStateName.visibility = View.GONE
            else
            binding.textState.text = incidentDetails.state_name
            if (incidentDetails.route_name == null || incidentDetails.route_name == "")
                binding.linearLayoutRoute.visibility = View.GONE
            else
            binding.textRoute.text = incidentDetails.route_name
            if (incidentDetails.mile_marker == null || incidentDetails.mile_marker == "")
                binding.linearLayoutMM.visibility = View.GONE
            else
            binding.textMm.text = incidentDetails.mile_marker
            if (incidentDetails.traffic_direction == null || incidentDetails.traffic_direction == "")
                binding.linearLayoutDirection.visibility = View.GONE
            else
            binding.textDirection.text = incidentDetails.traffic_direction
            if (incidentDetails.lane_location_name == null || incidentDetails.lane_location_name == "")
                binding.linearLayoutLanesEffected.visibility = View.GONE
            else
            binding.textLanesEffected.text = incidentDetails.lane_location_name
            if (incidentDetails.incident_status == "")
                binding.linearLayoutStatus.visibility = View.GONE
            else
            binding.textStatus.text = incidentDetails.incident_status

            binding.buttonUpdateIncident.visibility = View.GONE
            binding.buttonAccept.visibility = View.GONE
            binding.buttonDecline.visibility = View.GONE
            binding.buttonClose.visibility = View.GONE

            when (incidentDetails.incident_status) {
                "Assigned" -> {
                    binding.buttonAccept.visibility = View.VISIBLE
                    binding.buttonDecline.visibility = View.VISIBLE
                }
                "Accepted" -> {
                    binding.buttonUpdateIncident.visibility = View.VISIBLE
                }
                "En-Route" -> {
                    binding.buttonUpdateIncident.visibility = View.VISIBLE
                }
                "On Scene" -> {
                    if(incidentDetails.is_dispatched_incedent=="0"){
                        binding.buttonUpdateIncident.setText("Edit Incident")
                        binding.buttonUpdateIncident.visibility = View.VISIBLE
                    }else{
                        binding.buttonUpdateIncident.visibility = View.GONE
                    }
                    binding.textStatus.text="Open"
                    binding.textStatus.setTextColor(Color.WHITE)
                    binding.textStatus.setBackgroundColor(Color.RED)
                    binding.buttonClose.visibility = View.VISIBLE
                    binding.buttonClose.setTextColor(Color.BLACK)
                    binding.buttonClose.setBackgroundResource(R.drawable.button_yellow)
                }
                "Denied"->{
                    binding.textStatus.setBackgroundColor(Color.RED)
                }
            }
        }
    }

    interface Callback {
        fun updateIncident(incidentDetails: IncidentDetails)
        fun gotoDetails(incidentDetails: IncidentDetails)
        fun accept(incidentDetails: IncidentDetails)
        fun decline(incidentDetails: IncidentDetails)
        fun close(incidentDetails: IncidentDetails)
    }
}