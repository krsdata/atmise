package com.met.atims_reporter.util.activity

import android.graphics.drawable.Drawable
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.model.Result

@Suppress("unused")
interface ShowMessageCallback {
    fun showMessageInDialog(message: String)

    fun showMessageWithOneButton(
        message: String,
        callback: DialogUtil.CallBack,
        cancellable: Boolean = false,
        buttonText: String = "Ok",
        image: Drawable? = null
    )

    fun showMessageWithTwoButton(
        message: String,
        callback: DialogUtil.MultiButtonCallBack,
        cancellable: Boolean = false,
        buttonOneText: String = "Ok",
        buttonTwoText: String = "Cancel",
        image: Drawable? = null
    )

    fun handleGenericResult(result: Result)
}