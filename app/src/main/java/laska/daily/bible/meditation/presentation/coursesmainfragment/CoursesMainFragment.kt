package laska.daily.bible.meditation.presentation.coursesmainfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import laska.daily.bible.meditation.databinding.FragmentCoursesMainBinding

@AndroidEntryPoint
class CoursesMainFragment : Fragment() {

    private var _binding: FragmentCoursesMainBinding? = null

    private val binding: FragmentCoursesMainBinding
        get() = _binding ?: throw Exception("FragmentCoursesMainBinding is null")

    private val viewModel: CoursesMainFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoursesMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.coursesMainFragmentState.collect { state ->
                        when (state) {
                            is CoursesMainFragmentState.Content -> {
                                binding.progressBar.visibility = View.INVISIBLE
                                val recyclerView = binding.rvCourses
                                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                                val adapter = CourseAdapter {
                                    val action =
                                        CoursesMainFragmentDirections.actionCoursesMainFragmentToCourseFragment(
                                            it.id
                                        )
                                    findNavController().navigate(action)
                                }
                                adapter.submitList(state.courses)
                                recyclerView.adapter = adapter
                            }

                            is CoursesMainFragmentState.Error -> {

                            }

                            CoursesMainFragmentState.Progress -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}