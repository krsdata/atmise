package com.met.atims_reporter.ui.startshift.list.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.AdapterStartShiftBinding
import com.met.atims_reporter.model.StateList

class StartShiftStateAdapter(
    val mContext: Context,
    val stateList: ArrayList<StateList>?,
    val callback: Callback
) :
    RecyclerView.Adapter<StartShiftStateAdapter.ViewHolder>() {

    interface Callback {
        fun clicked(stateList: StateList?)
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
        return stateList?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    inner class ViewHolder(val binding: AdapterStartShiftBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind() {
            val mStateList = stateList?.get(adapterPosition)
            binding.shiftData.setOnClickListener {
                callback.clicked(mStateList)
            }
            binding.shiftChildNameText.text = mStateList?.state_name?:""
        }
    }
}