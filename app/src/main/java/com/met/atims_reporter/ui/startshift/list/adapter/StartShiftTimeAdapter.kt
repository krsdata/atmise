package com.met.atims_reporter.ui.startshift.list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.AdapterStartShiftBinding
import com.met.atims_reporter.model.ShiftType

class StartShiftTimeAdapter(
    val mContext: Context,
    val list: ArrayList<ShiftType>?,
    val callBack: Callback
) :
    RecyclerView.Adapter<StartShiftTimeAdapter.ViewHolder>() {

    interface Callback {
        fun clicked(item: ShiftType?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterStartShiftBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    inner class ViewHolder(val binding: AdapterStartShiftBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind() {
            val item:ShiftType? = list?.get(adapterPosition)
            binding.shiftData.setOnClickListener {
                callBack.clicked(item)
            }
            binding.shiftChildNameText.text = item?.operation_shifts_name
        }
    }
}