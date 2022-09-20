package com.met.atims_reporter.ui.inspection_details.one.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.AdapterInspectionVehicelBinding
import com.met.atims_reporter.model.QuestionList

class InspectionVehicelAdapter(
    private val insepectionList: ArrayList<QuestionList>
) :
    RecyclerView.Adapter<InspectionVehicelAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterInspectionVehicelBinding.inflate(
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

    inner class ViewHolder(private val binding: AdapterInspectionVehicelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(questionList: QuestionList) {

            binding.textQuestion.text = questionList.inspection_vehicle_question
            binding.textAns.text = questionList.inspection_vehicle_answer
            binding.textComment.text = questionList.inspection_vehicle_answer_comments
            if (questionList.inspection_vehicle_answer_image == "") {
                binding.textImage.visibility = View.GONE
            } else {
                binding.textImage.visibility = View.VISIBLE
                binding.textImage.setImage(
                    questionList.reports_q_image
                )
            }

        }
    }


}