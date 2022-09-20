package com.met.atims_reporter.model

import android.graphics.drawable.Drawable
import com.met.atims_reporter.util.DropDownItem

data class SpinnerData(val data :String) : DropDownItem {

    override fun getId(): String {
        return data
    }

    override fun getTitle(): String {
        return data
    }

    override fun getItemIcon(): Drawable? {
        return null
    }



}


