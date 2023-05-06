package com.impressiontrackingrecyclerview.verticalrecycler

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
import kotlinx.coroutines.delay

class VerticalRecyclerViewImpressionTrackingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerticalRecyclerViewImpressionTrackingBinding
    private var adapter: VerticalRecyclerAdapter? = null
    private var impressionTracker: ImpressionTracker? = null
    private val items = populateItems()

    private var isFirstLoad = true
    private var handler = Handler(Looper.getMainLooper())

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
        adapter = VerticalRecyclerAdapter(items)
        binding.recyclerViewVertical.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewVertical.adapter = adapter
    }

    private fun setImpressionTracking() {
        impressionTracker = ImpressionTracker(binding.recyclerViewVertical, 40, object : ImpressionTrackerListener {
            override fun onVerticalItem(position: Int, viewHolder: RecyclerView.ViewHolder?) {
                /* use as per your use case */
            }

            override fun onHorizontalItem(parentPosition: Int, childPosition: Int) {
                /* use as per your use case */
            }

            override fun onVerticalItemVisibility(visibility: Double, position: Int) {
                adapter?.updateData(items[position].copy(visibility = visibility.toInt()), position)

                if (isFirstLoad) {//added a delay to refresh the recycler, as it doesn't update when it's getting set.
                    handler.postDelayed({
                        adapter?.notifyItemChanged(position)
                        isFirstLoad = false
                    }, 100)
                } else adapter?.notifyItemChanged(position)
            }

            override fun onHorizontalItemVisibility(
                visibility: Double,
                parentPosition: Int,
                childPosition: Int
            ) {
                /* use as per your use case */
            }
        })
    }

    private fun populateItems(): ArrayList<UiData> {
        val arrayList = ArrayList<UiData>()
        repeat(50) { index ->
            arrayList.add(UiData(0))
        }

        return arrayList
    }
}