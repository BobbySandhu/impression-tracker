package com.bobbysandhu.impressiontracker

import androidx.recyclerview.widget.RecyclerView.ViewHolder

interface ImpressionTrackerListener {

    /** This callback method is called when provided visibility criteria is met after scroll.
     * @param position returns the position of current recyclerview item.
     * @param viewHolder returns the type of current view-holder in recyclerview.
     **/
    fun onVerticalItem(position: Int, viewHolder: ViewHolder?) {}

    /** This callback method is called when the inner horizontal recyclerview scroll stops when
     * provided visibility criteria is met.
     * @param parentPosition returns the position of the outer recyclerview item.
     * @param childPosition returns the position of the current inner recyclerview item.
     **/
    fun onHorizontalItem(parentPosition: Int, childPosition: Int) {}

    /** This callback method can be used when the vertical recyclerview scroll stops.
     * It should be used when current visibility percentage is required to perform certain tasks.
     * @param visibility returns the visibility percentage of current vertical item.
     * @param position returns the position of the current vertical recyclerview item.
     **/
    fun onVerticalItemVisibility(visibility: Double, position: Int) {}

    /** This callback method can be used when the inner horizontal recyclerview scroll stops.
     * It should be used when current visibility percentage is required to perform certain tasks.
     * @param visibility returns the visibility percentage of current horizontal item.
     * @param parentPosition returns the position of the current vertical recyclerview item.
     * @param childPosition returns the position of the current horizontal recyclerview item.
     **/
    fun onHorizontalItemVisibility(visibility: Double, parentPosition: Int, childPosition: Int) {}
}