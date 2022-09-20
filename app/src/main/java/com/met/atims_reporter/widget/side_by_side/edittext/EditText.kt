package com.met.atims_reporter.widget.side_by_side.edittext

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.met.atims_reporter.databinding.SideBySideHeadingEdittextBinding
import com.met.atims_reporter.widget.container.WidgetContainer
import com.met.atims_reporter.widget.container.WidgetContainerContracts
import com.met.atims_reporter.widget.edittext.EditText
import com.met.atims_reporter.widget.edittext.EditTextContracts
import com.met.atims_reporter.widget.edittext.EditTextInputMode
import com.met.atims_reporter.widget.edittext.MultiLineConfig

class EditText @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr),
    EditTextContracts,
    WidgetContainerContracts {

    private lateinit var binding: SideBySideHeadingEdittextBinding
    private lateinit var widgetContainer: WidgetContainer
    private lateinit var contextForLaterUse: Context

    init {
        context?.let {
            binding = SideBySideHeadingEdittextBinding.inflate(
                it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                this,
                false
            )
            widgetContainer = WidgetContainer.getOne(it, this, binding.root)
            contextForLaterUse = it
        }
    }

    fun editTextWidthPercent(widthPercent: Int = 50) {
        val params1 = binding.textViewHeading.layoutParams as LayoutParams
        params1.weight = 1 - (widthPercent.toFloat() / 100F)
        binding.textViewHeading.layoutParams = params1
        val params: LayoutParams = binding.editText.layoutParams as LayoutParams
        //params.matchConstraintPercentWidth = (widthPercent.toFloat() / 100F)
        params.weight =  (widthPercent.toFloat() / 100F)
        binding.editText.layoutParams = params
        binding.asterisk.visibility = View.GONE
    }

    fun heading(heading: String) {
        binding.textViewHeading.text = heading
    }

    override fun getText(): String {
        return binding.editText.getText()
    }

    override fun setText(text: String): EditText {
        return binding.editText.setText(text)
    }

    override fun hint(hint: String): EditText {
        return binding.editText.hint(hint)
    }

    override fun inputMode(
        editTextInputMode: EditTextInputMode,
        multiLineConfig: MultiLineConfig
    ): EditText {
        return binding.editText.inputMode(editTextInputMode, multiLineConfig)
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

    override fun editable(isEditable: Boolean): EditText {
        return binding.editText.editable(isEditable)
    }


    override fun watchTextChange(textWatcher: EditText.TextWatcher): EditText {
        return binding.editText.watchForTextChange(textWatcher)
    }

    override fun getTimeMills(): Long {
        return binding.editText.getTimeMills()
    }

    override fun drawableStart(drawable: Drawable): EditText {
        return binding.editText.drawableStart(drawable)
    }

    override fun mandatory(isMandatory: Boolean): EditText {
        return binding.editText.mandatory(isMandatory)
    }

    override fun registerForFocusChange(methodToCall: (focusChanged: Boolean) -> Unit) {
        return binding.editText.registerForFocusChange(methodToCall)
    }
}