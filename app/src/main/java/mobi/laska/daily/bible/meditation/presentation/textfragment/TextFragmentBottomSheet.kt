package mobi.laska.daily.bible.meditation.presentation.textfragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import mobi.laska.daily.bible.meditation.R
import mobi.laska.daily.bible.meditation.databinding.FragmentTextBottomSheetBinding
import mobi.laska.daily.bible.meditation.domain.settings.Settings
import mobi.laska.daily.bible.meditation.presentation.mainfragment.AudioPlayerState
import mobi.laska.daily.bible.meditation.presentation.mainfragment.DialogArguments
import mobi.laska.daily.bible.meditation.presentation.mainfragment.MainFragmentViewModel

@AndroidEntryPoint
class TextFragmentBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentTextBottomSheetBinding? = null
    private val binding: FragmentTextBottomSheetBinding
        get() = _binding ?: throw Exception("FragmentTextBottomSheetBinding is null")

    private val viewModel: MainFragmentViewModel by activityViewModels()
    private val settingViewModel: TextFragmentOptionsViewModel by viewModels()

    private var dialogArguments: DialogArguments = DialogArguments()

    private var actualSetting: Settings = Settings()

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

    override fun onStart() {
        super.onStart()

        val dialog = dialog as? BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )

        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)

            behavior.maxWidth = ViewGroup.LayoutParams.MATCH_PARENT

            behavior.skipCollapsed = true

            it.layoutParams = it.layoutParams.apply {
                height = ViewGroup.LayoutParams.MATCH_PARENT
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }

            val parent = it.parent as View
            parent.layoutParams = parent.layoutParams.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun getTheme(): Int = R.style.FullScreenBottomSheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            (view?.parent as ViewGroup).background =
                "#F8F8F6".toColorInt().toDrawable()
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.playerUIState.collect {
                        when (it) {
                            is AudioPlayerState.Paused -> {
                                binding.progressBar.visibility = View.INVISIBLE
                                binding.dialogPlayerProgressBar.progress = it.progress
                                binding.dialogPlayerProgressBar.max = it.maxProgress
                                binding.playBtnIc.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.play_dialog_ic
                                    )
                                )
                            }

                            is AudioPlayerState.Playing -> {
                                binding.dialogPlayerProgressBar.max = it.max
                                binding.progressBar.visibility = View.INVISIBLE
                                binding.dialogPlayerProgressBar.progress = it.progress
                                binding.playBtnIc.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.pause_dialog_ic
                                    )
                                )
                            }

                            is AudioPlayerState.Downloading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    settingViewModel.state.collect {
                        when (it) {
                            is TextFragmentOptionsState.Content -> {
                                binding.progressBar.visibility = View.INVISIBLE
                                actualSetting = it.settings
                                binding.tv1.textSize = actualSetting.fontSize
                                binding.tv2.textSize = actualSetting.fontSize
                                binding.tv3.textSize = actualSetting.fontSize
                            }

                            is TextFragmentOptionsState.Error -> {
                                binding.progressBar.visibility = View.INVISIBLE
                                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                            }

                            is TextFragmentOptionsState.Progress -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                        }
                    }
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
        binding.btnTextOptions.setOnClickListener {
            val dialog = ChooseFontSizeDialogFragment.newInstance(actualSetting.fontSize)
            dialog.callback = object : ChooseFontSizeCallback{
                override fun chosenFont(fontSize: Float) {
                    settingViewModel.updateSettings(
                        Settings(
                            actualSetting.language,
                            fontSize,
                            actualSetting.textFragmentTheme
                        )
                    )
                }

            }
            dialog.show(childFragmentManager, FONT_SIZE_DIALOG_TAG)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        private const val TEXTS_TO_SHOW_KEY = "textsToShowKey"

        private const val FONT_SIZE_DIALOG_TAG = "fontSizeDialog"

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