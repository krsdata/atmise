package com.met.atims_reporter.ui.extra_time_list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.ChildIncidentNumberBinding
import com.met.atims_reporter.interfaces.OnItemClickListener

class IncidentNumberAdapter(val mContext: Context,mOnItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<IncidentNumberAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChildIncidentNumberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    inner class ViewHolder(val binding: ChildIncidentNumberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var expanded = false

        fun onBind() {

        }
    }
}