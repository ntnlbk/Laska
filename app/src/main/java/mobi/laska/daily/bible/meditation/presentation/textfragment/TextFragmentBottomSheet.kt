package mobi.laska.daily.bible.meditation.presentation.textfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mobi.laska.daily.bible.meditation.databinding.FragmentTextBottomSheetBinding
import mobi.laska.daily.bible.meditation.presentation.mainfragment.TextsToShow
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.lyricsText.text = "Bible Text:\n" +
            textsToShow.bibleTextPlain +
                "\nReflection Intro:\n" +
                textsToShow.reflectionTextIntro +
                "\nReflection Body:\n" +
                textsToShow.reflectionTextBody +
                "\nFeast name:\n" +
                textsToShow.feastName
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