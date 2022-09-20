package com.met.atims_reporter.ui.add_inspection.step_three.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.ChildPreOpsToolsBinding
import com.met.atims_reporter.model.ToolListResponceInsp
import com.met.atims_reporter.widget.NumberToggle
import com.met.atims_reporter.widget.spinner.SpinnerData

class InspectionThreeAdapter(
    private val toolList: ArrayList<ToolListResponceInsp>
) :
    RecyclerView.Adapter<InspectionThreeAdapter.ViewHolder>() {

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
        fun bind(toolListResponce: ToolListResponceInsp) {
            val selections = arrayListOf(
                SpinnerData("present", "present"),
                SpinnerData("missing", "missing"),
                SpinnerData("broken", "broken")
            )

            toolListResponce.tool_status ?: run {
                toolListResponce.tool_status = selections[0].data
            }
            toolListResponce.tool_quantity ?: run {
                toolListResponce.tool_quantity = "0"
            }

            binding.spinner.visibility = View.GONE
            binding.textViewHeading.text = toolListResponce.tool_name

            when (
                toolListResponce.tool_status
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
                    toolListResponce.tool_status = selections[0].data
                }
            }

            binding.missing.setOnCheckedChangeListener { _, b ->
                if (b) {
                    toolListResponce.tool_status = selections[1].data
                }
            }

            binding.broken.setOnCheckedChangeListener { _, b ->
                if (b) {
                    toolListResponce.tool_status = selections[2].data
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

            /*binding.spinner.apply {
                heading(
                    toolListResponce.tool_name
                )

                spinnerWidthPercent(60)

                addItems(
                    selections
                    ,
                    object : Spinner.OnItemSelectedListener {
                        override fun <T> selected(item: SpinnerData<T>) {
                            toolListResponce.tool_status = item.data as String
                        }
                    }
                )

                toolListResponce.tool_status?.let { ans ->
                    select<SpinnerData<String>>(ans)
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
            }*/
        }
    }

}