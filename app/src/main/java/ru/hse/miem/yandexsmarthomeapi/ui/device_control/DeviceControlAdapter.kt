package ru.hse.miem.yandexsmarthomeapi.ui.device_control

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hse.miem.yandexsmarthomeapi.ui.models.DeviceCapabilityUIModel
import ru.hse.miem.yandexsmarthomeapi.ui.models.DevicePropertyUIModel

import ru.hse.miem.yandexsmarthomeapi.ui.views.createCardForCapability
import ru.hse.miem.yandexsmarthomeapi.ui.views.createCardForProperty

class DeviceControlAdapter : ListAdapter<Any, DeviceControlAdapter.DeviceControlViewHolder>(DiffCallback()){

    class DeviceControlViewHolder(val container: FrameLayout) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceControlViewHolder {
        val context = parent.context
        val container = FrameLayout(context)
        container.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return DeviceControlViewHolder(container)
    }

    override fun onBindViewHolder(holder: DeviceControlViewHolder, position: Int) {
        val item = getItem(position)
        val context = holder.container.context
        val view = when (getItemViewType(position)) {
            VIEW_TYPE_CAPABILITY -> context.createCardForCapability(item as DeviceCapabilityUIModel)
            VIEW_TYPE_PROPERTY -> context.createCardForProperty(item as DevicePropertyUIModel)
            else -> throw IllegalArgumentException("Unknown item type")
        }

        holder.container.removeAllViews()
        holder.container.addView(view)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DeviceCapabilityUIModel -> VIEW_TYPE_CAPABILITY
            is DevicePropertyUIModel -> VIEW_TYPE_PROPERTY
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val currentList = currentList.toMutableList()
        val movedItem = currentList.removeAt(fromPosition)
        currentList.add(toPosition, movedItem)
        currentList.forEachIndexed { index, item ->
            when (item) {
                is DeviceCapabilityUIModel -> item.position = index
                is DevicePropertyUIModel -> item.position = index
            }
        }
        submitList(currentList)
    }

    companion object {
        const val VIEW_TYPE_CAPABILITY = 1
        const val VIEW_TYPE_PROPERTY = 2
    }

    class DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is DeviceCapabilityUIModel && newItem is DeviceCapabilityUIModel -> oldItem.id == newItem.id
                oldItem is DevicePropertyUIModel && newItem is DevicePropertyUIModel -> oldItem.id == newItem.id
                else -> oldItem === newItem
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return if(oldItem is DeviceCapabilityUIModel && newItem is DeviceCapabilityUIModel){
                oldItem == newItem
            } else if(oldItem is DevicePropertyUIModel && newItem is DevicePropertyUIModel){
                oldItem == newItem
            } else false

        }
    }
}