package com.bobbysandhu.impressiontracker

interface ImpressionTrackerListener {
    fun onVerticalItem(position: Int)

    fun onHorizontalItem(parentPosition: Int, childPosition: Int)
}