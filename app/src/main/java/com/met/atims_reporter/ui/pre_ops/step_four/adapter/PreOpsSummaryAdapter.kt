package com.met.atims_reporter.ui.pre_ops.step_four.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.PreOpsSummaryHeadingListItemBinding
import com.met.atims_reporter.databinding.PreOpsSummaryQuestionListItemBinding
import com.met.atims_reporter.databinding.PreOpsSummaryToolsQuestionListItemBinding
import com.met.atims_reporter.model.QuestionLisResponse
import com.met.atims_reporter.model.ToolListResponce

class PreOpsSummaryAdapter(
    private val preOpsQuestions: ArrayList<QuestionLisResponse>
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

        fun bind(questionLisResponse: QuestionLisResponse) {
            questionLisResponse.apply {
                binding.textViewQuestion.text = preops_vehicle_question
                binding.textViewAnswer.text = answer
                if(comments.trim().isNotEmpty()) {
                    binding.textViewComment.text = comments
                }else{
                    binding.textViewComment.visibility = View.GONE
                }
                if(imagePath?.trim()?.isNotEmpty()==true) {
                    binding.ivSummaryImage.setImageURI(Uri.parse(imagePath))
                }else{
                    binding.ivSummaryImage.visibility = View.GONE
                }


            }
        }
    }

    inner class ViewHolderToolsQuestion(
        private var binding: PreOpsSummaryToolsQuestionListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(toolListResponce: ToolListResponce) {
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
                holder.bind("PreOps Questions")
        } else if (holder is ViewHolderQuestion)
            holder.bind(preOpsQuestions[position - 1])
       /* else if (holder is ViewHolderToolsQuestion)
            holder.bind(toolsQuestions[position - (preOpsQuestions.size + 2)])*/
    }
}