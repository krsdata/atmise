package com.met.atims_reporter.util

import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.ImageQuality

object ImagePickerUtil {
    fun pickImage(
        context: AppCompatActivity,
        reqCode: Int,
        numberOfImage: Int = 1,
        returnList: ArrayList<String> = ArrayList(),
        orientation: Int = Options.SCREEN_ORIENTATION_PORTRAIT,
        takePictureFrom: Options.TakePictureFrom = Options.TakePictureFrom.GALLERY_AND_CAMERA
    ) {
        val options = Options.init()
            .setRequestCode(reqCode)
            .setCount(numberOfImage)
            .setFrontfacing(false)
            .setImageQuality(ImageQuality.LOW)
            .setPreSelectedUrls(returnList)
            .takePictureFrom(takePictureFrom)
            .setScreenOrientation(orientation)

        Pix.start(
            context,
            options
        )
    }
}