@file:Suppress("LeakingThis")

package com.met.atims_reporter.util.activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.NetworkUtil
import com.met.atims_reporter.util.ProgressUtil
import com.met.atims_reporter.util.model.Result

@Suppress("unused")
@SuppressLint("Registered")
open class SuperActivity :
    AppCompatActivity(),
    ShowMessageCallback,
    ShowProgressCallback {

    inline fun <reified T> fromJson(json: String): T {
        return Gson().fromJson(json, object : TypeToken<T>() {}.type)
    }

    fun toJson(argument: Any) = Gson().toJson(argument)!!

    private val dialogUtil: DialogUtil =
        DialogUtil(this)

    private val progressUtil: ProgressUtil =
        ProgressUtil(this)

    fun isConnectedToNetwork() = NetworkUtil.isConnected(this)

    override fun showProgress(numberOfLoader: Int) {
        progressUtil.show(numberOfLoader)
    }

    override fun hideProgress() {
        progressUtil.hide()
    }

    override fun isAnyThingInProgress(): Boolean {
        return progressUtil.isAnyThingInProgress()
    }

    fun hideProgressForced() {
        progressUtil.hideForced()
    }

    override fun handleGenericResult(result: Result) {
        hideProgress()
        showMessageInDialog(result.getMessageToShow())
    }

    override fun showMessageInDialog(message: String) {
        dialogUtil.showMessage(
            message = message
        )
    }

    override fun showMessageWithOneButton(
        message: String,
        callback: DialogUtil.CallBack,
        cancellable: Boolean,
        buttonText: String,
        image: Drawable?
    ) {
        dialogUtil.showMessage(
            message = message,
            cancellable = cancellable,
            callBack = callback,
            buttonText = buttonText,
            image = image
        )
    }

    override fun showMessageWithTwoButton(
        message: String,
        callback: DialogUtil.MultiButtonCallBack,
        cancellable: Boolean,
        buttonOneText: String,
        buttonTwoText: String,
        image: Drawable?
    ) {
        dialogUtil.showMessage(
            message = message,
            cancellable = cancellable,
            multiButtonCallBack = callback,
            buttonOneText = buttonOneText,
            buttonTwoText = buttonTwoText,
            image = image
        )
    }

    fun View.setMarginTop(marginTop: Int) {
        val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
        menuLayoutParams.setMargins(0, marginTop, 0, 0)
        this.layoutParams = menuLayoutParams
    }

    private var askedForExit = false

    protected fun askForAppExit() {
        if (askedForExit)
            finish()
        else {
            askedForExit = true
            Handler().postDelayed(
                {
                    askedForExit = false
                },
                2000
            )
            Toast.makeText(
                this,
                "Press back to exit",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}