package ru.hse.miem.yandexsmarthomeapi.ui.token_entry

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.musfickjamil.snackify.Snackify
import ru.hse.miem.yandexsmarthomeapi.databinding.FragmentTokenEntryBinding
import ru.hse.miem.yandexsmarthomeapi.ui.MainActivityViewModel

class TokenEntryFragment : Fragment() {

    private val viewModel: MainActivityViewModel by activityViewModels()
    private var _binding: FragmentTokenEntryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTokenEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextUrl.setText(viewModel.getUrl())
        binding.editTextToken.setText(viewModel.getToken())

        binding.buttonSubmit.setOnClickListener {
            val token = binding.editTextToken.text.toString()
            val url = binding.editTextUrl.text.toString()

            val urlPatterns = Patterns.WEB_URL
            val urlMatcher = urlPatterns.matcher(url)

            if (urlMatcher.matches() && token.isNotBlank() && url.isNotBlank()) {
                viewModel.saveTokenAndUrl(token, url)
                findNavController()
                    .navigate(TokenEntryFragmentDirections.actionTokenEntryFragmentToDeviceListFragment())
            } else {
                Snackify.error(requireView(), "Необходимо заполнить все поля", Snackify.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}