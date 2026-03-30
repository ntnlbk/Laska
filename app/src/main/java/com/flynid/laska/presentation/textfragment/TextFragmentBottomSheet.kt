package com.flynid.laska.presentation.textfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flynid.laska.databinding.FragmentTextBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TextFragmentBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentTextBottomSheetBinding? = null
    private val binding: FragmentTextBottomSheetBinding
        get() = _binding ?: throw Exception("FragmentTextBottomSheetBinding is null")
    private var readingText: String = EMPTY_STRING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            readingText = it.getString(READING_TEXT_KEY) ?: EMPTY_STRING
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
        binding.lyricsText.text = readingText
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val EMPTY_STRING = ""
        private const val READING_TEXT_KEY = "textReadingParam"

        const val TAG = "TextFragmentBottomSheet"
        @JvmStatic
        fun newInstance(readingText: String) =
            TextFragmentBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(READING_TEXT_KEY, readingText)
                }
            }
    }
}