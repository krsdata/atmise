package com.met.atims_reporter.ui.crash_report.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.AdapterCrashReportBinding
import com.met.atims_reporter.model.CrashReport

class CrashReportListAdapter(
    private val list: ArrayList<CrashReport>,
    private val callback: Callback
) :
    RecyclerView.Adapter<CrashReportListAdapter.Holder>() {

    interface Callback {
        fun viewDetails(crashReport: CrashReport)
    }

    inner class Holder(private val binding: AdapterCrashReportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(crashReport: CrashReport) {
            binding.truckId.text = "#${crashReport.vehicleId}"
            binding.state.text = crashReport.state_name
            binding.latitude.text = crashReport.latitude
            binding.longitude.text = crashReport.longitude
            binding.apply {
                buttonViewDetails.setOnClickListener {
                    callback.viewDetails(crashReport)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            AdapterCrashReportBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(list[position])
    }
}