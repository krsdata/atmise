package com.met.atims_reporter.ui.report.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.AdapterShiftReportBinding
import com.met.atims_reporter.model.FuelListDetails
import com.met.atims_reporter.model.GetShiftReportResponse

class ShiftReportAdapter(
    private val list: ArrayList<GetShiftReportResponse>, private val mContext: Context
) :
    RecyclerView.Adapter<ShiftReportAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterShiftReportBinding.inflate(
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
        holder.bind(list.get(position))
    }

    inner class ViewHolder(private val binding: AdapterShiftReportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(getShiftDetails: GetShiftReportResponse) {
            binding.textShiftSt.text = getShiftDetails.shiftStart
            binding.textShiftEnd.text = getShiftDetails.shiftEnd
            binding.textDuration.text = getShiftDetails.totalDuration
            binding.btnShiftDate.text = getShiftDetails.shiftStart

            val mFAQAdapter = ShiftIncidentReportAdapter(arrayListOf(getShiftDetails.status))
            binding.recycleViewIncidentList.layoutManager = LinearLayoutManager(mContext)
            binding.recycleViewIncidentList.adapter = mFAQAdapter

        }
    }

    interface Callback {
        fun gotoFuelDeatils(fuelListDetails: FuelListDetails)
    }
}