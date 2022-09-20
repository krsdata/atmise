package com.met.atims_reporter.ui.incident_details.frags.image


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.met.atims_reporter.databinding.FragmentImageBinding
import com.met.atims_reporter.model.IncidentDetails

/**
 * A simple [Fragment] subclass.
 */
class Image : Fragment() {

    lateinit var incidentDetails: IncidentDetails

    private lateinit var binding: FragmentImageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentImageBinding.inflate(inflater, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        if (incidentDetails.incedent_photo == null || incidentDetails.incedent_photo == "") {
            binding.imgIncidentImage.visibility = View.GONE
            return
        }
        binding.imgIncidentImage.setImage(
            incidentDetails.incedent_photo!!
        )
    }
}
