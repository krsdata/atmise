package com.met.atims_reporter.ui.pre_ops.step_four.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.AdapterToolsBinding
import com.met.atims_reporter.model.ToolListResponce

class PreOpsToolsAadpter(
    private val insepectionList: ArrayList<ToolListResponce>
) :
    RecyclerView.Adapter<PreOpsToolsAadpter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterToolsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return insepectionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(insepectionList.get(position))
    }

    inner class ViewHolder(private val binding: AdapterToolsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(questionList: ToolListResponce) {

            binding.textToolInfo.text = questionList.tool_name+"("+questionList.tool_quantity+")"

        }
    }


}