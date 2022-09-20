package com.met.atims_reporter.util

import android.view.View

class DoubleClickListener(
    private val doubleClickTimeLimitMills: Long = 1000L,
    private val callback: Callback
) :
    View.OnClickListener {
    private var lastClicked: Long = -1L

    override fun onClick(v: View?) {
        lastClicked = when {
            lastClicked == -1L -> {
                System.currentTimeMillis()
            }
            isDoubleClicked() -> {
                callback.doubleClicked()
                -1L
            }
            else -> {
                System.currentTimeMillis()
            }
        }
    }

    private fun getTimeDiff(from: Long, to: Long) = to - from

    private fun isDoubleClicked() =
        getTimeDiff(
            lastClicked,
            System.currentTimeMillis()
        ) <= doubleClickTimeLimitMills

    interface Callback {
        fun doubleClicked()
    }
}