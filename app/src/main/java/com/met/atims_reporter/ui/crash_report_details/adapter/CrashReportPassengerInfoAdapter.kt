package com.met.atims_reporter.ui.crash_report_details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.ChildCrashPassengerInfoBinding
import com.met.atims_reporter.model.CrashReportPassengerInfo

class CrashReportPassengerInfoAdapter(private var list: ArrayList<CrashReportPassengerInfo>) :
    RecyclerView.Adapter<CrashReportPassengerInfoAdapter.Holder>() {

    inner class Holder(private val binding: ChildCrashPassengerInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(report: CrashReportPassengerInfo) {
            binding.apply {
                name.text = report.name
                phoneNumber.text = report.passenger_phone
                address.text = report.passenger_address
                drivingLicenseInfo.text = report.passenger_licence_number
                injuries.text = report.is_injury
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ChildCrashPassengerInfoBinding.inflate(
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