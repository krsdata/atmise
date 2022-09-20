package com.met.atims_reporter.widget.container

interface WidgetContainerContracts {
    fun setErrorText(errorText: String): WidgetContainer
    fun showError(errorText: String? = null)
    fun hideError()
}