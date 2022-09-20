package com.met.atims_reporter.widget.edittext

import android.graphics.drawable.Drawable

interface EditTextContracts {
    fun getText(): String
    fun setText(text: String): EditText
    fun hint(hint: String): EditText
    fun inputMode(
        editTextInputMode: EditTextInputMode,
        multiLineConfig: MultiLineConfig = MultiLineConfig()
    ): EditText

    fun editable(isEditable: Boolean = true): EditText
    fun watchTextChange(textWatcher: EditText.TextWatcher): EditText
    fun getTimeMills(): Long
    fun drawableStart(drawable: Drawable): EditText
    fun mandatory(isMandatory: Boolean): EditText
    fun registerForFocusChange(methodToCall: (focusChanged: Boolean) -> Unit)
}