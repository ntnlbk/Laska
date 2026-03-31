package com.flynid.laska.presentation.mainfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.flynid.laska.databinding.FragmentMainBinding
import com.flynid.laska.domain.Language
import com.flynid.laska.presentation.textfragment.TextFragmentBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() = _binding ?: throw Exception("FragmentMainBinding is null")

    private val viewModel: MainFragmentViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupViews()
    }

    private fun setupViews() {
        binding.button.setOnClickListener {
            viewModel.showReadingText("20260330", Language.BY)
        }
        binding.testPlay.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.play("20260330", Language.BY)
            }

        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is MainFragmentState.Content -> {
                        showTextFragment(it.readingText)
                    }

                    is MainFragmentState.Progress -> {
                        binding.testTv.text = "progress"
                    }
                }
            }
        }
    }

    private fun showTextFragment(readingText: String) {
        val instance = TextFragmentBottomSheet.newInstance(readingText)
        instance.show(requireActivity().supportFragmentManager, TextFragmentBottomSheet.TAG)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}