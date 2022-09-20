package com.met.atims_reporter.widget.spinner

interface SpinnerContracts {
    fun <T> addItems(
        list: ArrayList<SpinnerData<T>>,
        listener: Spinner.OnItemSelectedListener
    ): Spinner

    fun <T> select(toShowString: String? = null, data: T? = null): Spinner
    fun editable(isEditable: Boolean = true): Spinner
    fun mandatory(isMandatory: Boolean): Spinner
}