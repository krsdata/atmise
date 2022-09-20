package com.met.atims_reporter.widget.image_view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.met.atims_reporter.R
import com.met.atims_reporter.databinding.CustomImageViewBinding
import com.met.atims_reporter.util.DoubleClickListener
import com.met.atims_reporter.util.TextDrawableUtil
import com.met.atims_reporter.util.transformation.CircleTransformWithBorder
import com.met.atims_reporter.util.transformation.CircleTransformation
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import java.io.File
import java.util.*


@Suppress("unused", "MemberVisibilityCanBePrivate")
class ImageView @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    enum class Mode {
        DOWNLOAD, UPLOAD
    }

    enum class ZoomEnabled {
        YES, NO
    }

    enum class ImageLoadedVia {
        BITMAP, DRAWABLE, URL
    }

    companion object {
        const val UPLOAD_UPDATE = "uploadUpdate"
    }

    lateinit var contextForLaterUse: Context
    lateinit var binding: CustomImageViewBinding
    private var mode: Mode =
        Mode.DOWNLOAD
    var scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP
    private var identifier: String = ""

    lateinit var imageLoadedVia: ImageLoadedVia
    lateinit var imageUrl: String
    lateinit var imageBitmap: Bitmap
    lateinit var imageDrawable: Drawable

    private val imageViewDoubleClickListener: OnClickListener = DoubleClickListener(
        callback = object : DoubleClickListener.Callback {
            override fun doubleClicked() {
                Zoom(this@ImageView)
            }
        }
    )

    init {
        context?.let {
            binding = CustomImageViewBinding.inflate(
                it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                this,
                true
            )
            contextForLaterUse = it
        }

        attrs?.let {
            val typedArray = context?.obtainStyledAttributes(
                it, R.styleable.ImageView
            )
            typedArray?.let {
                val type = typedArray.getInteger(
                    R.styleable.ImageView_scaleType, 6
                )
                setScaleType(type)
                val mode = typedArray.getInteger(
                    R.styleable.ImageView_mode, 0
                )
                when (mode) {
                    0 -> setMode(Mode.DOWNLOAD)
                    1 -> setMode(Mode.UPLOAD)
                }
                val isZoomEnabled = typedArray.getInteger(
                    R.styleable.ImageView_enableZoom, 0
                )
                when (isZoomEnabled) {
                    0 -> configureZoom(ZoomEnabled.NO)
                    1 -> configureZoom(ZoomEnabled.YES)
                }
                initiate()
            } ?: run {
                initiate()
            }
            typedArray?.recycle()
        }
    }

    fun setScaleType(type: Int? = null, scaleType: ImageView.ScaleType? = null) {
        type?.let {
            binding.imageView.scaleType = when (it) {
                0 -> ImageView.ScaleType.MATRIX
                1 -> ImageView.ScaleType.FIT_XY
                2 -> ImageView.ScaleType.FIT_START
                3 -> ImageView.ScaleType.FIT_CENTER
                4 -> ImageView.ScaleType.FIT_END
                5 -> ImageView.ScaleType.CENTER
                6 -> ImageView.ScaleType.CENTER_CROP
                7 -> ImageView.ScaleType.CENTER_INSIDE
                else -> ImageView.ScaleType.CENTER_CROP
            }
        }

        scaleType?.let {
            binding.imageView.scaleType = it
        }

        this.scaleType = binding.imageView.scaleType
    }

    private fun setMode(mode: Mode) {
        this.mode = mode

        initiate()
    }

    fun configureZoom(zoomEnabled: ZoomEnabled) {
        if (zoomEnabled == ZoomEnabled.YES) {
            binding.imageView.setOnClickListener(imageViewDoubleClickListener)
        } else {
            binding.imageView.setOnClickListener(null)
        }
    }

    private fun initiate() {
        when (mode) {
            Mode.DOWNLOAD -> {
                binding.progressDownload.visibility = View.VISIBLE
                binding.progressUpload.visibility = View.GONE
            }
            Mode.UPLOAD -> {
                binding.progressDownload.visibility = View.GONE
                binding.progressUpload.visibility = View.VISIBLE
            }
        }
        binding.imageView.scaleType = scaleType
    }

    fun setImage(
        imagePath: File,
        isCircularImage: Boolean = false,
        needBorderWithCircularImage: Boolean = false
    ) {
        mode = Mode.DOWNLOAD
        initiate()

        try {
            val bitmap = BitmapFactory.decodeFile(imagePath.absolutePath)
            var toSetHeight = bitmap.height
            var toSetWidth = bitmap.width
            val percentageChange: Float
            if (
                bitmap.height > binding.imageView.height || bitmap.width > binding.imageView.width
            ) {
                if (bitmap.width > bitmap.height) {
                    toSetWidth = binding.imageView.width
                    percentageChange =
                        (bitmap.width.toFloat() - binding.imageView.width.toFloat()) / bitmap.width.toFloat()
                    toSetHeight = (bitmap.height - (bitmap.height * percentageChange)).toInt()
                } else {
                    toSetHeight = binding.imageView.height
                    percentageChange =
                        (bitmap.height.toFloat() - binding.imageView.height.toFloat()) / bitmap.height.toFloat()
                    toSetWidth = (bitmap.width - (bitmap.width * percentageChange)).toInt()
                }
            }
            var scaledBitmap = Bitmap.createScaledBitmap(
                bitmap,
                toSetWidth,
                toSetHeight,
                true
            )
            if (isCircularImage && needBorderWithCircularImage) {
                scaledBitmap = CircleTransformWithBorder().transform(scaledBitmap)
            } else if (isCircularImage) {
                scaledBitmap = CircleTransformation().transform(scaledBitmap)
            }
            binding.imageView.scaleType = scaleType
            binding.imageView.setImageBitmap(
                scaledBitmap
            )
            doneLoadingImage()

            imageLoadedVia =
                ImageLoadedVia.BITMAP
            imageBitmap = scaledBitmap
        } catch (exception: Exception) {
            Handler().postDelayed(
                {
                    setImage(
                        imagePath, isCircularImage, needBorderWithCircularImage
                    )
                },
                200
            )
        }
    }

    fun setImage(
        imageUrl: String,
        isCircularImage: Boolean = false,
        needBorderWithCircularImage: Boolean = false,
        needPlaceHolderImageForName: String = "",
        appCompatImageView: AppCompatImageView? = null,
        picassoCallback: Callback? = null
    ) {
        this.imageUrl = if (imageUrl == "") "https://www.noimg.com/noimg.jpg" else imageUrl
        imageLoadedVia =
            ImageLoadedVia.URL

        mode = Mode.DOWNLOAD
        initiate()

        binding.imageView.scaleType = scaleType

        try {
            val requestCreator: RequestCreator =
                Picasso.get()
                    .load(this.imageUrl)

            if (scaleType == ImageView.ScaleType.CENTER_CROP) {
                requestCreator
                    .centerCrop()
                    .resize(binding.imageView.measuredWidth, binding.imageView.measuredHeight)
            }

            if (isCircularImage && needBorderWithCircularImage) {
                requestCreator.transform(
                    CircleTransformWithBorder()
                )
            } else if (isCircularImage) {
                requestCreator.transform(
                    CircleTransformation()
                )
            }

            if (needPlaceHolderImageForName != "") {
                TextDrawableUtil.getPlaceHolder(
                    needPlaceHolderImageForName,
                    if (isCircularImage) TextDrawableUtil.Shape.ROUND else TextDrawableUtil.Shape.RECTANGLE
                )?.let {
                    requestCreator.placeholder(
                        it
                    )
                }
            }

            requestCreator
                .into(
                    appCompatImageView ?: binding.imageView,
                    picassoCallback ?: object : Callback {
                        override fun onSuccess() {
                            doneLoadingImage()
                        }

                        override fun onError(e: Exception?) {
                        }
                    }
                )
        } catch (exception: Exception) {
            Handler().postDelayed(
                {
                    setImage(
                        imageUrl,
                        isCircularImage,
                        needBorderWithCircularImage,
                        needPlaceHolderImageForName
                    )
                },
                200
            )
        }
    }

    fun setImage(imageDrawable: Drawable) {
        this.imageDrawable = imageDrawable
        imageLoadedVia =
            ImageLoadedVia.DRAWABLE
        mode = Mode.DOWNLOAD
        initiate()
        binding.imageView.scaleType = scaleType
        binding.imageView.setImageDrawable(imageDrawable)
    }

    fun putPlaceHolder(name: String, isCircularImage: Boolean = false) {
        mode = Mode.DOWNLOAD
        initiate()

        binding.imageView.scaleType = scaleType
        try {
            val requestCreator: RequestCreator =
                Picasso.get()
                    .load("https://dummy.com/this_is_not_an_image.jpg")

            if (scaleType == ImageView.ScaleType.CENTER_CROP) {
                requestCreator
                    .centerCrop()
                    .resize(binding.imageView.measuredWidth, binding.imageView.measuredHeight)
            }

            TextDrawableUtil.getPlaceHolder(
                name,
                if (isCircularImage) TextDrawableUtil.Shape.ROUND else TextDrawableUtil.Shape.RECTANGLE
            )?.let {
                requestCreator.placeholder(
                    it
                )
            }

            requestCreator
                .into(
                    binding.imageView,
                    object : Callback {
                        override fun onSuccess() {
                            doneLoadingImage()
                        }

                        override fun onError(e: Exception?) {
                        }
                    }
                )
        } catch (exception: Exception) {
            Handler().postDelayed(
                {
                    putPlaceHolder(name, isCircularImage)
                },
                200
            )
        }
    }

    fun initiatingOrOnProgressUpload(identifier: String? = null, imageFile: File? = null): String {
        imageFile?.let {
            setImage(imageFile)
        }
        mode = Mode.UPLOAD
        binding.progressUpload.visibility = View.VISIBLE
        binding.progressUpload.indeterminateMode = true
        identifier?.let {
            this@ImageView.identifier = it
        } ?: run {
            this@ImageView.identifier = UUID.randomUUID().toString()
        }
        registerForUpdates()
        return this.identifier
    }

    fun setUpdate(updatePercentage: Int) {
        binding.progressUpload.indeterminateMode = false
        binding.progressUpload.progressMax = 100F
        binding.progressUpload.progress = updatePercentage.toFloat()

        if (
            updatePercentage == 100
        ) {
            binding.progressUpload.visibility = View.GONE
        } else {
            binding.progressUpload.visibility = View.VISIBLE
        }
    }

    private fun doneLoadingImage() {
        binding.progressDownload.visibility = View.GONE
        binding.progressUpload.visibility = View.GONE
    }

    private fun registerForUpdates() {
        context.registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    intent?.let {
                        setUpdate(
                            it.getIntExtra(
                                UPLOAD_UPDATE,
                                -1
                            )
                        )
                    }
                }
            },
            IntentFilter(
                identifier
            )
        )
    }
}