@file:Suppress("unused")

package com.met.atims_reporter.widget.fullwidth.edittext

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.met.atims_reporter.databinding.HeadingEdittextBinding
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
    ConstraintLayout(context, attrs, defStyleAttr),
    EditTextContracts,
    WidgetContainerContracts {

    private lateinit var binding: HeadingEdittextBinding
    private lateinit var widgetContainer: WidgetContainer
    private lateinit var contextForLaterUse: Context

    init {
        context?.let {
            binding = HeadingEdittextBinding.inflate(
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

    override fun getText(): String {
        return binding.editText.getText().toString()
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