package com.met.atims_reporter.util

import android.content.Context
import android.util.DisplayMetrics
import kotlin.math.roundToInt


/**
 * class to make various conversions.
 */
object ConversionUtil {
    /**
     * to convert dp to px
     *
     * @param dp      your required dp
     * @param context context
     * @return the result px value
     */
    fun dpToPx(dp: Int, context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }
}
