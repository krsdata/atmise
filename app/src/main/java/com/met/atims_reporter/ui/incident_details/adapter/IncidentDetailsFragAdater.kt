package com.met.atims_reporter.ui.incident_details.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.met.atims_reporter.model.IncidentDetails
import com.met.atims_reporter.ui.incident_details.frags.details.Details
import com.met.atims_reporter.ui.incident_details.frags.image.Image
import com.met.atims_reporter.ui.incident_details.frags.video.Video

class IncidentDetailsFragAdater(
    fragmentManager: FragmentManager,
    incidentDetails: IncidentDetails
) :
    FragmentPagerAdapter(
        fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    private val details = Details()
    private val image = Image()
    private val video = Video()

    init {
        details.incidentDetails = incidentDetails
        image.incidentDetails = incidentDetails
        video.incidentDetails = incidentDetails
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> details
            1 -> image
            2 -> video
            else -> details
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Details"
            1 -> "Image"
            2 -> "Video"
            else -> ""
        }
    }
}