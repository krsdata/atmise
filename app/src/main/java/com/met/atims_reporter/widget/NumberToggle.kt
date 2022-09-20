package com.met.atims_reporter.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.met.atims_reporter.R
import com.met.atims_reporter.databinding.NumberToggleBinding

@Suppress("MemberVisibilityCanBePrivate")
class NumberToggle @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    interface Callback {
        fun countChanged(count: Int)
    }

    private lateinit var binding: NumberToggleBinding
    private var count = 0
    private lateinit var callback: Callback

    init {
        context?.let {
            binding = DataBindingUtil.inflate(
                it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                R.layout.number_toggle,
                this,
                true
            )

            addListeners()
        }
    }

    fun getCount() = count

    fun setCount(count: Int) {
        this.count = count
        updateUI(false)
    }

    fun addListener(callback: Callback) {
        this.callback = callback
    }

    private fun addListeners() {
        binding.buttonAddOne.setOnClickListener {
            addOne()
        }
        binding.buttonRemoveOne.setOnClickListener {
            removeOne()
        }
    }

    private fun addOne() {
        count++
        updateUI()
    }

    private fun removeOne() {
        if (count == 0)
            return
        count--
        updateUI()
    }

    private fun updateUI(propagateToListener: Boolean = true) {
        binding.textViewNumber.text = count.toString()

        if (propagateToListener && this::callback.isInitialized)
            callback.countChanged(count)
    }
}