package laska.daily.bible.meditation.presentation.coursesmainfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.domain.courses.Author
import laska.daily.bible.meditation.domain.courses.Course

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CoursesMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CoursesMainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_courses_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Внутри onViewCreated вашего Fragment:
        val recyclerView: RecyclerView = view.findViewById(R.id.rvCourses)

        // Тестовые данные
        val sampleAuthors = listOf(
            Author(id = "1", name = "а. Яўген Голуб", imageResId = R.drawable.test_author_ic),
            Author(id = "2", name = "a. Имя Фамилия", imageResId = R.drawable.default_author_icon),
            Author(id = "3", name = "a. Имя Фамилия", imageResId = R.drawable.default_author_icon),
            Author(id = "4", name = "a. Имя Фамилия", imageResId = R.drawable.default_author_icon)
        )

        val sampleCourses = listOf(
            Course(
                id = "1",
                imageRes = R.drawable.home_fragment_courses_section_test_img,
                sessionsCount = "26 sessions",
                title = "Name",
                description = "Short description short Short description short",
                authors = sampleAuthors
            ),
            Course(
                id = "2",
                imageRes = R.drawable.home_fragment_courses_section_test_img,
                sessionsCount = "26 sessions",
                title = "Name",
                description = "Short description short Short description short",
                authors = sampleAuthors
            ),
            Course(
                id = "3",
                imageRes = R.drawable.home_fragment_courses_section_test_img,
                sessionsCount = "26 sessions",
                title = "Name",
                description = "Short description short Short description short",
                authors = sampleAuthors
            )
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter =
            CourseAdapter({ findNavController().navigate(R.id.action_coursesMainFragment_to_courseFragment) })
        adapter.submitList(sampleCourses)
        recyclerView.adapter = adapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CoursesMainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CoursesMainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}