package laska.daily.bible.meditation.presentation.episodefragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.databinding.FragmentEpisodeBinding
import laska.daily.bible.meditation.presentation.homefragment.AuthorsAdapter
import laska.daily.bible.meditation.presentation.mainfragment.DialogArguments
import laska.daily.bible.meditation.presentation.textfragment.TextFragmentBottomSheet

@AndroidEntryPoint
class EpisodeFragment : Fragment() {
    private var _binding: FragmentEpisodeBinding? = null

    private val binding: FragmentEpisodeBinding
        get() = _binding ?: throw Exception("FragmentEpisodeBinding is null")

    private val viewModel: EpisodeFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        observeViewModel()

    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.episodeUIState.collect { state ->
                        when (state) {
                            is EpisodeFragmentState.Content -> {
                                binding.progressBar.visibility = View.INVISIBLE
                                binding.ivEpisodeCover.load(state.imageUrl) {
                                    crossfade(true)
                                    error(R.drawable.home_fragment_courses_section_test_img)
                                }
                                binding.dateTv.text = state.title
                                binding.bibleRefTv.text = state.reference
                                binding.feastNameTv.text = state.subtitle
                                val authorsAdapter = AuthorsAdapter({})
                                binding.rvAuthors.adapter = authorsAdapter
                                authorsAdapter.submitList(state.authors)
                                binding.ivEpisodeCover.visibility = View.VISIBLE
                                binding.showTextBtn.setOnClickListener {
                                    val instance = TextFragmentBottomSheet.newInstance(
                                        DialogArguments(
                                            bibleRef = state.title,
                                            reflectionTextBody = state.text
                                        )
                                    )
                                    instance.show(requireActivity().supportFragmentManager, TextFragmentBottomSheet.TAG)
                                }
                            }

                            is EpisodeFragmentState.Error -> {
                                Log.d("COURSES", state.message)
                            }

                            EpisodeFragmentState.Progress -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}