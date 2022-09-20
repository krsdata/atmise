package com.met.atims_reporter.ui.extra_time_list

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.met.atims_reporter.R
import com.met.atims_reporter.interfaces.OnItemClickListener
import com.met.atims_reporter.ui.extra_time_list.adapter.ExtraTimeListAdapter
import com.met.atims_reporter.ui.extra_time_list.adapter.IncidentNumberAdapter

class IncidentNumberDialog (val mContext:Context):BottomSheetDialog(mContext) {
    private lateinit var rvCheckedList:RecyclerView
    init{
        val displayMetrics = DisplayMetrics()
        window?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_incident_number)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(width, width)
        initView()
    }

    private fun initView() {
        rvCheckedList = findViewById<RecyclerView>(R.id.rvCheckedList) as RecyclerView
        setUpAdapter()
    }

    private fun setUpAdapter() {
        rvCheckedList.apply {
            layoutManager = LinearLayoutManager(mContext)
           adapter = IncidentNumberAdapter(
                mContext,
                object :OnItemClickListener{
                    override fun onItemClick(view: View, position: Int) {
                        when(view.id){
                            R.id.checkBox->{

                            }
                        }
                    }
                }
            )
        }
    }
}