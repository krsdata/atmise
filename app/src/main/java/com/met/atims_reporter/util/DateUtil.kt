package com.met.atims_reporter.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@Suppress("unused")
object DateUtil {
    @SuppressLint("SimpleDateFormat")
    fun getDateToShowInUI(timeMills: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeMills

        val simpleDateFormat =
            if (cal.get(Calendar.YEAR) > Calendar.getInstance().get(Calendar.YEAR))
                SimpleDateFormat("EEE',' dd MMM yyyy hh:mm")
            else
                SimpleDateFormat("EEE',' dd MMM hh:mm")
        return simpleDateFormat.format(cal.time)
    }


    @SuppressLint("SimpleDateFormat")
    fun getDateTimeToShowInUI(timeMills: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeMills

        val simpleDateFormat =
            if (cal.get(Calendar.YEAR) > Calendar.getInstance().get(Calendar.YEAR))
                SimpleDateFormat("EEE',' dd MMM yyyy hh':'mm")
            else
                SimpleDateFormat("EEE',' dd MMM hh':'mm")
        return simpleDateFormat.format(cal.time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getMillsFromServerTime(serverTimeString: String): Long? {
        val simpleDateFormatUTC = SimpleDateFormat("yyyy-MM-dd hh:mm a")
        simpleDateFormatUTC.timeZone = TimeZone.getTimeZone("UTC")
        val simpleDateFormatLocal = SimpleDateFormat("yyyy-MM-dd'T'hh':'mm':'ss'.'SSS'Z'")
        simpleDateFormatLocal.timeZone = TimeZone.getDefault()
        val utcDate = simpleDateFormatUTC.parse(serverTimeString)
        utcDate?.let {
            val localString = simpleDateFormatLocal.format(it)
            val localDate = simpleDateFormatLocal.parse(localString)
            return localDate?.let { date ->
                return date.time
            } ?: run {
                return null
            }
        } ?: run {
            return null
        }

    }

    @SuppressLint("SimpleDateFormat")
    fun getDateStringForServerMDY(timeMills: Long = Calendar.getInstance().timeInMillis): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeMills

        val simpleDateFormat = SimpleDateFormat("MM-dd-yyyy")
        return simpleDateFormat.format(cal.time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateStringForServerDMY(timeMills: Long = Calendar.getInstance().timeInMillis): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeMills

        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        return simpleDateFormat.format(cal.time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateStringForServerYMD(timeMills: Long = Calendar.getInstance().timeInMillis): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeMills

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        return simpleDateFormat.format(cal.time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimeStringForServer(
        timeMills: Long = Calendar.getInstance().timeInMillis,
        isTwelveHoursFormat: Boolean = false
    ): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeMills

        val simpleDateFormat =
            if (isTwelveHoursFormat) SimpleDateFormat("hh:mm a") else SimpleDateFormat("HH:mm")
        return simpleDateFormat.format(cal.time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getUTCTimeStringForServer(): String {
        val cal = Calendar.getInstance()

        val simpleDateFormatUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz")
        simpleDateFormatUTC.timeZone = TimeZone.getDefault()

        val resultToSend =  simpleDateFormatUTC.format(Date(cal.timeInMillis)).replace("GMT", "")

        return resultToSend
    }
}