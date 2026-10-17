package laska.daily.bible.meditation.presentation.homefragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.domain.courses.Author

class AuthorsAdapter(
    private val onAuthorClick: (Author) -> Unit
) : RecyclerView.Adapter<AuthorsAdapter.AuthorViewHolder>() {

    private val authors = mutableListOf<Author>()

    fun submitList(newList: List<Author>) {
        authors.clear()
        authors.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_author, parent, false)
        return AuthorViewHolder(view)
    }

    override fun onBindViewHolder(holder: AuthorViewHolder, position: Int) {
        holder.bind(authors[position])
    }

    override fun getItemCount(): Int = authors.size

    inner class AuthorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAvatar: ShapeableImageView = itemView.findViewById(R.id.ivAvatar)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)

        fun bind(author: Author) {
            tvName.text = author.name

            // Если картинка передана — ставим её, если нет (null) — ставим дефолтную
            if (author.imageResId != null) {
                ivAvatar.setImageResource(author.imageResId)
            } else {
                ivAvatar.setImageResource(R.drawable.bottom_menu_main_ic) // Замените на вашу дефолтную иконку-заглушку
            }

            itemView.setOnClickListener {
                onAuthorClick(author)
            }
        }
    }
}