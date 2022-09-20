package com.met.atims_reporter.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class SpacesItemDecoration(private val space: Int) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.left = space / 2
        outRect.right = space / 2
        outRect.bottom = space

        if (
            parent.getChildLayoutPosition(view) == 0 ||
            parent.getChildLayoutPosition(view) % 2 == 0
        ) {
            outRect.left = space
            outRect.right = space / 2
            outRect.bottom = space
        } else if (
            parent.getChildLayoutPosition(view) == 1 ||
            parent.getChildLayoutPosition(view) % 2 == 1
        ) {
            outRect.left = space / 2
            outRect.right = space
            outRect.bottom = space
        }

        if (
            parent.getChildLayoutPosition(view) == 0 ||
            parent.getChildLayoutPosition(view) == 1
        )
            outRect.top = space
        else
            outRect.top = 0
    }
}