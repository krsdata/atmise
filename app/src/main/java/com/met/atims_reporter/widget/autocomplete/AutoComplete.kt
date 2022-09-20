package com.met.atims_reporter.widget.autocomplete

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.AdapterView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.met.atims_reporter.R
import com.met.atims_reporter.databinding.AutoCompleteBinding

class AutoComplete @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: AutoCompleteBinding
    private lateinit var contextForLaterUse: Context

    init {
        context?.let {
            binding = AutoCompleteBinding.inflate(
                it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                this,
                true
            )
            contextForLaterUse = it
        }
    }

    fun <T> addItems(
        list: ArrayList<AutoCompleteData<T>>,
        listener: OnItemSelectedListener
    ): AutoComplete {
        val adapter =
            AutoCompleteAdapter(
                contextForLaterUse,
                list
            )
        binding.autoComplete.setAdapter(adapter)
        binding.autoComplete.threshold = 1
        binding.autoComplete.setDropDownBackgroundDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.dropdown_suggestion_bg,
                null
            )
        )
        binding.autoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                @Suppress("UNCHECKED_CAST")
                listener.selected(
                    parent.getItemAtPosition(position) as AutoCompleteData<T>
                )
            }
        return this
    }

    interface OnItemSelectedListener {
        fun <T> selected(item: AutoCompleteData<T>)
    }
}