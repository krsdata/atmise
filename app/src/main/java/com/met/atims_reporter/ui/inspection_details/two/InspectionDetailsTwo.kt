package com.met.atims_reporter.ui.inspection_details.two

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants
import com.met.atims_reporter.databinding.ActivityInspectionDetailsTwoBinding
import com.met.atims_reporter.model.InspectionListDetailsResponce
import com.met.atims_reporter.model.ToolsList
import com.met.atims_reporter.ui.inspection_details.two.adapter.ToolsAdapter

class InspectionDetailsTwo : AtimsSuperActivity() {

    private lateinit var binding: ActivityInspectionDetailsTwoBinding
    private lateinit var inspectionListDetailsResponce: InspectionListDetailsResponce
    private lateinit var toolMissing: ArrayList<ToolsList>
    private lateinit var toolBroken: ArrayList<ToolsList>
    private lateinit var toolPresent: ArrayList<ToolsList>
    private lateinit var toolAdapter: ToolsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_inspection_details_two)

        setPageTitle("INSPECTION DETAILS")
        enableBackButton()

        intent.getStringExtra(KeyWordsAndConstants.DATA)?.let {
            inspectionListDetailsResponce = fromJson(it)
        }
        iniView()
    }

    private fun iniView() {

        toolBroken = ArrayList<ToolsList>()
        toolMissing = ArrayList<ToolsList>()
        toolPresent = ArrayList<ToolsList>()

        binding.textViewTotalPresent.visibility = View.GONE
        binding.textViewTotalMissing.visibility = View.GONE
        binding.textViewTotalBroken.visibility = View.GONE

        for (i in 0 until inspectionListDetailsResponce.toolsReport.size) {
            if (inspectionListDetailsResponce.toolsReport.get(i).tool_status.equals("missing")) {
                toolMissing.add(inspectionListDetailsResponce.toolsReport.get(i))
                binding.textViewTotalMissing.visibility = View.VISIBLE
            } else if (inspectionListDetailsResponce.toolsReport.get(i).tool_status.equals("present")) {
                binding.textViewTotalPresent.visibility = View.VISIBLE
                toolPresent.add(inspectionListDetailsResponce.toolsReport.get(i))
            } else if (inspectionListDetailsResponce.toolsReport.get(i).tool_status.equals("broken")) {
                binding.textViewTotalBroken.visibility = View.VISIBLE
                toolBroken.add(inspectionListDetailsResponce.toolsReport.get(i))
            }
        }

        setAdapterToolBroken(toolBroken)
        setAdapterToolMissing(toolMissing)
        setAdapterToolPresent(toolPresent)

        binding.textComment.text =
            inspectionListDetailsResponce.reportUserData.inspection_reports_comment
        /*   Picasso
               .get()
               .load(
                   inspectionListDetailsResponce.reportUserData.reports_image
               )
               .into(
                   binding.imgCommentImage
               )*/
        if (inspectionListDetailsResponce.reportImages.size > 0) {
            binding.imageViewOne.setImage(inspectionListDetailsResponce.reportImages[0].image)
            binding.imageViewOne.visibility = View.VISIBLE
        }
        if (inspectionListDetailsResponce.reportImages.size > 1) {
            binding.imageViewTwo.setImage(inspectionListDetailsResponce.reportImages[1].image)
            binding.imageViewTwo.visibility = View.VISIBLE
        }
        if (inspectionListDetailsResponce.reportImages.size > 2) {
            binding.imageViewThree.setImage(inspectionListDetailsResponce.reportImages[2].image)
            binding.imageViewThree.visibility = View.VISIBLE
        }
        if (inspectionListDetailsResponce.reportImages.size > 3) {
            binding.imageViewFour.setImage(inspectionListDetailsResponce.reportImages[3].image)
            binding.imageViewFour.visibility = View.VISIBLE
        }
        if (inspectionListDetailsResponce.reportImages.size > 4) {
            binding.imageViewFive.setImage(inspectionListDetailsResponce.reportImages[4].image)
            binding.imageViewFive.visibility = View.VISIBLE
        }
        if (inspectionListDetailsResponce.reportImages.size > 5) {
            binding.imageViewSix.setImage(inspectionListDetailsResponce.reportImages[5].image)
            binding.imageViewSix.visibility = View.VISIBLE
        }
    }

    private fun setAdapterToolBroken(toolList: ArrayList<ToolsList>) {
        toolAdapter = ToolsAdapter(toolList)
        binding.reToolBroken.setHasFixedSize(true)
        binding.reToolBroken.layoutManager = LinearLayoutManager(this)
        binding.reToolBroken.adapter = toolAdapter
        binding.reToolBroken.isNestedScrollingEnabled = false
    }

    private fun setAdapterToolMissing(toolList: ArrayList<ToolsList>) {
        toolAdapter = ToolsAdapter(toolList)
        binding.reToolMisssing.setHasFixedSize(true)
        binding.reToolMisssing.layoutManager = LinearLayoutManager(this)
        binding.reToolMisssing.adapter = toolAdapter
        binding.reToolMisssing.isNestedScrollingEnabled = false
    }

    private fun setAdapterToolPresent(toolList: ArrayList<ToolsList>) {
        toolAdapter = ToolsAdapter(toolList)
        binding.reToolPresent.setHasFixedSize(true)
        binding.reToolPresent.layoutManager = LinearLayoutManager(this)
        binding.reToolPresent.adapter = toolAdapter
        binding.reToolPresent.isNestedScrollingEnabled = false
    }

    override fun goBack() {
        finish()
    }
}
