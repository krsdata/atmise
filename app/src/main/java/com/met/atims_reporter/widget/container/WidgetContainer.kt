package com.met.atims_reporter.widget.container

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.met.atims_reporter.databinding.WidgetContainerBinding

class WidgetContainer @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    root: ViewGroup? = null,
    child: View? = null
) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        fun getOne(
            context: Context,
            root: ViewGroup,
            child: View
        ) = WidgetContainer(
            context = context, root = root, child = child
        )
    }

    private lateinit var binding: WidgetContainerBinding
    private lateinit var contextForLaterUse: Context
    private var errorText = "Invalid input"

    init {
        context?.let {
            binding = WidgetContainerBinding.inflate(
                it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                root ?: this,
                true
            )
            child?.let { childView ->
                binding.container.addView(childView)
            }
            contextForLaterUse = it
            binding.textViewErrorText.visibility = View.GONE
        }
    }

    fun setErrorText(errorText: String): WidgetContainer {
        this.errorText = errorText
        return this
    }

    fun showError(errorText: String? = null) {
        binding.textViewErrorText.apply {
            visibility = View.VISIBLE
            text = errorText ?: this@WidgetContainer.errorText
        }
    }

    fun hideError() {
        binding.textViewErrorText.apply {
            visibility = View.GONE
        }
    }
}