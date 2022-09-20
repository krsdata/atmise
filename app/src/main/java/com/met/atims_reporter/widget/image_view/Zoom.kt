package com.met.atims_reporter.widget.image_view

import android.app.Dialog
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.met.atims_reporter.databinding.ZoomBinding
import com.met.atims_reporter.util.DoubleClickListener
import com.squareup.picasso.Callback

class Zoom(imageView: ImageView) {

    private var dialog: Dialog = Dialog(imageView.context)
    private var binding: ZoomBinding = ZoomBinding.inflate(
        LayoutInflater.from(imageView.context)
    )

    private val imageViewDoubleClickListener: View.OnClickListener = DoubleClickListener(
        callback = object : DoubleClickListener.Callback {
            override fun doubleClicked() {
                close()
            }
        }
    )

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window
            ?.setLayout(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.MATCH_PARENT
            )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        binding.imageView.scaleType = imageView.binding.imageView.scaleType

        binding.zoomLayout.engine.apply {
            setZoomEnabled(true)
            setFlingEnabled(true)
            setHorizontalPanEnabled(true)
            setScrollEnabled(true)
            setHorizontalPanEnabled(true)
            setVerticalPanEnabled(true)
            setOneFingerScrollEnabled(true)
            setTwoFingersScrollEnabled(true)
            setThreeFingersScrollEnabled(true)
        }
        binding.imageView.setOnClickListener(imageViewDoubleClickListener)

        when (imageView.imageLoadedVia) {
            ImageView.ImageLoadedVia.DRAWABLE -> {
                binding.imageView.setImageDrawable(imageView.imageDrawable)
                performInitialZoom()
            }
            ImageView.ImageLoadedVia.BITMAP -> {
                binding.imageView.setImageBitmap(imageView.imageBitmap)
                performInitialZoom()
            }
            ImageView.ImageLoadedVia.URL -> {
                imageView.setImage(
                    imageUrl = imageView.imageUrl,
                    appCompatImageView = binding.imageView,
                    picassoCallback = object : Callback {
                        override fun onSuccess() {
                            performInitialZoom()
                        }

                        override fun onError(e: Exception?) {
                            dialog.dismiss()
                        }
                    }
                )
            }
        }

        dialog.show()
    }

    private fun close() {
        binding.zoomLayout.zoomTo(
            1F,
            true
        )
        Handler().postDelayed(
            {
                dialog.dismiss()
            },
            500
        )
    }

    private fun performInitialZoom() {
        Handler().postDelayed(
            {
                binding.zoomLayout.zoomTo(
                    2F,
                    true
                )
            },
            500
        )
    }
}