package com.met.atims_reporter.ui.incident_details.frags.video


import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.met.atims_reporter.application.ApplicationClass
import com.met.atims_reporter.databinding.FragmentVideoBinding
import com.met.atims_reporter.model.IncidentDetails
import com.met.atims_reporter.util.repository.Event
import com.shuyu.gsyvideoplayer.utils.FileUtils
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class Video : Fragment(), KodeinAware {

    override lateinit var kodein: Kodein

    private lateinit var requiredContext: Context
    private lateinit var binding: FragmentVideoBinding
    private val provider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    lateinit var incidentDetails: IncidentDetails

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoBinding.inflate(
            inflater,
            null,
            false
        )
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        this.requiredContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kodein = (requiredContext.applicationContext as ApplicationClass).kodein

        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            provider
        )
            .get(
                ViewModel::class.java
            )

        bindToViewModel()

        var video = ""
        incidentDetails.incedent_video?.let {
            if (it.isNotEmpty())
                video = it
        }
        video?.let { videoUrl ->
            if (videoUrl == "") {
                binding.videoPlayer.visibility = View.GONE
                return@let
            }

            binding.videoPlayer.apply {
                setUp(
                    video,
                    true,
                    File(FileUtils.getPath()),
                    null,
                    ""
                )
                backButton.visibility = View.GONE
                fullscreenButton.visibility = View.GONE
                playTag = "${System.currentTimeMillis()}"
                isAutoFullWithSize = false
                isReleaseWhenLossAudio = false
                isShowFullAnimation = false
                keepScreenOn = true
                setIsTouchWiget(false)
                currentPlayer.startPlayLogic()
            }
        } ?: kotlin.run {
            binding.videoPlayer.visibility = View.GONE
        }
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataIncidentDetailsVideoThumb.observe(
            viewLifecycleOwner,
            Observer<Event<Bitmap>> {
            }
        )
    }
}
