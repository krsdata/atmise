package com.met.atims_reporter.ui.add_inspection.step_four.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.PreOpsSummaryHeadingListItemBinding
import com.met.atims_reporter.databinding.PreOpsSummaryQuestionListItemBinding
import com.met.atims_reporter.databinding.PreOpsSummaryToolsQuestionListItemBinding
import com.met.atims_reporter.model.InsectionQuestionLisResponse
import com.met.atims_reporter.model.ToolListResponceInsp
import java.io.File

class InspectionSummaryAdapter(
    private val preOpsQuestions: ArrayList<InsectionQuestionLisResponse>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val HEADING = 1
        const val QUESTION = 2
        const val TOOLS = 3
    }

    inner class ViewHolderHeading(
        private var binding: PreOpsSummaryHeadingListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(heading: String) {
            binding.textView.text = heading
        }
    }

    inner class ViewHolderQuestion(
        private var binding: PreOpsSummaryQuestionListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(questionLisResponse: InsectionQuestionLisResponse) {
            questionLisResponse.apply {
                binding.textViewQuestion.text = inspection_vehicle_question
                binding.textViewAnswer.text = answer
                binding.textViewComment.text = comments
                if (imagePath != "") {
                    binding.ivSummaryImage.setImageURI(Uri.parse(imagePath))
                }
            }
        }
    }

    inner class ViewHolderToolsQuestion(
        private var binding: PreOpsSummaryToolsQuestionListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(toolListResponce: ToolListResponceInsp) {
            toolListResponce.apply {
                binding.textViewQuestion.text = tool_name
                binding.textViewAnswer.text = status
                binding.textViewNumber.text = tool_quantity
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                HEADING
            }
            in 1..preOpsQuestions.size -> {
                QUESTION
            }
            preOpsQuestions.size + 1 -> {
                HEADING
            }
            else -> {
                TOOLS
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADING -> {
                ViewHolderHeading(
                    PreOpsSummaryHeadingListItemBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ),
                        parent,
                        false
                    )
                )
            }
            QUESTION -> {
                ViewHolderQuestion(
                    PreOpsSummaryQuestionListItemBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ),
                        parent,
                        false
                    )
                )
            }
            else -> {
                ViewHolderToolsQuestion(
                    PreOpsSummaryToolsQuestionListItemBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return preOpsQuestions.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderHeading) {
            if (position == 0)
                holder.bind("Inspections Questions")
            else
                holder.bind("Tools")
        } else if (holder is ViewHolderQuestion)
            holder.bind(preOpsQuestions[position - 1])
    }
}