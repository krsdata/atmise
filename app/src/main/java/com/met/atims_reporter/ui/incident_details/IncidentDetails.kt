package com.met.atims_reporter.ui.incident_details

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager.widget.ViewPager
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.core.KeyWordsAndConstants.DATA
import com.met.atims_reporter.databinding.ActivityIncidentDetailsBinding
import com.met.atims_reporter.databinding.DialogDirectionDescriptionBinding
import com.met.atims_reporter.databinding.IncidentDetailsCarouselItemBinding
import com.met.atims_reporter.enums.OperationMode
import com.met.atims_reporter.model.IncidentDetails
import com.met.atims_reporter.ui.add_incident.data.AddIncidentDataActivity
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.ui.incident_details.adapter.IncidentDetailsFragAdater
import com.met.atims_reporter.ui.incidents.Incidents
import com.met.atims_reporter.widget.spinner.Spinner
import com.met.atims_reporter.widget.spinner.SpinnerData
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import kotlin.random.Random

class IncidentDetails : AtimsSuperActivity() {

    private lateinit var binding: ActivityIncidentDetailsBinding
    private lateinit var incidentDetails: IncidentDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(
            R.layout.activity_incident_details
        )

        intent.getStringExtra(DATA)?.let {
            incidentDetails = fromJson(it)
        }

        showIncidentsActionBar()
        setPageTitle("IND# ${incidentDetails.Incidents_report_data_id}")
        enableBackButton()
        willHandleBackNavigation()
        setUpCarousel()
        setUpTabs()

        binding.content.textViewHeaderContent.text = incidentDetails.comments
    }

    override fun onResume() {
        super.onResume()
        GSYVideoType.setShowType(
            GSYVideoType.SCREEN_TYPE_DEFAULT
        )
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.releaseAllVideos()
    }

    override fun goBack() {
        GSYVideoManager.releaseAllVideos()
        sendBroadcast(
            Intent("getHomeGrid")
        )
        startActivity(
            Intent(
                this,
                Incidents::class.java
            )
        )
        finish()
    }

    private fun setUpCarousel() {
        val demoImages: ArrayList<String> = ArrayList()
        demoImages.add("https://picsum.photos/400?${Random.nextInt()}")
        demoImages.add("https://picsum.photos/400?${Random.nextInt()}")
        demoImages.add("https://picsum.photos/400?${Random.nextInt()}")
        demoImages.add("https://picsum.photos/400?${Random.nextInt()}")
        demoImages.add("https://picsum.photos/400?${Random.nextInt()}")
        demoImages.add("https://picsum.photos/400?${Random.nextInt()}")
        binding.content.carousel.pageCount = demoImages.size
        binding.content.carousel.setViewListener { position ->
            val incidentDetailsCarouselItemBinding =
                IncidentDetailsCarouselItemBinding.inflate(
                    layoutInflater,
                    null,
                    false
                )
            incidentDetailsCarouselItemBinding.imageView.setImage(
                demoImages[position]
            )
            incidentDetailsCarouselItemBinding.root
        }
    }

    private fun setUpTabs() {
        binding.content.viewPager.adapter = IncidentDetailsFragAdater(
            supportFragmentManager,
            incidentDetails
        )
        binding.content.tabLayout.setupWithViewPager(
            binding.content.viewPager
        )
        binding.content.viewPager.addOnPageChangeListener(
            object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }
                override fun onPageSelected(position: Int) {
                    GSYVideoManager.releaseAllVideos()
                }
            }
        )
    }

    override fun addIncident() {
        super.addIncident()
        directionDescriptionsDialog(
            OperationMode.ADD,
            incidentDetails
        )
    }

    @SuppressLint("SetTextI18n")
    private fun directionDescriptionsDialog(
        @Suppress("SameParameterValue") mode: OperationMode,
        incidentDetails: IncidentDetails? = null
    ) {
        val dialogBinding = DialogDirectionDescriptionBinding.inflate(
            LayoutInflater.from(this),
            null,
            false
        )
      val   directionAndDescriptionDialog = Dialog(this)
        directionAndDescriptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        directionAndDescriptionDialog.setContentView(dialogBinding.root)

        directionAndDescriptionDialog.window
            ?.setLayout(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            )

        directionAndDescriptionDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        directionAndDescriptionDialog.setCancelable(true)
        directionAndDescriptionDialog.show()

        var selectedDirection = "One Direction"

        dialogBinding.spinnerDirection.apply {
            heading("Direction")
            addItems(
                arrayListOf(
                    SpinnerData(
                        "One Direction",
                        "One Direction"
                    ),
                    SpinnerData(
                        "Both Direction",
                        "Both Direction"
                    )
                ),
                object : Spinner.OnItemSelectedListener {
                    override fun <T> selected(item: SpinnerData<T>) {
                        selectedDirection = item.data as String
                    }
                }
            )
        }

        var selectedDescription = "Motor Vehicle Accident"

        dialogBinding.spinnerDescription.heading("Description")
        dialogBinding.spinnerDescription.addItems(
            arrayListOf(
                SpinnerData(
                    "Motor Vehicle Accident",
                    "Motor Vehicle Accident"
                ),
                SpinnerData(
                    "Safety Service Operator-Roadside Assistance",
                    "Safety Service Operator-Roadside Assistance"
                ),
                SpinnerData(
                    "Debris in Road",
                    "Debris in Road"
                ),
                SpinnerData(
                    "Disabled Vehicle",
                    "Disabled Vehicle"
                )
            ),
            object : Spinner.OnItemSelectedListener {
                override fun <T> selected(item: SpinnerData<T>) {
                    selectedDescription = item.data as String
                }
            }
        )
        dialogBinding.spinnerIncidentType.visibility = View.GONE
        dialogBinding.buttonActionOne.text = "Submit"

        dialogBinding.buttonActionTwo.text = "Cancel"

        dialogBinding.buttonActionTwo.setOnClickListener {
            directionAndDescriptionDialog.dismiss()
        }

        dialogBinding.buttonActionOne.setOnClickListener {
            if (
                mode == OperationMode.ADD
            ) {
                startActivity(
                    Intent(
                        this,
                        AddIncidentDataActivity::class.java
                    )
                        .putExtra(
                            KeyWordsAndConstants.OPERATION_MODE,
                            mode
                        )
                        .putExtra(
                            AddIncidentDataActivity.DIRECTION,
                            selectedDirection
                        )
                        .putExtra(
                            AddIncidentDataActivity.DESCRIPTION,
                            selectedDescription
                        )
                )
            } else {
                startActivity(
                    Intent(
                        this,
                        AddIncidentDataActivity::class.java
                    )
                        .putExtra(
                            KeyWordsAndConstants.OPERATION_MODE,
                            mode
                        )
                        .putExtra(
                            DATA,
                            toJson(incidentDetails!!)
                        )
                        .putExtra(
                            AddIncidentDataActivity.DIRECTION,
                            selectedDirection
                        )
                        .putExtra(
                            AddIncidentDataActivity.DESCRIPTION,
                            selectedDescription
                        )
                )
            }
            directionAndDescriptionDialog.dismiss()
        }
    }
}
