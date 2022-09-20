package com.met.atims_reporter.ui.add_crash_report.step_two.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.AddCrashReportImageListItemAddMoreBinding
import com.met.atims_reporter.databinding.AddCrashReportImageListItemBinding
import java.io.File

class TakePictureAdapter(private val callback: Callback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface Callback {
        fun takePicture()
    }

    companion object {
        const val ITEM = 1
        const val ADD_MORE = 2
    }

    var max = 4
    private val images: ArrayList<String> = ArrayList()

    fun getImages() = images

    fun pictureCaptured(imagePath: String) {
        this.images.add(imagePath)
        notifyDataSetChanged()
    }

    inner class HolderItem(private val binding: AddCrashReportImageListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imagePath: String) {
            binding.imageView.setImage(
                File(imagePath)
            )
        }
    }

    inner class HolderItemAddMore(private val binding: AddCrashReportImageListItemAddMoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.container.setOnClickListener {
                callback.takePicture()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ADD_MORE -> {
                HolderItemAddMore(
                    AddCrashReportImageListItemAddMoreBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                HolderItem(
                    AddCrashReportImageListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return if (images.size == max) max else if (images.size == 0) 1 else images.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        if (images.size == 0)
            return ADD_MORE
        if (position < images.size)
            return ITEM
        return ADD_MORE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HolderItem)
            holder.bind(
                images[position]
            )
        if (holder is HolderItemAddMore)
            holder.bind()
    }
}