@file:Suppress("unused")

package com.met.atims_reporter.util

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.met.atims_reporter.R


@Suppress("SENSELESS_COMPARISON")
class DialogUtil(private var context: Context) {

    private lateinit var customDialog: Dialog

    interface CallBack {
        fun dialogCancelled() {}

        fun buttonClicked() {}
    }

    interface MultiButtonCallBack {
        fun dialogCancelled() {}

        fun buttonOneClicked() {}

        fun buttonTwoClicked() {}
    }

    fun showMessage(
        message: String,
        cancellable: Boolean = true,
        buttonText: String = "Ok",
        callBack: CallBack? = null,
        image: Drawable? = null
    ) {
        if (this::customDialog.isInitialized && customDialog.isShowing)
            customDialog.dismiss()
        customDialog = Dialog(context)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setContentView(R.layout.dialog_with_single_button)

        customDialog.window
            ?.setLayout(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            )
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val textViewMessage = customDialog.findViewById<TextView>(R.id.text_view_message)
        val buttonAction = customDialog.findViewById<TextView>(R.id.button_action)
        val imageView = customDialog.findViewById<AppCompatImageView>(R.id.appcompatImageView)

        textViewMessage.text = message
        buttonAction.text = buttonText

        image?.let {
            imageView.visibility = View.VISIBLE
            imageView.setImageDrawable(
                it
            )
        }

        customDialog.setCancelable(cancellable)

        buttonAction.setOnClickListener {
            callBack?.buttonClicked()
            customDialog.dismiss()
        }
        customDialog.setOnCancelListener { callBack?.dialogCancelled() }

        customDialog.show()
    }

    fun showMessage(
        message: String,
        cancellable: Boolean = true,
        buttonOneText: String = "Ok",
        buttonTwoText: String = "Cancel",
        multiButtonCallBack: MultiButtonCallBack? = null,
        image: Drawable? = null
    ) {
        if (this::customDialog.isInitialized && customDialog.isShowing)
            customDialog.dismiss()
        customDialog = Dialog(context)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setContentView(R.layout.dialog_with_two_button)

        customDialog.window
            ?.setLayout(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            )
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val textViewMessage = customDialog.findViewById<TextView>(R.id.text_view_message)
        val buttonActionOne = customDialog.findViewById<TextView>(R.id.button_action_one)
        val buttonActionTwo = customDialog.findViewById<TextView>(R.id.button_action_two)
        val imageView = customDialog.findViewById<AppCompatImageView>(R.id.appcompatImageView)

        textViewMessage.text = message
        buttonActionOne.text = buttonOneText
        buttonActionTwo.text = buttonTwoText

        image?.let {
            imageView.visibility = View.VISIBLE
            imageView.setImageDrawable(
                it
            )
        }

        customDialog.setCancelable(cancellable)

        buttonActionOne.setOnClickListener {
            multiButtonCallBack?.buttonOneClicked()
            customDialog.dismiss()
        }
        buttonActionTwo.setOnClickListener {
            multiButtonCallBack?.buttonTwoClicked()
            customDialog.dismiss()
        }
        customDialog.setOnCancelListener { multiButtonCallBack?.dialogCancelled() }

        customDialog.show()
    }
}
