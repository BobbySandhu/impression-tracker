package com.bobbysandhu.impressiontracker

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A helper class to track impressions based on the visibility percentage.
 * Author: Bobby Sandhu
 **/
class ImpressionTracker(
    private val recyclerView: RecyclerView,
    private val itemVisibilityPercentage: Int = 50,
    private val impressionTrackerListener: ImpressionTrackerListener
) {
    private var isFistLoad = true
    private val globalVisibleRect = Rect()
    private val horizontalVisibleRect = Rect()
    private val itemRect = Rect()

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

                    impressionTrackerListener.onVerticalItemVisibility(
                        segmentVisibilityPercentage,
                        pos
                    )

                    if (segmentVisibilityPercentage >= itemVisibilityPercentage) {
                        impressionTrackerListener.onVerticalItem(
                            pos,
                            recyclerView.findViewHolderForAdapterPosition(pos)
                        )
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

    /** Call this method to start tracking impressions. */
    fun startTracking() {
        recyclerView.addOnScrollListener(scrollListener)
    }

    /** Call this method to stop tracking impressions and removing listeners. */
    fun stopTracking() {
        recyclerView.removeOnScrollListener(scrollListener)
    }

    fun trackHorizontalRecyclerView(
        innerRecyclerView: RecyclerView,
        innerVisibilityPercentage: Int,
        parentPosition: Int
    ) {
        /* adding scroll listener for horizontal recycler view and calculating
        its view visibility percentage before tracking the impression after scroll */
        innerRecyclerView.also { hr ->
            hr.clearOnScrollListeners()
            hr.getGlobalVisibleRect(horizontalVisibleRect)
            hr.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int
                ) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        trackEntityForInitialLoad(
                            hr.layoutManager,
                            innerVisibilityPercentage,
                            parentPosition
                        )
                    }
                }
            })
        }
    }

    /** This method tracks the events for the entities (horizontal recycler view items) when vertical
     * recycler view (segments) loads for the first time as user has not scrolled yet manually.
     * It also prevents the calculation on every vertical scroll input again. */
    private fun trackEntityForInitialLoad(
        innerLayoutManager: RecyclerView.LayoutManager?,
        innerVisibilityPercentage: Int,
        parentPosition: Int
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

                        impressionTrackerListener.onHorizontalItemVisibility(
                            entityVisibilityPercentage,
                            parentPosition,
                            entityPosition
                        )

                        if (entityVisibilityPercentage >= innerVisibilityPercentage) {
                            impressionTrackerListener.onHorizontalItem(
                                parentPosition,
                                entityPosition
                            )
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