package com.bobbysandhu.impressiontracker

import androidx.recyclerview.widget.RecyclerView.ViewHolder

interface ImpressionTrackerListener {
    fun onVerticalItem(position: Int, viewHolder: ViewHolder?)

    fun onHorizontalItem(parentPosition: Int, childPosition: Int)
}