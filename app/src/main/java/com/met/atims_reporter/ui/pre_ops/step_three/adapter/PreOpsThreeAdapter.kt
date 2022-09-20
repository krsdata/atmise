package com.met.atims_reporter.ui.pre_ops.step_three.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.ChildPreOpsToolsBinding
import com.met.atims_reporter.model.ToolListResponce
import com.met.atims_reporter.widget.NumberToggle
import com.met.atims_reporter.widget.spinner.SpinnerData

class PreOpsThreeAdapter(
    private val toolList: ArrayList<ToolListResponce>
) :
    RecyclerView.Adapter<PreOpsThreeAdapter.ViewHolder>() {

    init {
        toolList.forEach {
            it.answer = "present"
        }
    }

    fun getList() = toolList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChildPreOpsToolsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return toolList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(toolList.get(position))
    }

    inner class ViewHolder(private val binding: ChildPreOpsToolsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(toolListResponce: ToolListResponce) {
            val selections = arrayListOf(
                SpinnerData("present", "present"),
                SpinnerData("missing", "missing"),
                SpinnerData("broken", "broken")
            )

            toolListResponce.answer ?: run {
                toolListResponce.answer = selections[0].data
            }
            toolListResponce.tool_quantity ?: run {
                toolListResponce.tool_quantity = "0"
            }

            binding.spinner.visibility = View.GONE
            binding.textViewHeading.text = toolListResponce.tool_name

            when (
                toolListResponce.answer
                ) {
                selections[0].data -> {
                    binding.present.isChecked = true
                }
                selections[1].data -> {
                    binding.missing.isChecked = true
                }
                selections[2].data -> {
                    binding.broken.isChecked = true
                }
            }

            binding.present.setOnCheckedChangeListener { _, b ->
                if (b) {
                    toolListResponce.answer = selections[0].data
                }
            }

            binding.missing.setOnCheckedChangeListener { _, b ->
                if (b) {
                    toolListResponce.answer = selections[1].data
                }
            }

            binding.broken.setOnCheckedChangeListener { _, b ->
                if (b) {
                    toolListResponce.answer = selections[2].data
                }
            }

            binding.numberToggleHowMany.addListener(
                object : NumberToggle.Callback {
                    override fun countChanged(count: Int) {
                        toolListResponce.tool_quantity = count.toString()
                    }
                }
            )

            toolListResponce.tool_quantity?.let {
                binding.numberToggleHowMany.setCount(it.toInt())
            }
        }
    }

}