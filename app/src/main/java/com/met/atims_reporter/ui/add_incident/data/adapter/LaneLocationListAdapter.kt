package com.met.atims_reporter.ui.add_incident.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.CheckBoxListItemBinding
import com.met.atims_reporter.model.LaneLocation

class LaneLocationListAdapter : RecyclerView.Adapter<LaneLocationListAdapter.Holder>() {
    private val LANE_LOCATION = arrayListOf(
        LaneLocation(checked = false, value = "Left Shoulder"),
        LaneLocation(checked = false, value = "Left Lane"),
        LaneLocation(checked = false, value = "Middle Lane"),
        LaneLocation(checked = false, value = "Right Lane"),
        LaneLocation(checked = false, value = "Right Shoulder"),
        LaneLocation(checked = false, value = "Gore")
    )

    fun getSelected(): String {
        var toSend = ""

        LANE_LOCATION.forEach {
            if (it.checked) {
                toSend = if (toSend == "") it.value else "$toSend,${it.value}"
            }
        }

        return toSend
    }

    fun setSelected(lanes: String) {
        if (lanes == "")
            return

        val splits = lanes.split(",")

        splits.forEach {
            LANE_LOCATION.forEach { lanesStatics ->
                if (it == lanesStatics.value) {
                    lanesStatics.checked = true
                }
            }
        }

        notifyDataSetChanged()
    }

    inner class Holder(private val binding: CheckBoxListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(laneLocation: LaneLocation) {
            binding.checkBox.text = laneLocation.value
            binding.checkBox.isChecked = laneLocation.checked

            binding.checkBox.setOnCheckedChangeListener { compoundButton, b ->
                if (!compoundButton.isPressed)
                    return@setOnCheckedChangeListener
                LANE_LOCATION[adapterPosition].checked = b
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            CheckBoxListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return LANE_LOCATION.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(LANE_LOCATION[position])
    }
}