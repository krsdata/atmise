package com.met.atims_reporter.ui.fuel_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.met.atims_reporter.repository.Repository

class ViewModelProvider(
    private val repository: Repository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ViewModel(
            repository
        ) as T
    }
}