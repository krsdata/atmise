package com.met.atims_reporter.widget.fullwidth.spinner

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.met.atims_reporter.databinding.HeadingSpinnerBinding
import com.met.atims_reporter.widget.container.WidgetContainer
import com.met.atims_reporter.widget.container.WidgetContainerContracts
import com.met.atims_reporter.widget.spinner.Spinner
import com.met.atims_reporter.widget.spinner.SpinnerContracts
import com.met.atims_reporter.widget.spinner.SpinnerData

class Spinner @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr),
    WidgetContainerContracts,
    SpinnerContracts {

    private lateinit var binding: HeadingSpinnerBinding
    private lateinit var widgetContainer: WidgetContainer
    private lateinit var contextForLaterUse: Context

    init {
        context?.let {
            binding = HeadingSpinnerBinding.inflate(
                it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                this,
                false
            )
            widgetContainer = WidgetContainer.getOne(it, this, binding.root)
            contextForLaterUse = it
        }
    }

    fun heading(heading: String) {
        binding.textViewHeading.text = heading
    }

    override fun setErrorText(errorText: String): WidgetContainer {
        return widgetContainer.setErrorText(errorText)
    }

    override fun showError(errorText: String?) {
        widgetContainer.showError(errorText)
    }

    override fun hideError() {
        widgetContainer.hideError()
    }

    override fun <T> addItems(
        list: ArrayList<SpinnerData<T>>,
        listener: Spinner.OnItemSelectedListener
    ): Spinner {
        return binding.spinner.addItems(
            list, listener
        )
    }

    override fun <T> select(toShowString: String?, data: T?): Spinner {
        return binding.spinner.select(toShowString, data)
    }

    override fun editable(isEditable: Boolean): Spinner {
        return binding.spinner.editable(isEditable)
    }

    override fun mandatory(isMandatory: Boolean): Spinner {
        return binding.spinner.mandatory(isMandatory)
    }
}