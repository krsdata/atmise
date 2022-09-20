package com.met.atims_reporter.ui.crash_report_details.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.CrashReportDriverInfoListItemBinding
import com.met.atims_reporter.model.CrashReportDriverInfo

class DriverInfoAdapter(
    private var list: ArrayList<CrashReportDriverInfo>,
    private var context: Context
) : RecyclerView.Adapter<DriverInfoAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: CrashReportDriverInfoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(crashReportDriverInfo: CrashReportDriverInfo) {

            binding.apply {
                textViewHeading.text = "Third Party Information Set ${adapterPosition + 1}:"
                name.text = crashReportDriverInfo.dirver_name
                phoneNumber.text = crashReportDriverInfo.driver_phone
                address.text = crashReportDriverInfo.driver_address
                licenseInfo.text = crashReportDriverInfo.driver_licence_number
                insuranceInfo.text = crashReportDriverInfo.vehicle_insurance_info
                insuranceExpDate.text = crashReportDriverInfo.insurance_expriary_date
                injuries.text = crashReportDriverInfo.is_injury
                recyclerViewPassenger.layoutManager = LinearLayoutManager(context)
                recyclerViewPassenger.adapter = CrashReportPassengerInfoAdapter(
                    crashReportDriverInfo.passenger_info
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CrashReportDriverInfoListItemBinding.inflate(
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
        holder.bind(
            list[position]
        )
    }
}