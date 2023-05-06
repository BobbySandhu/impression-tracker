package com.impressiontrackingrecyclerview.verticalrecycler

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.impressiontrackingrecyclerview.databinding.VisibilityViewBinding

class VerticalRecyclerAdapter(
    val data: ArrayList<UiData>
) : RecyclerView.Adapter<VerticalRecyclerAdapter.VisibilityView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisibilityView {
        return VisibilityView(
            VisibilityViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: VisibilityView, position: Int) {
        holder.bind(position)
    }

    fun updateData(uiData: UiData, position: Int) {
        data[position] = uiData
    }

    inner class VisibilityView(private val view: VisibilityViewBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(pos: Int) {
            view.textVisibility.text = data[pos].visibility.toString()
            view.textIndex.text = pos.toString()
        }
    }
}