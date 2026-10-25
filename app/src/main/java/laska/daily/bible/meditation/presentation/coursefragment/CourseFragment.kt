package laska.daily.bible.meditation.presentation.coursefragment

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.databinding.FragmentCourseBinding
import laska.daily.bible.meditation.domain.courses.EpisodeState
import laska.daily.bible.meditation.presentation.homefragment.AuthorsAdapter


@AndroidEntryPoint
class CourseFragment : Fragment() {

    private var _binding: FragmentCourseBinding? = null

    private val binding: FragmentCourseBinding
        get() = _binding ?: throw Exception("FragmentCourseBinding is null")

    private val viewModel: CourseFragmentViewModel by activityViewModels()

    val args: CourseFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        val parent = binding.root.parent as? ViewGroup
        parent?.removeView(binding.root)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.setContent(args.courseId)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()

    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.courseUIState.collect {
                        when (it) {
                            is CourseFragmentState.Content -> {
                                binding.ivCourseImage.load(
                                    it.course.imageURL
                                ) {
                                    crossfade(true)
                                    placeholder(R.drawable.home_fragment_courses_section_test_img)
                                    error(R.drawable.home_fragment_courses_section_test_img)
                                }
                                binding.tvCourseTitle.text = it.course.title
                                binding.informationTv.text = it.course.description
                                val authorsAdapter = AuthorsAdapter({})
                                binding.rvAuthors.adapter = authorsAdapter
                                authorsAdapter.submitList(it.course.authors)
                                binding.containerEpisodes.removeAllViews()


                                for (episode in it.episodes) {
                                    // Надуваем макет одного эпизода (item_episode.xml)
                                    val episodeView = LayoutInflater.from(requireContext())
                                        .inflate(
                                            R.layout.item_episode,
                                            binding.containerEpisodes,
                                            false
                                        )

                                    // Наполняем данными
                                    episodeView.findViewById<TextView>(R.id.tvEpisodeTitle).text =
                                        "${episode.number} ${episode.title}"
                                    episodeView.findViewById<TextView>(R.id.tvEpisodeSubtitle).text =
                                        episode.subtitle
                                    episodeView.findViewById<TextView>(R.id.tvEpisodeTime).text =
                                        episode.duration.toString()

                                    when (episode.state) {
                                        EpisodeState.AVAILABLE -> {
                                            episodeView.findViewById<ImageView>(R.id.ivEpisodeStatus)
                                                .setImageResource(R.drawable.ic_play)
                                        }

                                        EpisodeState.COMPLETED -> {
                                            episodeView.findViewById<ImageView>(R.id.ivEpisodeStatus)
                                                .setImageResource(R.drawable.ic_completed)
                                        }

                                        EpisodeState.LOCKED -> {
                                            episodeView.findViewById<ImageView>(R.id.ivEpisodeStatus)
                                                .setImageResource(R.drawable.ic_closed_episode)
                                            episodeView.findViewById<TextView>(R.id.tvEpisodeTitle)
                                                .setTextColor("#717274".toColorInt())
                                            episodeView.findViewById<TextView>(R.id.tvEpisodeSubtitle)
                                                .setTextColor("#4d4d4d".toColorInt())
                                            episodeView.findViewById<TextView>(R.id.tvEpisodeTime)
                                                .setTextColor("#4d4d4d".toColorInt())
                                        }
                                    }

                                    episodeView.setOnClickListener {
                                        findNavController().navigate(R.id.action_courseFragment_to_episodeFragment)
                                    }
                                    binding.containerEpisodes.addView(episodeView)
                                }
                            }

                            is CourseFragmentState.Error -> Log.d("COURSES", "ERROR, "+ it.message)
                            CourseFragmentState.Progress -> Log.d("COURSES", "PROGRESS")
                        }
                    }
                }
            }
        }
    }

    private fun setupViews() {
        val detailedInformationButton = binding.detailedInformationBtn
        val informationTv = binding.informationTv
        detailedInformationButton.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.root, AutoTransition().apply {
                duration = 300 // Длительность анимации в миллисекундах (можно поставить 200–400)
            })
            if (informationTv.maxLines == 2) {
                informationTv.maxLines = Int.MAX_VALUE
                detailedInformationButton.text = "Скрыть"
            } else {
                informationTv.maxLines = 2
                detailedInformationButton.text = "Подробнее о курсе"
            }
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}