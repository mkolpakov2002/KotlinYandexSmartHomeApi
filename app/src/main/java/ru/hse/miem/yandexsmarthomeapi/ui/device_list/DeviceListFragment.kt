package ru.hse.miem.yandexsmarthomeapi.ui.device_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.musfickjamil.snackify.Snackify
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.hse.miem.yandexsmarthomeapi.databinding.FragmentDeviceListBinding
import ru.hse.miem.yandexsmarthomeapi.ui.MainActivityViewModel

class DeviceListFragment : Fragment() {

    private val viewModel: MainActivityViewModel by activityViewModels()
    private var _binding: FragmentDeviceListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupSwipeRefresh()
        setupRecyclerView()
        observeViewModel()
        viewModel.fetchDeviceList()
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Устройства"
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchDeviceList()
        }
    }

    private fun setupRecyclerView() {
        val adapter = DeviceListAdapter {
            val action = DeviceListFragmentDirections.actionDeviceListFragmentToDeviceControlFragment(it.id)
            findNavController().navigate(action)
        }

        binding.recyclerViewDevices.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewDevices.adapter = adapter
    }

    private fun observeViewModel() {
        // Используем viewLifecycleOwner.lifecycleScope для автоматической отмены корутин при уничтожении представления
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.deviceList.collect { devices ->
                        _binding?.let { binding ->
                            (binding.recyclerViewDevices.adapter as DeviceListAdapter).submitList(devices)
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }

                launch {
                    viewModel.errorMessage.collect { errorMessage ->
                        errorMessage?.let {
                            _binding?.let { binding ->
                                Snackify.error(binding.root, it, Snackify.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}