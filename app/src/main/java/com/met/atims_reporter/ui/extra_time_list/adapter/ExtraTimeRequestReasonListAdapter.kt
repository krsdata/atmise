package com.met.atims_reporter.ui.extra_time_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.ExtraTimeRequestReasonListItemBinding
import com.met.atims_reporter.widget.edittext.EditTextInputMode
import com.met.atims_reporter.widget.edittext.MultiLineConfig

class ExtraTimeRequestReasonListAdapter :
    RecyclerView.Adapter<ExtraTimeRequestReasonListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ExtraTimeRequestReasonListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(isOthers: Boolean = false) {
            if (isOthers)
                binding.editTextOtherReason.apply {
                    visibility = View.VISIBLE
                    heading("Write Your Reason")
                    inputMode(
                        EditTextInputMode.INPUT_TEXT_MULTI_LINE,
                        MultiLineConfig(
                            2,
                            3
                        )
                    )
                }
            else
                binding.editTextOtherReason.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ExtraTimeRequestReasonListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            position == 4
        )
    }
}