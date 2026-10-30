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
import androidx.fragment.app.viewModels
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
import laska.daily.bible.meditation.domain.courses.Episode
import laska.daily.bible.meditation.domain.courses.EpisodeState
import laska.daily.bible.meditation.presentation.homefragment.AuthorsAdapter
import kotlin.math.roundToInt


@AndroidEntryPoint
class CourseFragment : Fragment() {

    private var _binding: FragmentCourseBinding? = null

    private val binding: FragmentCourseBinding
        get() = _binding ?: throw Exception("FragmentCourseBinding is null")

    private val viewModel: CourseFragmentViewModel by viewModels()

    val args: CourseFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        val parent = binding.root.parent as? ViewGroup
        parent?.removeView(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()

    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.setContent()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.courseUIState.collect { state ->
                        when (state) {
                            is CourseFragmentState.Content -> {
                                binding.ivCourseImage.load(
                                    state.course.imageURL
                                ) {
                                    crossfade(true)
                                    placeholder(R.drawable.home_fragment_courses_section_test_img)
                                    error(R.drawable.home_fragment_courses_section_test_img)
                                }
                                binding.tvCourseTitle.text = state.course.title
                                if(state.course.description == ""){
                                    binding.informationTv.visibility = View.GONE
                                    binding.detailedInformationBtn.visibility = View.GONE
                                } else {
                                    binding.detailedInformationBtn.visibility = View.VISIBLE
                                    binding.informationTv.text = state.course.description
                                }

                                val authorsAdapter = AuthorsAdapter({
                                    AuthorDetailsDialogFragment.newInstance(
                                        it.imageURL,
                                        it.name,
                                        it.description
                                    ).show(
                                        childFragmentManager,
                                        AuthorDetailsDialogFragment.TAG
                                    )
                                })
                                binding.rvAuthors.adapter = authorsAdapter
                                authorsAdapter.submitList(state.course.authors)
                                binding.tvEpisodesCount.text =
                                    state.course.episodeCount.toString() + " sessions"
                                binding.containerEpisodes.removeAllViews()

                                for (episode in state.episodes) {
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

                                    if (episode.subtitle == "") {
                                        episodeView.findViewById<TextView>(R.id.tvEpisodeSubtitle).visibility =
                                            View.GONE
                                    } else {
                                        episodeView.findViewById<TextView>(R.id.tvEpisodeSubtitle).text =
                                            episode.subtitle
                                    }
                                    episodeView.findViewById<TextView>(R.id.tvEpisodeTime).text = (
                                            (episode.duration.toFloat() / 60).roundToInt()
                                                .toString()
                                                    + " мин")

                                    when (episode.state) {
                                        EpisodeState.AVAILABLE -> {
                                            episodeView.findViewById<ImageView>(R.id.ivEpisodeStatus)
                                                .setImageResource(R.drawable.ic_play)
                                            episodeView.setOnClickListener {
                                                openEpisodeFragment(episode, state)
                                            }
                                        }

                                        EpisodeState.COMPLETED -> {
                                            episodeView.findViewById<ImageView>(R.id.ivEpisodeStatus)
                                                .setImageResource(R.drawable.ic_completed)
                                            episodeView.setOnClickListener {
                                                openEpisodeFragment(episode, state)
                                            }
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

                                    binding.containerEpisodes.addView(episodeView)
                                }
                                binding.progressBar.visibility = View.INVISIBLE
                                binding.btnStartCourse.visibility = View.VISIBLE
                            }

                            is CourseFragmentState.Error -> Log.d(
                                "COURSES",
                                "ERROR, " + state.message
                            )

                            CourseFragmentState.Progress -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openEpisodeFragment(
        episode: Episode,
        state: CourseFragmentState.Content
    ) {
        val action =
            CourseFragmentDirections.actionCourseFragmentToEpisodeFragment(
                args.courseId,
                episode.id,
                state.course.imageURL
            )
        findNavController().navigate(action)
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