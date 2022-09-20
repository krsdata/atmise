package com.met.atims_reporter.ui.startshift.list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.AdapterStartShiftBinding
import com.met.atims_reporter.model.ZoneList

class StartShiftZoneAdapter(
    val mContext: Context,
    val ZoneList: ArrayList<ZoneList>?,
    val callBack: Callback
) :
    RecyclerView.Adapter<StartShiftZoneAdapter.ViewHolder>() {

    interface Callback {
        fun clicked(ZoneList: ZoneList?)
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
        return ZoneList?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    inner class ViewHolder(val binding: AdapterStartShiftBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind() {
           val mZoneList =  ZoneList?.get(adapterPosition)
            binding.shiftData.setOnClickListener {
                callBack.clicked(mZoneList)
            }
            binding.shiftChildNameText.text = mZoneList?.beat_zone_name?:""
        }
    }
}