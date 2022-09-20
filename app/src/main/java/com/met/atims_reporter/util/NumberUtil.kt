package com.met.atims_reporter.util

object NumberUtil {
    fun roundToTwoDecimal(number: Float): String {
        return "%.2f".format(number)
    }
    fun roundToThreeDecimal(number: Float): String {
        return "%.3f".format(number)
    }
}