package com.met.atims_reporter.util.repository

import com.met.atims_reporter.util.model.Result

interface SuperRepositoryCallback<in T> {
    fun success(result: T) {}
    fun notAuthorised() {}
    fun noContent() {}
    fun error(result: Result) {}
}