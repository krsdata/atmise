package com.met.atims_reporter.ui.fuel_report.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.FuelReportListItemBinding
import com.met.atims_reporter.model.FuelListDetails

class FuelReportAdapter(private val fuelList: ArrayList<FuelListDetails>,
    private val callback: Callback
) :
    RecyclerView.Adapter<FuelReportAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FuelReportListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return fuelList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fuelList.get(position))
    }

    inner class ViewHolder(private val binding: FuelReportListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(fuelListDetails: FuelListDetails) {
            binding.textDate.text = fuelListDetails.refueling_date
            binding.textTime.text = fuelListDetails.refueling_time
            binding.textFuelQty.text = fuelListDetails.fuel_quantity
            binding.textCostPerLtr.text = fuelListDetails.cost_per_galon
            binding.buttonViewDetails.setOnClickListener {
                callback.gotoFuelDeatils(fuelListDetails)
            }
        }
    }

    interface Callback {
        fun gotoFuelDeatils(fuelListDetails: FuelListDetails)
    }
}