package com.met.atims_reporter.ui.report.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.AdapterShiftIncidentReportBinding
import com.met.atims_reporter.model.FuelListDetails
import com.met.atims_reporter.util.model.PatrollingOnSceneStatus

class ShiftIncidentReportAdapter(
    private val fuelList: ArrayList<PatrollingOnSceneStatus>

) :
    RecyclerView.Adapter<ShiftIncidentReportAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterShiftIncidentReportBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return fuelList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fuelList.get(position))
    }

    inner class ViewHolder(private val binding: AdapterShiftIncidentReportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(patrollingOnSceneStatus: PatrollingOnSceneStatus) {
            binding.textTotalPatrolling.text = patrollingOnSceneStatus.totalpatrolling
            binding.textOnScene.text = patrollingOnSceneStatus.onScele

        }
    }

    interface Callback {
        fun gotoFuelDeatils(fuelListDetails: FuelListDetails)
    }
}