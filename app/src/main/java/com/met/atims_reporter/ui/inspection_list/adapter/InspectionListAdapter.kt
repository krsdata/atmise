package com.met.atims_reporter.ui.inspection_list.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.met.atims_reporter.databinding.AdapterInspectionListBinding
import com.met.atims_reporter.model.InspectionListResponce

class InspectionListAdapter(
    private val callback: Callback, private val insepectionList: ArrayList<InspectionListResponce>
) :
    RecyclerView.Adapter<InspectionListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterInspectionListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return insepectionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(insepectionList.get(position))
    }

    inner class ViewHolder(private val binding: AdapterInspectionListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(inspectionListResponce: InspectionListResponce) {

            binding.textVehicelIdNumber.text = inspectionListResponce.vehicleId
            binding.textCurrentMilles.text = inspectionListResponce.odometer_reading
            binding.textInsuranceExp.text = inspectionListResponce.insurance_expiary_date
            binding.textRegistrationExp.text = inspectionListResponce.registration_expiry_date
            binding.textStateInspectionExp.text =
                inspectionListResponce.state_inspection_expiry_date
            binding.buttonViewDetails.setOnClickListener {
                callback.gotoFuelDeatils(inspectionListResponce)
            }
        }
    }

    interface Callback {
        fun gotoFuelDeatils(inspectionListResponce: InspectionListResponce)
    }
}