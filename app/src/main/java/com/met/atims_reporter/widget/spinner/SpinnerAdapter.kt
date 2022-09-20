package com.met.atims_reporter.widget.spinner

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.met.atims_reporter.R
import com.met.atims_reporter.databinding.SpinnerItemBinding

class SpinnerAdapter<T>(
    context: Context,
    private val list: ArrayList<SpinnerData<T>>
) :
    ArrayAdapter<SpinnerData<T>>(
        context,
        R.layout.spinner_item,
        list
    ) {

    @Suppress("unused")
    fun getList() = list

    @SuppressLint("SetTextI18n", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        convertView?.let {
            val binding = it.tag as SpinnerItemBinding
            binding.textView.text = list[position].toShow
            binding.dropdownArrow.visibility = View.VISIBLE
            return binding.root
        } ?: run {
            val binding = SpinnerItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            binding.textView.text = list[position].toShow
            binding.dropdownArrow.visibility = View.VISIBLE
            binding.root.tag = binding
            return binding.root
        }
    }

    @SuppressLint("SetTextI18n")
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        convertView?.let {
            val binding = it.tag as SpinnerItemBinding
            binding.textView.text = list[position].toShow
            binding.dropdownArrow.visibility = View.GONE
            return binding.root
        } ?: run {
            val binding = SpinnerItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            binding.textView.text = list[position].toShow
            binding.dropdownArrow.visibility = View.GONE
            binding.root.tag = binding
            return binding.root
        }
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): SpinnerData<T>? {
        return list[position]
    }
}