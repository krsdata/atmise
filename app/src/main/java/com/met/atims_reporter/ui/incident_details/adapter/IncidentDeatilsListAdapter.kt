package com.met.atims_reporter.ui.incident_details.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.ChildIncidentDetailsBinding
import com.met.atims_reporter.model.IncidentDetailsListItem


class IncidentDeatilsListAdapter(
    private val incidentDeatilsList: ArrayList<IncidentDetailsListItem>
) : RecyclerView.Adapter<IncidentDeatilsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChildIncidentDetailsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return incidentDeatilsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(incidentDeatilsList[position])
    }

    inner class ViewHolder(private val binding: ChildIncidentDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: IncidentDetailsListItem) {
            binding.textKey.text = item.key
            binding.textValue.text = item.value

            if (adapterPosition % 2 == 0) {
                binding.llBg.setBackgroundColor(Color.parseColor("#ffffff"))
            } else {
                binding.llBg.setBackgroundColor(Color.parseColor("#D8D8D8"))
            }
        }
    }
}