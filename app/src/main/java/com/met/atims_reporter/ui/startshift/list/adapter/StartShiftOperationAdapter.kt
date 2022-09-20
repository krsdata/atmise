package com.met.atims_reporter.ui.startshift.list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.AdapterStartShiftBinding
import com.met.atims_reporter.model.OperationList

class StartShiftOperationAdapter(
    val mContext: Context,
    val operationList: ArrayList<OperationList>?,
    val callback: Callback
) :
    RecyclerView.Adapter<StartShiftOperationAdapter.ViewHolder>() {

    interface Callback {
        fun selected(operationList: OperationList?)
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
        return operationList?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    inner class ViewHolder(val binding: AdapterStartShiftBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind() {
            val mOperationList =operationList?.get(adapterPosition)
            binding.shiftData.setOnClickListener {
                callback.selected(mOperationList)
            }
            binding.shiftChildNameText.text = mOperationList?.operation_area_name?:""
        }
    }
}