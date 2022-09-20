package com.met.atims_reporter.widget

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.halilibo.bvpkotlin.BetterVideoPlayer
import com.halilibo.bvpkotlin.VideoCallback
import com.met.atims_reporter.R
import com.met.atims_reporter.databinding.VideoPlayerBinding
import kotlin.random.Random

class VideoPlayer @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: VideoPlayerBinding

    init {
        context?.let {
            binding = VideoPlayerBinding.inflate(
                it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                this,
                true
            )
            binding.context = this

            binding.videoPlayer.visibility = View.GONE
            binding.videoPlayerForFiles.visibility = View.GONE
        }
    }

    fun setVideo(url: String) {
        binding.videoPlayer.visibility = View.VISIBLE
        binding.videoPlayer.apply {
            setUp(
                url,
                true,
                null,
                null,
                ""
            )
            backButton.visibility = View.GONE
            fullscreenButton.visibility = View.GONE
            playTag = "svsfsfbsfb${Random.nextInt()}"
            isAutoFullWithSize = false
            isReleaseWhenLossAudio = false
            isShowFullAnimation = false
            keepScreenOn = true
            setIsTouchWiget(false)
            isLooping = true
            currentPlayer.startPlayLogic()
        }
    }

    fun setThumbNail(thumb: Bitmap) {
        binding.videoPlayer.visibility = View.VISIBLE
    }

    fun setVideoUri(uri: Uri) {
        binding.videoPlayerForFiles.visibility = View.VISIBLE
        binding.videoPlayerForFiles.setSource(uri)

        binding.buttonControlVideoPlayerFiles.setOnClickListener {
            if (binding.videoPlayerForFiles.isPlaying())
                binding.videoPlayerForFiles.pause()
            else {
                binding.videoPlayerForFiles.start()
                showPauseButton()
            }
        }

        binding.videoPlayerForFiles.setCallback(
            object : VideoCallback {
                override fun onBuffering(percent: Int) {

                }

                override fun onCompletion(player: BetterVideoPlayer) {
                    showPlayButton()
                    binding.videoPlayerForFiles.seekTo(0)
                }

                override fun onError(player: BetterVideoPlayer, e: Exception) {
                }

                override fun onPaused(player: BetterVideoPlayer) {
                    showPlayButton()
                }

                override fun onPrepared(player: BetterVideoPlayer) {
                    showPlayButton()
                }

                override fun onPreparing(player: BetterVideoPlayer) {
                }

                override fun onStarted(player: BetterVideoPlayer) {
                }

                override fun onToggleControls(player: BetterVideoPlayer, isShowing: Boolean) {
                }
            }
        )
    }

    private fun showPlayButton() {
        binding.buttonControlVideoPlayerFiles.setImageDrawable(
            resources.getDrawable(
                R.drawable.play_video,
                null
            )
        )
    }

    private fun showPauseButton() {
        binding.buttonControlVideoPlayerFiles.setImageDrawable(
            resources.getDrawable(
                R.drawable.ic_pause,
                null
            )
        )
    }
}