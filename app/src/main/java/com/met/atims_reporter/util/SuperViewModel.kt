@file:Suppress("unused")

package com.met.atims_reporter.util

import androidx.lifecycle.ViewModel
import com.met.atims_reporter.repository.Repository

open class SuperViewModel(private val repository: Repository) : ViewModel() {

    private var activityPaused = false
    private var shouldNotifyWhileActivityPaused = true

    fun notifyDuringPaused(shouldNotifyWhileActivityPaused: Boolean) {
        this.shouldNotifyWhileActivityPaused = shouldNotifyWhileActivityPaused
    }

    fun activityPaused() {
        activityPaused = true
    }

    fun activityResumed() {
        activityPaused = false
    }

    fun canPushData(): Boolean {
        if (shouldNotifyWhileActivityPaused)
            return true
        return !activityPaused
    }

    fun giveRepository(): Repository {
        return repository
    }
}