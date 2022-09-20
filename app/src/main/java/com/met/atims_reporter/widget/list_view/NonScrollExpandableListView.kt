package com.met.atims_reporter.widget.list_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView


class NonScrollExpandableListView : ExpandableListView{
   var  expanded = false;
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun setAdapter(adapter: ExpandableListAdapter?) {
        super.setAdapter(adapter)
    }

    override fun setOnChildClickListener(onChildClickListener: OnChildClickListener) {
        super.setOnChildClickListener(onChildClickListener)
    }

    override fun expandGroup(groupPos: Int) : Boolean {
        return super.expandGroup(groupPos)
    }

    override fun expandGroup(groupPos: Int, animate: Boolean) : Boolean {
        return super.expandGroup(groupPos, animate)
    }

    override fun isGroupExpanded(groupPosition: Int): Boolean {
        return super.isGroupExpanded(groupPosition)
    }
    fun isExpanded(): Boolean {
        return expanded
    }
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isExpanded()) {
            val expandSpec =
                MeasureSpec.makeMeasureSpec(View.MEASURED_SIZE_MASK, MeasureSpec.AT_MOST)
            super.onMeasure(widthMeasureSpec, expandSpec)
            val params = layoutParams
            params.height = measuredHeight
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }


}