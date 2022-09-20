package com.met.atims_reporter.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.HomeGridItemBinding
import com.met.atims_reporter.enums.HomeGridItems
import com.met.atims_reporter.model.HomeGridItem

class GridItemsAdapter(private val items: ArrayList<HomeGridItem>, private val callback: Callback) :
    RecyclerView.Adapter<GridItemsAdapter.Holder>() {

    interface Callback {
        fun clicked(homeGridItem: HomeGridItem)
    }

    var shiftStarted = false

    inner class Holder(private val binding: HomeGridItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: HomeGridItem) {
            binding.image.setImageDrawable(
                item.icon
            )
            if (item.tag == HomeGridItems.START_SHIFT && shiftStarted)
                binding.textViewTitle.text = "End Shift"
            else
                binding.textViewTitle.text = item.title
            binding.container.setOnClickListener {
                callback.clicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            HomeGridItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                null,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(
            items[position]
        )
    }
}