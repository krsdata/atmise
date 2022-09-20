package com.met.atims_reporter.widget.fullwidth.autocomplete

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.met.atims_reporter.databinding.HeadingAutoCompleteBinding
import com.met.atims_reporter.widget.autocomplete.AutoComplete
import com.met.atims_reporter.widget.autocomplete.AutoCompleteContracts
import com.met.atims_reporter.widget.autocomplete.AutoCompleteData
import com.met.atims_reporter.widget.container.WidgetContainer
import com.met.atims_reporter.widget.container.WidgetContainerContracts

class AutoComplete @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ConstraintLayout(context, attrs, defStyleAttr),
    WidgetContainerContracts,
    AutoCompleteContracts {

    private lateinit var binding: HeadingAutoCompleteBinding
    private lateinit var widgetContainer: WidgetContainer
    private lateinit var contextForLaterUse: Context

    init {
        context?.let {
            binding = HeadingAutoCompleteBinding.inflate(
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
        list: ArrayList<AutoCompleteData<T>>,
        listener: AutoComplete.OnItemSelectedListener
    ): AutoComplete {
        return binding.autoComplete.addItems(
            list, listener
        )
    }
}