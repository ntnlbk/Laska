package laska.daily.bible.meditation.presentation.coursesmainfragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.domain.courses.Course
import laska.daily.bible.meditation.presentation.homefragment.AuthorsAdapter

class CourseAdapter(
    private val onCourseClick: (Course) -> Unit
) : ListAdapter<Course, CourseAdapter.CourseViewHolder>(CourseDiffCallback()) {

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCourseImage: ImageView = view.findViewById(R.id.ivCourseImage)
        val tvSessionsCount: TextView = view.findViewById(R.id.tvSessionsCount)
        val tvCourseTitle: TextView = view.findViewById(R.id.tvCourseTitle)
        val tvCourseDescription: TextView = view.findViewById(R.id.tvCourseDescription)
        val rvAuthors: RecyclerView = view.findViewById(R.id.rvAuthors)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = getItem(position)
        holder.ivCourseImage.load(course.imageURL)
        {
            crossfade(true)
            placeholder(R.drawable.home_fragment_courses_section_test_img)
            error(R.drawable.home_fragment_courses_section_test_img)
        }
        holder.tvSessionsCount.text = course.episodeCount.toString()
        holder.tvCourseTitle.text = course.title
        holder.tvCourseDescription.text = course.shortDescription

        val authorAdapter = AuthorsAdapter({})
        authorAdapter.submitList(course.authors)
        holder.rvAuthors.layoutManager = LinearLayoutManager(
            holder.itemView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        holder.rvAuthors.adapter = authorAdapter

        holder.itemView.setOnClickListener {
            onCourseClick(course)
        }
    }

    class CourseDiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id // Сравниваем по уникальному ID
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem // Сравниваем содержимое полей (работает для data class)
        }
    }
}