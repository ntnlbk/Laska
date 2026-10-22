package laska.daily.bible.meditation.presentation.homefragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.domain.courses.Author

class AuthorsAdapter(
    private val onAuthorClick: (Author) -> Unit
) : ListAdapter<Author, AuthorsAdapter.AuthorViewHolder>(AuthorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_author, parent, false)
        return AuthorViewHolder(view)
    }

    override fun onBindViewHolder(holder: AuthorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AuthorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAvatar: ShapeableImageView = itemView.findViewById(R.id.ivAvatar)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)

        fun bind(author: Author) {
            tvName.text = author.name

            if (author.imageURL != "") {
                ivAvatar.load(author.imageURL){
                    crossfade(true)
                    placeholder(R.drawable.author_default_ic)
                    error(R.drawable.author_default_ic)
                }
            } else {
                ivAvatar.setImageResource(R.drawable.author_default_ic)
            }

            itemView.setOnClickListener {
                onAuthorClick(author)
            }
        }
    }

    class AuthorDiffCallback : DiffUtil.ItemCallback<Author>() {
        override fun areItemsTheSame(oldItem: Author, newItem: Author): Boolean {
            return oldItem.id == newItem.id // Сравниваем по уникальному id автора
        }

        override fun areContentsTheSame(oldItem: Author, newItem: Author): Boolean {
            return oldItem == newItem // Сравниваем содержимое
        }
    }
}