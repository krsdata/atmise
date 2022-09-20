package com.met.atims_reporter.util

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever

@Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
object VideoThumbnailUtil {
    fun retrieveVideoFrameFromVideo(videoPath: String, callback: Callback) {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(videoPath, HashMap())
            bitmap =
                mediaMetadataRetriever.getFrameAtTime(
                    2 * 1000000,
                    MediaMetadataRetriever.OPTION_CLOSEST
                )
            callback.gotThumbnail(bitmap!!)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaMetadataRetriever?.release()
        }
    }

    interface Callback {
        fun gotThumbnail(thumbNail: Bitmap)
    }
}