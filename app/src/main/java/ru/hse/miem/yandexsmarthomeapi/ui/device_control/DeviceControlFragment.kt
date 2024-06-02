package ru.hse.miem.yandexsmarthomeapi.ui.device_control

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.musfickjamil.snackify.Snackify
import io.github.hyuwah.draggableviewlib.DraggableListener
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import ru.hse.miem.yandexsmarthomeapi.R
import ru.hse.miem.yandexsmarthomeapi.databinding.FragmentDeviceControlBinding
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.ui.MainActivityViewModel
import ru.hse.miem.yandexsmarthomeapi.ui.models.DeviceCapabilityUIModel
import ru.hse.miem.yandexsmarthomeapi.ui.models.DevicePropertyUIModel
import ru.hse.miem.yandexsmarthomeapi.ui.models.DeviceUIModel

class DeviceControlFragment : Fragment() {

    private val viewModel: MainActivityViewModel by activityViewModels()
    private var _binding: FragmentDeviceControlBinding? = null
    private val binding get() = _binding!!
    private val args: DeviceControlFragmentArgs by navArgs()

    private var isUpdatingFromServer = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("DeviceControlFragment", "onViewCreated called with deviceId: ${args.deviceId}")

        setupRecyclerView()
        setupObservers()
        setDeviceNameInToolbar()
    }

    override fun onResume() {
        super.onResume()
        isUpdatingFromServer = true
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Загрузка")
            .setMessage("Пожалуйста, подождите...")
            .setCancelable(false)
            .create()

        dialog.show()

        lifecycleScope.launch {
            viewModel.fetchDeviceState(args.deviceId).invokeOnCompletion {
                isUpdatingFromServer = false
                dialog.dismiss()
                Log.d("DeviceControlFragment", "Device state updated from server")
            }
        }
    }

    private fun setDeviceNameInToolbar() {
        lifecycleScope.launch {
            viewModel.deviceList.collect { devices ->
                val device = devices.find { it.id == args.deviceId }
                device?.let {
                    (activity as? AppCompatActivity)?.supportActionBar?.title = it.name
                    Log.d("DeviceControlFragment", "Toolbar title set to: ${it.name}")
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val adapter = DeviceControlAdapter()
        binding.deviceRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.deviceRecyclerView.adapter = adapter

        binding.deviceRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                outRect.bottom = 16
            }
        })

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition
                adapter.moveItem(fromPosition, toPosition)

                val item = adapter.currentList[toPosition]
                if (item is DeviceCapabilityUIModel) {
                    viewModel.updateCapabilityPosition(args.deviceId, fromPosition, toPosition)
                } else if (item is DevicePropertyUIModel) {
                    viewModel.updatePropertyPosition(args.deviceId, fromPosition, toPosition)
                }
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        itemTouchHelper.attachToRecyclerView(binding.deviceRecyclerView)
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.deviceList.collect { devices ->
                Log.d("DeviceControlFragment", "deviceList updated: $devices")
                val device = devices.find { it.id == args.deviceId }
                if (device != null) {
                    Log.d("DeviceControlFragment", "Device found: $device")
                    setupDeviceView(device)
                } else {
                    Log.d("DeviceControlFragment", "Device not found")
                }

                device?.capabilities?.forEach { capability ->
                    lifecycleScope.launch {
                        capability.stateFlow.collect { state ->
                            if (state != null && !isUpdatingFromServer) {
                                sendUpdateToServer(device.id, capability.id, state)
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    Log.e("DeviceControlFragment", "Error message: $it")
                    Snackify.error(requireView(), it, Snackify.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupDeviceView(device: DeviceUIModel) {
        Log.d("DeviceControlFragment", "Setting up device view for: ${device.name}")
        (activity as? AppCompatActivity)?.supportActionBar?.title = device.name
        val adapter = binding.deviceRecyclerView.adapter as DeviceControlAdapter
        adapter.submitList((device.capabilities + device.properties).sortedBy {
            when (it) {
                is DeviceCapabilityUIModel -> it.position
                is DevicePropertyUIModel -> it.position
                else -> Int.MAX_VALUE
            }
        })
    }

    private fun sendUpdateToServer(deviceId: String, capabilityId: String, newState: CapabilityStateObjectData) {
        lifecycleScope.launch {
            try {
                val device = viewModel.deviceList.value.find { it.id == deviceId }
                val capability = device?.capabilities?.find { it.id == capabilityId }
                capability?.let {
                    Log.d(
                        "DeviceControlFragment",
                        "Sending update to server for deviceId=$deviceId, capabilityId=$capabilityId, newState=$newState"
                    )
                    viewModel.manageDeviceCapability(device.id, capability.id, newState)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Snackify.error(
                    requireView(),
                    "Ошибка при обновлении состояния",
                    Snackify.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}