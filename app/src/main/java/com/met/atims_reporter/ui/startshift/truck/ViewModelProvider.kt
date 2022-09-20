package com.met.atims_reporter.ui.startshift.truck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.met.atims_reporter.repository.Repository

class ViewModelProvider(
    private val repository: Repository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return com.met.atims_reporter.ui.startshift.truck.ViewModel(
            repository
        ) as T
    }
}