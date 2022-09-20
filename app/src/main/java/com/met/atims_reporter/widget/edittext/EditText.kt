package com.met.atims_reporter.widget.edittext

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.jakewharton.rxbinding3.widget.textChanges
import com.met.atims_reporter.R
import com.met.atims_reporter.databinding.EdittextBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.TimeUnit

class EditText @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr) {

    interface TextWatcher {
        fun textChanged(text: String)
    }

    private lateinit var binding: EdittextBinding
    private lateinit var contextForLaterUse: Context
    private var timeMills: Long = 0L
    private lateinit var touchCallback: () -> Unit

    fun getTimeMills() = timeMills

    init {
        context?.let {
            binding = EdittextBinding.inflate(
                it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                this,
                true
            )
            contextForLaterUse = it
        }
    }

    @SuppressLint("CheckResult")
    fun watchForTextChange(textWatcher: TextWatcher): EditText {
        binding.editText
            .textChanges()
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { charSeq ->
                textWatcher.textChanged(charSeq.toString())
            }

        return this
    }

    fun getText(): String {
        return binding.editText.text.toString()
    }

    fun setText(text: String): EditText {
        binding.editText.setText(text)
        binding.editText.setSelection(text.length)
        return this
    }

    fun hint(hint: String): EditText {
        binding.editText.hint = hint
        return this
    }

    fun inputMode(
        editTextInputMode: EditTextInputMode,
        multiLineConfig: MultiLineConfig = MultiLineConfig()
    ): EditText {
        when (editTextInputMode) {
            EditTextInputMode.INPUT_TEXT -> {
                inputText()
            }
            EditTextInputMode.INPUT_TEXT_CAPS -> {
                inputTextCaps()
            }
            EditTextInputMode.INPUT_TEXT_MULTI_LINE -> {
                inputTextMultiLine(multiLineConfig)
            }
            EditTextInputMode.DATE -> {
                inputDate()
            }
            EditTextInputMode.DATE_TIME -> {
                inputDateTime()
            }
            EditTextInputMode.NUMBER -> {
                inputNumber()
            }
            EditTextInputMode.DECIMAL -> {
                inputDecimalNumber()
            }
            EditTextInputMode.PASSWORD -> {
                inputPassword()
            }
            EditTextInputMode.EMAIL -> {
                inputEmail()
            }
        }
        if (binding.editText.text.toString().isNotEmpty()) {
            binding.editText.setSelection(
                binding.editText.text.toString().length
            )
        }
        setUpHeight(editTextInputMode == EditTextInputMode.INPUT_TEXT_MULTI_LINE)
        return this
    }

    fun editable(isEditable: Boolean = true): EditText {
        if (isEditable) {
            binding.cover.visibility = View.GONE
            binding.editText.isCursorVisible = true
        } else {
            binding.cover.visibility = View.VISIBLE
            binding.editText.isCursorVisible = false
        }

        return this
    }

    fun drawableStart(drawable: Drawable): EditText {
        binding.editText.setCompoundDrawablesWithIntrinsicBounds(
            drawable, null, null, null
        )

        return this
    }

    fun gravityCenter(): EditText {
        binding.editText.gravity = Gravity.CENTER
        return this
    }

    fun setColor(textColor: Int): EditText {
        binding.editText.setTextColor(textColor)
        return this
    }

    private fun inputText(): EditText {
        binding.editText.apply {
            inputType = InputType.TYPE_CLASS_TEXT
            transformationMethod = null
            isSingleLine = true
        }
        return this
    }

    private fun inputTextCaps(): EditText {
        binding.editText.apply {
            inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            transformationMethod = null
            isSingleLine = true
        }
        return this
    }

    fun typeFace(fontResEditText: Int? = null, fontResHeading: Int? = null): EditText {
        fontResEditText?.let {
            binding.editText.typeface = ResourcesCompat.getFont(
                context,
                it
            )
        }
        return this
    }

    private fun inputTextMultiLine(multiLineConfig: MultiLineConfig = MultiLineConfig()): EditText {
        binding.editText.apply {
            inputType = InputType.TYPE_CLASS_TEXT
            transformationMethod = null
            isSingleLine = false
            imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
            setLines(multiLineConfig.minLines)
            minLines = multiLineConfig.minLines
            maxLines = multiLineConfig.maxLines
        }
        return this
    }

    @SuppressLint("SetTextI18n")
    private fun inputDate(): EditText {
        inputText()
        binding.editText.apply {
            isClickable = true
            isFocusable = false
        }
        binding.editText.setOnClickListener {
            DatePickerDialog(
                contextForLaterUse,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    binding.editText.setText(
                        "${monthOfYear + 1}-$dayOfMonth-$year"
                    )
                    val cal = Calendar.getInstance()
                    cal.apply {
                        this.set(Calendar.YEAR, year)
                        this.set(Calendar.MONTH, monthOfYear)
                        this.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }
                    timeMills = cal.timeInMillis
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }
        return this
    }

    @SuppressLint("SetTextI18n")
    private fun inputDateTime(): EditText {
        inputText()
        binding.editText.apply {
            isClickable = true
            isFocusable = false
        }
        binding.editText.setOnClickListener {
            val currentDateTime = Calendar.getInstance()
            val startYear = currentDateTime.get(Calendar.YEAR)
            val startMonth = currentDateTime.get(Calendar.MONTH)
            val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
            val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
            val startMinute = currentDateTime.get(Calendar.MINUTE)

            DatePickerDialog(contextForLaterUse, DatePickerDialog.OnDateSetListener { _, year, month, day ->
                TimePickerDialog(contextForLaterUse, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    val pickedDateTime = Calendar.getInstance()
                    pickedDateTime.set(year, month, day, hour, minute)

                    var formatedHour = hour
                    var formatedMinutes = minute
                    var timeSet = ""
                    if (formatedHour > 12) {
                        formatedHour -= 12
                        timeSet = "pm"
                    } else if (formatedHour === 0) {
                        formatedHour += 12
                        timeSet = "am"
                    } else if (formatedHour === 12) {
                        timeSet = "pm"
                    } else {
                        timeSet = "am"
                    }

                    var min: String? = ""
                    if (minute < 10) min = "0$minute" else min = java.lang.String.valueOf(minute)

                    val mTime = StringBuilder().append(formatedHour).append(':')
                        .append(min).append(" ").append(timeSet).toString()
                    binding.editText.setText(
                        "${month + 1}-$day-$year $mTime"
                    )
                }, startHour, startMinute, false).show()
            }, startYear, startMonth, startDay).show()
        }
        return this
    }

    private fun inputNumber(): EditText {
        binding.editText.apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            transformationMethod = null
            isSingleLine = true
        }
        return this
    }

    private fun inputDecimalNumber(): EditText {
        binding.editText.apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            transformationMethod = null
            isSingleLine = true
        }
        return this
    }

    private fun inputPassword(): EditText {
        binding.editText.apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
            transformationMethod = PasswordTransformationMethod.getInstance()
        }
        return this
    }

    private fun inputEmail(): EditText {
        binding.editText.apply {
            inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
            transformationMethod = null
            isSingleLine = true
        }
        return this
    }

    fun registerForTouch(methodToCall: () -> Unit) {
        this.touchCallback = methodToCall
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            if (it.action == 0) {
                performClick()
                if (this::touchCallback.isInitialized)
                    touchCallback()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun registerForFocusChange(methodToCall: (focusChanged: Boolean) -> Unit) {
        binding.editText.setOnFocusChangeListener { _, b ->
            methodToCall(b)
        }
    }

    private fun setUpHeight(isMultiLine: Boolean = false) {
        if (!isMultiLine) {
            binding.editText.height =
                resources.getDimensionPixelSize(R.dimen.editTextHeight)
        }
    }

    fun mandatory(isMandatory: Boolean): EditText {
        //binding.asterisk.visibility = View.GONE

        return this
    }
}