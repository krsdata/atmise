package com.met.atims_reporter.ui.inspection_details.two.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.AdapterToolsBinding
import com.met.atims_reporter.model.ToolsList

class ToolsAdapter(
    private val insepectionList: ArrayList<ToolsList>
) :
    RecyclerView.Adapter<ToolsAdapter.ViewHolder>() {
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
        fun bind(questionList: ToolsList) {
            binding.textToolInfo.text = questionList.tool_name + "(" + questionList.quantity + ")"
        }
    }
}