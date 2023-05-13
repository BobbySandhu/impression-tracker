package com.impressiontrackingrecyclerview.horizontalandvertical

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bobbysandhu.impressiontracker.ImpressionTracker
import com.bobbysandhu.impressiontracker.ImpressionTrackerListener
import com.impressiontrackingrecyclerview.databinding.ActivityVerticalRecyclerViewImpressionTrackingBinding
import com.impressiontrackingrecyclerview.verticalrecycler.UiData
import kotlinx.coroutines.delay

class HorizontalRecyclerViewImpressionTrackingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerticalRecyclerViewImpressionTrackingBinding
    private var adapter: VerticalRecyclerAdapter? = null
    private var impressionTracker: ImpressionTracker? = null
    private val items = populateItems()

    private var isFirstLoad = true
    private var handler = Handler(Looper.getMainLooper())
    private val innerAdapters = HashMap<Int, HorizontalRecyclerAdapter?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerticalRecyclerViewImpressionTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecyclerView()
        setImpressionTracking()
    }

    override fun onStart() {
        super.onStart()
        impressionTracker?.startTracking()
    }

    override fun onDestroy() {
        super.onDestroy()
        impressionTracker?.stopTracking()
    }

    private fun setRecyclerView() {
        adapter = VerticalRecyclerAdapter(items, innerAdapters)
        binding.recyclerViewVertical.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewVertical.adapter = adapter
    }

    private fun setImpressionTracking() {
        impressionTracker = ImpressionTracker(binding.recyclerViewVertical, 40, object : ImpressionTrackerListener {
            override fun onVerticalItem(position: Int, viewHolder: RecyclerView.ViewHolder?) {
                /* activating/adding inner horizontal recycler impression tracking.
                * horizontal item's callback is received in onHorizontalItemVisibility() and you
                * will have to override it. */
                if (viewHolder is VerticalRecyclerAdapter.VisibilityView) {
                    impressionTracker?.trackHorizontalRecyclerView(viewHolder.binding.recyclerViewInner, 40, position)
                }
            }

            override fun onHorizontalItem(parentPosition: Int, childPosition: Int) {
                /* use as per your use case */
            }

            override fun onVerticalItemVisibility(visibility: Double, position: Int) {
                /* use as per your use case */
            }

            override fun onHorizontalItemVisibility(
                visibility: Double,
                parentPosition: Int,
                childPosition: Int
            ) {
                val innerData = items[parentPosition].innerData
                val uiData = innerData[childPosition].copy(visibility = visibility.toInt())
                innerData[childPosition] = uiData
                items[parentPosition].innerData = innerData

                adapter?.updateHorizontalItem(uiData, childPosition, parentPosition)
            }
        })
    }


    private fun populateItems(): ArrayList<DemoData> {
        val arrayList = ArrayList<DemoData>()

        repeat(50) { index ->
            val innerList = ArrayList<UiData>()
            repeat(20) { innerIndex ->
                innerList.add(UiData(0))
            }
            arrayList.add(DemoData(index, innerList))
        }

        return arrayList
    }
}