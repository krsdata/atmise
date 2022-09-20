package com.met.atims_reporter.util.model

import com.met.atims_reporter.util.repository.ResultType

data class Result(
    private var error_code: Int,
    private var message: String = "",
    private var result: ResultType? = null
) {
    @Suppress("unused")
    fun isResultOk(): Boolean {
        @Suppress("SENSELESS_COMPARISON")
        if (result == null)
            result =
                if (error_code == 0) ResultType.OK else ResultType.FAIL

        return result == ResultType.OK
    }

    @Suppress("unused")
    fun getMessageToShow(): String {
        return message
    }
}