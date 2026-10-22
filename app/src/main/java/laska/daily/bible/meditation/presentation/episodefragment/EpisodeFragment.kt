package laska.daily.bible.meditation.presentation.episodefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import laska.daily.bible.meditation.databinding.FragmentEpisodeBinding
import laska.daily.bible.meditation.presentation.homefragment.AuthorsAdapter


class EpisodeFragment : Fragment() {
    private var _binding: FragmentEpisodeBinding? = null

    private val binding: FragmentEpisodeBinding
        get() = _binding ?: throw Exception("FragmentEpisodeBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = FragmentEpisodeBinding.inflate(inflater, container, false)
        }
        val parent = binding.root.parent as? ViewGroup
        parent?.removeView(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val authorsAdapter = AuthorsAdapter({})
//        val sampleAuthors = listOf(
//            Author(id = "1", name = "а. Яўген Голуб", imageResId = R.drawable.test_author_ic),
//            Author(id = "2", name = "a. Имя Фамилия", imageResId = R.drawable.default_author_icon),
//            Author(id = "3", name = "a. Имя Фамилия", imageResId = R.drawable.default_author_icon),
//            Author(id = "4", name = "a. Имя Фамилия", imageResId = R.drawable.default_author_icon)
//        )
//        binding.rvAuthors.adapter = authorsAdapter
//        authorsAdapter.submitList(sampleAuthors)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}