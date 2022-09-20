package com.met.atims_reporter.util.activity

@Suppress("unused")
interface ShowProgressCallback {
    fun showProgress(numberOfLoader: Int = 1)
    fun hideProgress()
    fun isAnyThingInProgress(): Boolean
}