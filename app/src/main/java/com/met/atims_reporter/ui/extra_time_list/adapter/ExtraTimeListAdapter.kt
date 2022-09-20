package com.met.atims_reporter.ui.extra_time_list.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.R
import com.met.atims_reporter.databinding.ExtraTimeListItemBinding
import com.met.atims_reporter.model.ExtraTime

class ExtraTimeListAdapter(
    private val list: ArrayList<ExtraTime>,
    private val callback: Callback
) :
    RecyclerView.Adapter<ExtraTimeListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ExtraTimeListItemBinding.inflate(
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

    inner class ViewHolder(private val binding: ExtraTimeListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(extraTime: ExtraTime) {
            binding.shiftId.text = extraTime.shiftId
            binding.date.text = extraTime.date
            binding.shiftStart.text = extraTime.startShiftTime
            binding.shiftEnd.text = extraTime.endShiftTime
            binding.tvRequestedExtraTime.text = if(extraTime.total_extra_time.trim()==""){
                "----"
            }else{
                extraTime.total_extra_time.toLowerCase()
            }
            binding.tvApprovedExtraTime.text =if(extraTime.approved_extra_time.trim()==""){
                "----"
            }else{
                extraTime.approved_extra_time.toLowerCase()
            }
            if (extraTime.statusId.trim()=="0") {
                binding.buttonRequest.visibility = View.VISIBLE
                binding.tvStatus.visibility = View.GONE
                binding.buttonRequest.text = "Request"
                binding.buttonRequest.setBackgroundResource(R.drawable.button_bg_yellow)
                binding.buttonRequest.setTextColor(Color.parseColor("#000000"))
            } else if (extraTime.extratimeStatus.equals("requestsent")) {
                binding.buttonRequest.visibility = View.GONE
                binding.tvStatus.visibility = View.VISIBLE
                binding.tvStatus.setTextColor(Color.parseColor("#24B233"))
            } else if (extraTime.extratimeStatus.toString().toLowerCase().equals("deny")) {
                binding.buttonRequest.visibility = View.GONE
                binding.tvStatus.visibility = View.VISIBLE
                binding.tvStatus.setTextColor(Color.RED)
            } else if (extraTime.extratimeStatus.toString().toLowerCase().equals("approved")) {
                binding.buttonRequest.visibility = View.GONE
                binding.tvStatus.visibility = View.VISIBLE
                binding.tvStatus.setTextColor(Color.parseColor("#24B233"))
            }
            binding.buttonRequest.setOnClickListener {
                if (extraTime.statusId=="0")
                    callback.request(extraTime)
            }
            when (extraTime.statusId) {
                "1" -> {
                    binding.tvStatus.text = "Approved"
                }
                "2" -> {
                    binding.tvStatus.text = "Denied"
                }
                "3" -> {
                    binding.tvStatus.text = "Request Sent"
                }
                else -> {

                }
            }
        }
    }

    interface Callback {
        fun request(extraTime: ExtraTime)
    }
}
