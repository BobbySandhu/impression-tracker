package com.impressiontrackingrecyclerview.horizontalandvertical

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.impressiontrackingrecyclerview.databinding.VerticalVisibilityViewBinding
import com.impressiontrackingrecyclerview.databinding.VisibilityViewBinding
import com.impressiontrackingrecyclerview.verticalrecycler.UiData

class VerticalRecyclerAdapter(
    val data: ArrayList<DemoData>,
    val innerAdapters: HashMap<Int, HorizontalRecyclerAdapter?>
) : RecyclerView.Adapter<VerticalRecyclerAdapter.VisibilityView>() {

    private lateinit var viewHolder: VisibilityView
    private val scrollStates: MutableMap<String, Parcelable?> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisibilityView {
        viewHolder = VisibilityView(
            parent.context,
            VerticalVisibilityViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        return viewHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: VisibilityView, position: Int) {
        holder.bind(position)
        restoreStates(
            getSectionID(holder.layoutPosition),
            holder.binding.recyclerViewInner
        )
    }

    private fun getSectionID(position: Int): String? {
        return if (position >= 0 && position < data.size) {
            data[position].index.toString()
        } else {
            null
        }
    }

    override fun onViewRecycled(holder: VisibilityView) {
        super.onViewRecycled(holder)

        val key = getSectionID(holder.layoutPosition)
        if (key.isNullOrEmpty()) {
            return
        }

        scrollStates[key] = holder.binding.recyclerViewInner.layoutManager?.onSaveInstanceState()
    }

    private fun restoreStates(key : String?, recyclerView: RecyclerView?) {
        if (key == null || recyclerView == null) {
            return
        }

        val state = scrollStates[key]
        if (state != null) {
            recyclerView.layoutManager?.onRestoreInstanceState(state)
        } else {
            recyclerView.layoutManager?.scrollToPosition(0)
        }
    }

    fun updateHorizontalItem(uiData: UiData, position: Int, parentPos: Int) {
        viewHolder.update(uiData, position, parentPos)
    }

    inner class VisibilityView(private val ctx: Context, private val view: VerticalVisibilityViewBinding) :
        RecyclerView.ViewHolder(view.root) {

        private var innerAdapter: HorizontalRecyclerAdapter? = null
        val binding = view

        fun bind(pos: Int) {
            if (innerAdapters.get(pos) != null) {
                innerAdapter = innerAdapters.get(pos)
            } else {
                val innerAdapterr = HorizontalRecyclerAdapter(data[pos].innerData)
                binding.recyclerViewInner.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                innerAdapters.put(pos, innerAdapterr)
                innerAdapter = innerAdapterr
            }

            binding.recyclerViewInner.adapter = innerAdapter
            binding.textIndex.text = pos.toString()
        }

        fun update(uiData: UiData, position: Int, parentPos: Int) {
            innerAdapter = innerAdapters.get(parentPos)
            innerAdapter?.updateData(uiData, position)
            innerAdapter?.notifyItemChanged(position)
        }
    }
}