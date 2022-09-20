package com.met.atims_reporter.ui.add_incident.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.CheckBoxListItemBinding
import com.met.atims_reporter.model.RoadSurface

class RoadSurfaceListAdapter(private val roadSurface: ArrayList<RoadSurface>) :
    RecyclerView.Adapter<RoadSurfaceListAdapter.Holder>() {

    fun getSelected(): String {
        var toSend = ""

        roadSurface.forEach {
            if (it.checked) {
                toSend = if (toSend == "") it.road_survey else "$toSend,${it.road_survey}"
            }
        }

        return toSend
    }

    fun setSelected(roadSurfaces: String) {
        if (roadSurfaces == "")
            return

        val splits = roadSurfaces.split(",")

        splits.forEach {
            roadSurface.forEach { roadSurface ->
                if (it == roadSurface.road_survey) {
                    roadSurface.checked = true
                }
            }
        }

        notifyDataSetChanged()
    }

    inner class Holder(private val binding: CheckBoxListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RoadSurface) {
            binding.checkBox.text = item.road_survey
            binding.checkBox.isChecked = item.checked

            binding.checkBox.setOnCheckedChangeListener { compoundButton, b ->
                if (!compoundButton.isPressed)
                    return@setOnCheckedChangeListener
                roadSurface[adapterPosition].checked = b
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
        return roadSurface.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(roadSurface[position])
    }
}