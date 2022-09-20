package com.met.atims_reporter.widget.autocomplete

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.met.atims_reporter.R
import com.met.atims_reporter.databinding.AutoCompleteItemBinding
import java.util.*
import kotlin.collections.ArrayList

class AutoCompleteAdapter<T>(
    context: Context,
    private val list: ArrayList<AutoCompleteData<T>>
) :
    ArrayAdapter<AutoCompleteData<T>>(
        context,
        R.layout.auto_complete_item,
        list
    ) {

    var suggestions: ArrayList<AutoCompleteData<T>> = ArrayList()
    @Suppress("UNCHECKED_CAST")
    var tempList: ArrayList<AutoCompleteData<T>> = list.clone() as ArrayList<AutoCompleteData<T>>

    @Suppress("unused")
    fun getList() = list

    @SuppressLint("SetTextI18n", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        convertView?.let {
            val binding = it.tag as AutoCompleteItemBinding
            binding.textView.text = list[position].toShow
            return binding.root
        } ?: run {
            val binding = AutoCompleteItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            binding.textView.text = list[position].toShow
            binding.root.tag = binding
            return binding.root
        }
    }

    @SuppressLint("SetTextI18n")
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        convertView?.let {
            val binding = it.tag as AutoCompleteItemBinding
            binding.textView.text = list[position].toShow
            return binding.root
        } ?: run {
            val binding = AutoCompleteItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            binding.textView.text = list[position].toShow
            binding.root.tag = binding
            return binding.root
        }
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): AutoCompleteData<T>? {
        return list[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                p0?.let {
                    suggestions.clear()
                    for (temp in tempList) {
                        if (temp.toShow.toLowerCase(Locale.getDefault()).contains(
                                it,
                                true
                            )
                        )
                            suggestions.add(temp)
                    }

                    val result = FilterResults()
                    result.values = suggestions
                    result.count = suggestions.size
                    return result
                } ?: kotlin.run {
                    return FilterResults()
                }
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                p1?.let {
                    if (it.count == 0)
                        return
                    clear()
                    for (res in it.values as ArrayList<*>) {
                        @Suppress("UNCHECKED_CAST")
                        add(res as AutoCompleteData<T>)
                    }
                    notifyDataSetChanged()
                }
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                resultValue?.let {
                    @Suppress("UNCHECKED_CAST")
                    return (it as AutoCompleteData<T>).toShow
                } ?: run {
                    return ""
                }
            }
        }
    }
}