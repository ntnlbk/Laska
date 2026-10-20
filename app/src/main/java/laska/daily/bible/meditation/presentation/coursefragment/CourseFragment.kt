package laska.daily.bible.meditation.presentation.coursefragment

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.databinding.FragmentCourseBinding
import laska.daily.bible.meditation.domain.courses.Author
import laska.daily.bible.meditation.domain.courses.Episode
import laska.daily.bible.meditation.presentation.homefragment.AuthorsAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CourseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CourseFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentCourseBinding? = null

    private val binding: FragmentCourseBinding
        get() = _binding ?: throw Exception("FragmentCourseBinding is null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = FragmentCourseBinding.inflate(inflater, container, false)
        }
        val parent = binding.root.parent as? ViewGroup
        parent?.removeView(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val authorsAdapter = AuthorsAdapter({})
        val sampleAuthors = listOf(
            Author(id = "1", name = "а. Яўген Голуб", imageResId = R.drawable.test_author_ic),
            Author(id = "2", name = "a. Имя Фамилия", imageResId = R.drawable.default_author_icon),
            Author(id = "3", name = "a. Имя Фамилия", imageResId = R.drawable.default_author_icon),
            Author(id = "4", name = "a. Имя Фамилия", imageResId = R.drawable.default_author_icon)
        )
        binding.rvAuthors.adapter = authorsAdapter
        authorsAdapter.submitList(sampleAuthors)
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

        val episodes = listOf(
            Episode(0, "Episode title", "subtitle", "5 min", true, false),
            Episode(1, "Episode title", "subtitle", "5 min", true, false),
            Episode(2, "Episode title", "subtitle", "5 min", true, false),
            Episode(3, "Episode title", "subtitle", "5 min", true, false),
            Episode(4, "Episode title", "subtitle", "5 min", false, false),
            Episode(5, "Episode title", "subtitle", "5 min", false, true),
            Episode(6, "Episode title", "subtitle", "5 min", false, true),
            Episode(7, "Episode title", "subtitle", "5 min", false, true),
        )
        binding.containerEpisodes.removeAllViews() // Очищаем на всякий случай
        for (episode in episodes) {
            // Надуваем макет одного эпизода (item_episode.xml)
            val episodeView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_episode, binding.containerEpisodes, false)

            // Наполняем данными
            episodeView.findViewById<TextView>(R.id.tvEpisodeTitle).text =
                "${episode.number} ${episode.title}"
            episodeView.findViewById<TextView>(R.id.tvEpisodeSubtitle).text = episode.subtitle
            episodeView.findViewById<TextView>(R.id.tvEpisodeTime).text = episode.time

            if (episode.isClosed) {
                episodeView.findViewById<ImageView>(R.id.ivEpisodeStatus)
                    .setImageResource(R.drawable.ic_closed_episode)
                episodeView.findViewById<TextView>(R.id.tvEpisodeTitle)
                    .setTextColor("#717274".toColorInt())
                episodeView.findViewById<TextView>(R.id.tvEpisodeSubtitle)
                    .setTextColor("#4d4d4d".toColorInt())
                episodeView.findViewById<TextView>(R.id.tvEpisodeTime)
                    .setTextColor("#4d4d4d".toColorInt())

            } else {
                if (episode.isCompleted) {
                    episodeView.findViewById<ImageView>(R.id.ivEpisodeStatus)
                        .setImageResource(R.drawable.ic_completed)
                } else {
                    episodeView.findViewById<ImageView>(R.id.ivEpisodeStatus)
                        .setImageResource(R.drawable.ic_play)
                }
            }
            episodeView.setOnClickListener {
                findNavController().navigate(R.id.action_courseFragment_to_episodeFragment)
            }

            binding.containerEpisodes.addView(episodeView)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CourseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CourseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}