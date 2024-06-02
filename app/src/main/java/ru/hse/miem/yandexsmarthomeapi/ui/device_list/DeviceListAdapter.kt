package ru.hse.miem.yandexsmarthomeapi.ui.device_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hse.miem.yandexsmarthomeapi.databinding.ItemDeviceBinding
import ru.hse.miem.yandexsmarthomeapi.ui.models.DeviceUIModel

class DeviceListAdapter(private val onClick: (DeviceUIModel) -> Unit) :
    ListAdapter<DeviceUIModel, DeviceListAdapter.DeviceViewHolder>(DeviceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = ItemDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = getItem(position)
        holder.bind(device, onClick)
    }

    class DeviceViewHolder(private val binding: ItemDeviceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(device: DeviceUIModel, onClick: (DeviceUIModel) -> Unit) {
            binding.itemName.text = device.name
            binding.iconImageView.setImageResource(device.iconResId)
            binding.root.setOnClickListener { onClick(device) }
        }
    }

    class DeviceDiffCallback : DiffUtil.ItemCallback<DeviceUIModel>() {
        override fun areItemsTheSame(oldItem: DeviceUIModel, newItem: DeviceUIModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DeviceUIModel, newItem: DeviceUIModel): Boolean {
            return oldItem == newItem
        }
    }
}