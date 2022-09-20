package com.met.atims_reporter.widget.spinner

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.met.atims_reporter.R
import com.met.atims_reporter.databinding.SpinnerBinding

class Spinner @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: SpinnerBinding
    private lateinit var contextForLaterUse: Context
    private lateinit var adapter: Any

    init {
        context?.let {
            binding = SpinnerBinding.inflate(
                it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                this,
                true
            )
            contextForLaterUse = it
        }
    }

    fun <T> addItems(
        list: ArrayList<SpinnerData<T>>,
        listener: OnItemSelectedListener
    ): Spinner {
        adapter = SpinnerAdapter(
            contextForLaterUse,
            list
        )
        binding.spinner.adapter = adapter as SpinnerAdapter<*>
        binding.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    parent?.let {
                        try {
                            @Suppress("UNCHECKED_CAST")
                            listener.selected(
                                it.getItemAtPosition(position) as SpinnerData<T>
                            )
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }
        return this
    }

    interface OnItemSelectedListener {
        fun <T> selected(item: SpinnerData<T>)
    }

    fun <T> select(toShowString: String? = null, data: T? = null): Spinner {
        var indexToChange = -1
        data?.let {
            (adapter as SpinnerAdapter<*>)
                .getList()
                .forEachIndexed { index, item ->
                    if (item.data == it) {
                        indexToChange = index
                    }
                }
        } ?: run {
            toShowString?.let {
                (adapter as SpinnerAdapter<*>)
                    .getList()
                    .forEachIndexed { index, spinnerData ->
                        if (spinnerData.toShow == it && indexToChange == -1) {
                            indexToChange = index
                        }
                    }
            }
        }

        if (indexToChange != -1) {
            binding.spinner.setSelection(indexToChange)
        }

        return this
    }

    fun editable(isEditable: Boolean = true): Spinner {
        if (isEditable)
            binding.cover.visibility = View.GONE
        else
            binding.cover.visibility = View.VISIBLE

        return this
    }

    fun mandatory(isMandatory: Boolean): Spinner {
       // binding.asterisk.visibility = View.GONE

        return this
    }
}