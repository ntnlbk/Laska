package mobi.laska.daily.bible.meditation.presentation.textfragment

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import mobi.laska.daily.bible.meditation.databinding.FragmentTextBottomSheetBinding
import mobi.laska.daily.bible.meditation.presentation.mainfragment.TextsToShow

class TextFragmentBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentTextBottomSheetBinding? = null
    private val binding: FragmentTextBottomSheetBinding
        get() = _binding ?: throw Exception("FragmentTextBottomSheetBinding is null")

    private var textsToShow: TextsToShow = TextsToShow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            textsToShow = (it.getSerializable(TEXTS_TO_SHOW_KEY)) as TextsToShow
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTextBottomSheetBinding.inflate(layoutInflater)
        return binding.root

    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            //this line transparent your dialog background
            (view?.parent as ViewGroup).background =
                Color.parseColor("#F8F8F6").toDrawable()
        }

        return dialog
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scrollView = binding.scrollView
        val progressBar = binding.headerProgressBar

        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val child = scrollView.getChildAt(0)

            val scrollY = scrollView.scrollY
            val totalHeight = child.height - scrollView.height

            val progress = if (totalHeight > 0) {
                (scrollY.toFloat() / totalHeight * 100).toInt()
            } else {
                0
            }

            progressBar.progress = progress
        }
        setupViews()
    }

    private fun setupViews() {
        binding.tv1.text = textsToShow.reflectionTextIntro
        binding.tv2.text = textsToShow.bibleTextPlain
        binding.tv3.text = textsToShow.reflectionTextBody
        binding.btnBack.setOnClickListener {
            dialog?.cancel()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val EMPTY_STRING = ""

        private const val TEXTS_TO_SHOW_KEY = "textsToShowKey"

        const val TAG = "TextFragmentBottomSheet"

        @JvmStatic
        fun newInstance(
            texts: TextsToShow
        ) =
            TextFragmentBottomSheet().apply {
                arguments = Bundle().apply {
                    putSerializable(TEXTS_TO_SHOW_KEY, texts)
                }
            }
    }
}