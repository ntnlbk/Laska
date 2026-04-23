package mobi.laska.daily.bible.meditation.presentation.textfragment

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import mobi.laska.daily.bible.meditation.R
import mobi.laska.daily.bible.meditation.databinding.FragmentTextBottomSheetBinding
import mobi.laska.daily.bible.meditation.presentation.mainfragment.AudioPlayerState
import mobi.laska.daily.bible.meditation.presentation.mainfragment.MainFragmentViewModel
import mobi.laska.daily.bible.meditation.presentation.mainfragment.DialogArguments
import kotlin.getValue

class TextFragmentBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentTextBottomSheetBinding? = null
    private val binding: FragmentTextBottomSheetBinding
        get() = _binding ?: throw Exception("FragmentTextBottomSheetBinding is null")

    private val viewModel: MainFragmentViewModel by activityViewModels()

    private var dialogArguments: DialogArguments = DialogArguments()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dialogArguments = (it.getSerializable(TEXTS_TO_SHOW_KEY)) as DialogArguments
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
            (view?.parent as ViewGroup).background =
                Color.parseColor("#F8F8F6").toDrawable()
        }

        return dialog
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupViews()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playerUIState.collect {
                when (it) {
                    is AudioPlayerState.Paused -> {
                        binding.dialogPlayerProgressBar.progress = it.progress
                        binding.playBtn.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.play_dialog_ic)
                        )
                    }
                    is AudioPlayerState.Playing -> {
                        binding.dialogPlayerProgressBar.progress = it.progress
                        binding.playBtn.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.pause_dialog_ic)
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupViews() {
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
        binding.tv1.text = dialogArguments.reflectionTextIntro
        binding.tv2.text = dialogArguments.bibleTextPlain
        binding.tv3.text = dialogArguments.reflectionTextBody
        binding.btnBack.setOnClickListener {
            dialog?.cancel()
        }
        binding.feastNameTv.text = dialogArguments.bibleRef
        binding.playBtn.setOnClickListener {
            viewModel.playButtonClicked()
        }
        binding.dialogPlayerProgressBar.max = dialogArguments.songMaxProgress
        binding.dialogPlayerProgressBar.progress = dialogArguments.actualProgress
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
            texts: DialogArguments
        ) =
            TextFragmentBottomSheet().apply {
                arguments = Bundle().apply {
                    putSerializable(TEXTS_TO_SHOW_KEY, texts)
                }
            }
    }
}