package com.met.atims_reporter.ui.add_extra_time
import androidx.lifecycle.ViewModel
import com.met.atims_reporter.repository.Repository
import androidx.lifecycle.ViewModelProvider
class ViewModelProvider(
    private val repository: Repository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ViewModel(repository) as T
    }
}