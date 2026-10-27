package laska.daily.bible.meditation.presentation.coursefragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.databinding.FragmentAuthorDetailsDialogBinding

class AuthorDetailsDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAuthorDetailsDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthorDetailsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener { dismiss() }


        val imageUrl = arguments?.getString(AUTHOR_IMAGE_URL) ?: ""
        val authorName = arguments?.getString(AUTHOR_NAME) ?: ""
        val authorDescription = arguments?.getString(AUTHOR_DESCRIPTION) ?: ""

        binding.ivAvatar.load(imageUrl)
        {
            crossfade(true)
            error(R.drawable.home_fragment_courses_section_test_img)
        }
        binding.tvAuthorTitle.text = authorName
        binding.tvAuthorDescription.text = authorDescription

    }

    override fun onStart() {
        super.onStart()
        dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { sheet ->
                sheet.background = null
                sheet.setBackgroundColor(Color.TRANSPARENT)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AuthorDetailsDialogFragment"
        private const val AUTHOR_IMAGE_URL = "author_image_url"
        private const val AUTHOR_NAME = "author_name"
        private const val AUTHOR_DESCRIPTION = "author_description"
        fun newInstance(
            authorImageUrl: String,
            authorName: String,
            authorDescription: String
        ) = AuthorDetailsDialogFragment().apply {
            arguments = Bundle().apply {
                putString(AUTHOR_IMAGE_URL, authorImageUrl)
                putString(AUTHOR_NAME, authorName)
                putString(AUTHOR_DESCRIPTION, authorDescription)
            }
        }
    }
}