package com.met.atims_reporter.ui.faq.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.R
import com.met.atims_reporter.databinding.AdapterFaqBinding
import com.met.atims_reporter.model.FaqResponce

class FAQAdapter(val mContext: Context, val faqList: ArrayList<FaqResponce>) :
    RecyclerView.Adapter<FAQAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterFaqBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return faqList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(faqList.get(position))
    }

    inner class ViewHolder(val binding: AdapterFaqBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var expanded = false

        fun onBind(faqResponce: FaqResponce) {
            binding.faqShortText.text = faqResponce.faq_questions
            binding.faqLongText.text = faqResponce.faq_answer
            binding.container.setOnClickListener {
                if (expanded) {
                    binding.faqLongText.visibility = View.GONE
                    binding.cvMainView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.colorLightYellow2
                        )
                    )
                    expanded = false
                    binding.ivDownArrow.animate()
                        .rotation(0F)
                        .setDuration(300L)
                        .start()
                } else {
                    binding.faqLongText.visibility = View.VISIBLE
                    binding.cvMainView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.colorLightYellow2
                        )
                    )
                    expanded = true
                    binding.ivDownArrow.animate()
                        .rotation(180F)
                        .setDuration(300L)
                        .start()
                }
            }
        }
    }
}