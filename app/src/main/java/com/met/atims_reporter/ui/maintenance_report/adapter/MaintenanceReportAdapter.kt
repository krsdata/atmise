package com.met.atims_reporter.ui.maintenance_report.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.MaintenanceReportListItemBinding
import com.met.atims_reporter.model.MaintenanceReport

class MaintenanceReportAdapter(
    private val list: ArrayList<MaintenanceReport>,
    private val callback: Callback
) :
    RecyclerView.Adapter<MaintenanceReportAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            MaintenanceReportListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class ViewHolder(private val binding: MaintenanceReportListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(report: MaintenanceReport) {
            binding.apply {
                vehicleId.text = report.vehicleId
                contract.text = report.contract_period
                state.text = report.state_name
                service.text = report.service_type
            }
            binding.buttonViewDetails.setOnClickListener {
                callback.gotoDetails(report)
            }
        }
    }

    interface Callback {
        fun gotoDetails(report: MaintenanceReport)
    }
}