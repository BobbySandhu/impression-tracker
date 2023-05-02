package com.bobbysandhu.impressiontracker

import android.graphics.Rect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
 * A helper class to track impressions based on the visibility percentage.
 * Author: Bobby Sandhu
 **/
class ImpressionTracker(
    private val recyclerView: RecyclerView,
    private val impressionTrackerListener: ImpressionTrackerListener,
    private val hasInnerRecycler: Boolean = false,
    private val viewTypes: HashMap<String, String>
) {
    private var isFistLoad = true
    private val globalVisibleRect = Rect()
    private val horizontalVisibleRect = Rect()
    private val itemRect = Rect()
    private val segmentTracked = HashMap<String, Boolean>()

    private companion object {
        const val ITEM_VISIBILITY_PERCENT_SEGMENT = 30
        const val ITEM_VISIBILITY_PERCENT_ENTITY = 15
    }

    /* scroll listener on vertical segment recycler view */
    private var scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                /* user's manual scroll handling. */
                checkForScroll()
            }
        }
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            /* for initial load after adapter set this method works, here we have handled a case
            * after app open to send event for initial visible segment (user hasn't scrolled yet).
            **/
            if (isFistLoad) {
                checkForScroll()
                isFistLoad = false
            }
        }
    }

    private fun checkForScroll() {
        try {
            /* vertical segment recyclerview handling */
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstPosition = layoutManager.findFirstVisibleItemPosition()
            val lastPosition = layoutManager.findLastVisibleItemPosition()

            recyclerView.getGlobalVisibleRect(globalVisibleRect)

            /* segment's visible item calculation */
            for (pos in firstPosition..lastPosition) {
                val segmentView = layoutManager.findViewByPosition(pos)

                if (segmentView != null) {
                    val segmentVisibilityPercentage = getVisibleHeightPercentage(segmentView)

                    if (segmentVisibilityPercentage >= ITEM_VISIBILITY_PERCENT_SEGMENT) {
                        impressionTrackerListener.onVerticalItem(pos)

                        if (hasInnerRecycler) {
                            val viewHolder =
                                recyclerView.findViewHolderForAdapterPosition(pos) ?: return

                            val isValidViewHolder = viewTypes.containsKey(viewHolder::class.simpleName)

                            if (isValidViewHolder) {
                                val currentViewHolderType = viewTypes

                                if (recyclerView.findViewHolderForAdapterPosition(pos) is HomeSegmentView.ViewHolder) {

                                    //fetching segment view's viewholder
                                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(pos)

                                    //a hacky way call entity impressions on first time recycler data load, for inner entities
                                    segmentList?.get(pos)?.id?.let { segmentId ->
                                        if (!segmentTracked.containsKey(segmentId) || segmentTracked.get(segmentId) == false) {
                                            /* Adding segment as tracked and setting value as false, it means
                                            segment is tracked but its entities are not yet tracked. Tracking
                                            the entities and setting the value as true. It doesn't run the
                                            calculations on segment scroll again.
                                            */
                                            segmentTracked.put(segmentId, false)
                                            trackEntityForInitialLoad(viewHolder.recyclerView.layoutManager, segmentId, pos)
                                        }
                                    }

                                    /* adding scroll listener for horizontal recycler view and calculating
                                    its view visibility percentage before tracking the impression after scroll */
                                    viewHolder?.recyclerView.also { hr ->
                                        hr.clearOnScrollListeners()
                                        hr.getGlobalVisibleRect(horizontalVisibleRect)
                                        hr.addOnScrollListener(object : OnScrollListener() {
                                            override fun onScrollStateChanged(
                                                recyclerView: RecyclerView,
                                                newState: Int
                                            ) {
                                                super.onScrollStateChanged(recyclerView, newState)
                                                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                                    if (pos < (segmentList?.size ?: 0) && segmentList?.get(pos) != null) {
                                                        trackEntityForInitialLoad(
                                                            viewHolder.recyclerView.layoutManager,
                                                            segmentList.get(pos)?.id ?: "",
                                                            pos
                                                        )
                                                    }
                                                }
                                            }
                                        })
                                    }
                                } else if (recyclerView.findViewHolderForAdapterPosition(pos) is CustomAdView.ViewHolder) {
                                    ImpressionTracker.trackSegment(
                                        homeSegment = segmentList?.get(pos),
                                        position = pos + 1,
                                        source = source
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } catch (indexOutOfBoundException: ArrayIndexOutOfBoundsException) {
            indexOutOfBoundException.printStackTrace()
        } catch (npe: NullPointerException) {
            npe.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** This method tracks the events for the entities (horizontal recycler view items) when vertical
     * recycler view (segments) loads for the first time as user has not scrolled yet manually.
     * It also prevents the calculation on every vertical scroll input again. */
    private fun trackEntityForInitialLoad(
        innerLayoutManager: LayoutManager?,
        segmentId: String,
        segmentPosition: Int
    ) {
        try {
            if (innerLayoutManager != null) {
                val entityFirstPosition =
                    (innerLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                val entityLastPosition =
                    innerLayoutManager.findLastVisibleItemPosition()

                /* entity's visible item calculation */
                for (entityPosition in entityFirstPosition..entityLastPosition) {
                    val horizontalView = innerLayoutManager.findViewByPosition(entityPosition)
                    if (horizontalView != null) {
                        val entityVisibilityPercentage = getVisibleWidthPercentage(horizontalView)
                        if (entityVisibilityPercentage >= ITEM_VISIBILITY_PERCENT_ENTITY) {
                            ImpressionTracker.trackEntity(
                                homeContent = segmentList?.get(segmentPosition)?.content?.get(
                                    entityPosition
                                ),
                                segmentName = segmentList?.get(segmentPosition)?.title ?: "",
                                segmentPosition = segmentPosition + 1,
                                entityPosition = entityPosition + 1,
                                source = source
                            )
                        }
                    }
                }

                segmentTracked.put(segmentId, true)
            }
        } catch (indexOutOfBoundException: ArrayIndexOutOfBoundsException) {
            indexOutOfBoundException.printStackTrace()
        } catch (npe: NullPointerException) {
            npe.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /** Call this method to start tracking impressions. */
    fun startTracking() {
        recyclerView.addOnScrollListener(scrollListener)
    }

    /** Call this method to stop tracking impressions and removing listeners. */
    fun stopTracking() {
        recyclerView.removeOnScrollListener(scrollListener)
    }

    //Method to calculate how much of the view is visible
    private fun getVisibleHeightPercentage(view: View): Double {
        val isParentViewEmpty = view.getLocalVisibleRect(itemRect)

        // Find the height of the item.
        val visibleHeight = itemRect.height().toDouble()
        val height = view.measuredHeight

        val viewVisibleHeightPercentage = visibleHeight / height * 100

        return if (isParentViewEmpty) {
            viewVisibleHeightPercentage
        } else {
            0.0
        }
    }

    //Method to calculate how much of the view is visible
    private fun getVisibleWidthPercentage(view: View): Double {
        val isParentViewEmpty = view.getLocalVisibleRect(itemRect)

        // Find the width of the item.
        val visibleWidth = itemRect.width().toDouble()
        val width = view.measuredWidth

        val viewVisibleWidthPercentage = visibleWidth / width * 100

        return if (isParentViewEmpty) {
            viewVisibleWidthPercentage
        } else {
            0.0
        }
    }
}