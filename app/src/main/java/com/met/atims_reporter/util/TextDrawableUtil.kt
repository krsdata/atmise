package com.met.atims_reporter.util

import android.graphics.drawable.Drawable
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator

/*
created by SAGAR KUMAR NAYAK
class to create place holder for image views.
dependencies -
project level -
maven { url 'http://dl.bintray.com/amulyakhare/maven' }
app level gradle -
implementation 'com.amulyakhare:com.amulyakhare.textdrawable:1.0.1'
how to use -
to ge the place holder call the getPlaceHolder method with the string to create
the image and the shape you want to create.
*/
object TextDrawableUtil {
    fun getPlaceHolder(stringToShow: String, shape: Shape?): Drawable? {
        when (shape) {
            Shape.ROUND -> return TextDrawable.builder()
                .buildRound(
                    stringToShow.toCharArray()[0]
                        .toString(),
                    ColorGenerator.MATERIAL.getColor(
                        stringToShow
                    )
                )
            Shape.RECTANGLE -> return TextDrawable.builder()
                .buildRect(
                    stringToShow.toCharArray()[0]
                        .toString(),
                    ColorGenerator.MATERIAL.getColor(
                        stringToShow
                    )
                )
        }
        return null
    }

    enum class Shape {
        ROUND, RECTANGLE
    }
}