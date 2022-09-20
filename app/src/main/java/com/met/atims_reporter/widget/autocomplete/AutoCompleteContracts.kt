package com.met.atims_reporter.widget.autocomplete

interface AutoCompleteContracts {
    fun <T> addItems(
        list: ArrayList<AutoCompleteData<T>>,
        listener: AutoComplete.OnItemSelectedListener
    ): AutoComplete
}