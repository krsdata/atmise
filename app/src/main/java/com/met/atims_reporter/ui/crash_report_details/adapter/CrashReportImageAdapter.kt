package com.met.atims_reporter.ui.crash_report_details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.ChildCrashReportImageBinding
import com.squareup.picasso.Picasso

class CrashReportImageAdapter(private var list: ArrayList<String>, private var callback: Callback) :
    RecyclerView.Adapter<CrashReportImageAdapter.Holder>() {

    interface Callback {
        fun putToMainView(imageUrl: String)
    }

    inner class Holder(private val binding: ChildCrashReportImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(imageUrl: String) {
            Picasso
                .get()
                .load(
                    imageUrl
                )
                .into(
                    binding.imageCameraExteriorSmall1
                )

            binding.container.setOnClickListener {
                callback.putToMainView(imageUrl)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ChildCrashReportImageBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(list[position])
    }
}