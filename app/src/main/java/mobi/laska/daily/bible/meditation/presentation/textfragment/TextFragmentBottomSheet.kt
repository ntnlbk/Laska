package mobi.laska.daily.bible.meditation.presentation.textfragment

import android.app.Dialog
import android.content.res.ColorStateList
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mobi.laska.daily.bible.meditation.R
import mobi.laska.daily.bible.meditation.databinding.FragmentTextBottomSheetBinding
import mobi.laska.daily.bible.meditation.domain.settings.Settings
import mobi.laska.daily.bible.meditation.domain.settings.TextFragmentTheme
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
    private var play_button_id: Int = R.drawable.ic_play
    private var pause_button_id: Int = R.drawable.pause_ic

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
                                delay(50)
                                binding.playBtnIc.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        play_button_id
                                    )
                                )
                            }

                            is AudioPlayerState.Playing -> {
                                binding.dialogPlayerProgressBar.max = it.max
                                binding.progressBar.visibility = View.INVISIBLE
                                binding.dialogPlayerProgressBar.progress = it.progress
                                delay(50)
                                binding.playBtnIc.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        pause_button_id
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
                                when (it.settings.textFragmentTheme) {
                                    TextFragmentTheme.DARK -> {
                                        setupDarkTheme()
                                    }

                                    TextFragmentTheme.LIGHT -> {
                                        setupLightTheme()
                                    }
                                }
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

    fun setupDarkTheme() {
        binding.playBtn.clipToOutline = true
        binding.header.background = "#292423".toColorInt().toDrawable()
        binding.btnBack.backgroundTintList = ColorStateList.valueOf("#3e3a39".toColorInt())
        binding.btnBackTv.setTextColor("#FFFFFF".toColorInt())
        binding.btnBackIc.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.back_btn_light_ic
            )
        )
        binding.btnTextOptions.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.bg_circle_button_light)
        binding.btnTextOptions.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.font_size_ic_light
            )
        )
        binding.headerProgressBar.progressDrawable =
            ContextCompat.getDrawable(requireActivity(), R.drawable.custom_progress_bar_dark)
        binding.scrollView.background = "#292423".toColorInt().toDrawable()
        binding.tv1.background = "#292423".toColorInt().toDrawable()
        binding.tv1.setTextColor("#c4baa3".toColorInt())
        binding.tv2.backgroundTintList = ColorStateList.valueOf("#312b29".toColorInt())
        binding.tv2.setTextColor("#eef1f5".toColorInt())
        binding.tv3.background = "#292423".toColorInt().toDrawable()
        binding.tv3.setTextColor("#c4baa3".toColorInt())
        binding.dialogPlayerGradient.background = ContextCompat.getDrawable(
            requireActivity(),
            R.drawable.dialog_player_layout_background_dark
        )
        binding.playBtn.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_player_background_dark)
        binding.feastNameTv.setTextColor("#989898".toColorInt())
        binding.dialogPlayerProgressBar.progressDrawable = ContextCompat.getDrawable(
            requireActivity(),
            R.drawable.custom_dialog_player_progress_bar_dark
        )
        play_button_id = R.drawable.ic_play
        pause_button_id = R.drawable.pause_ic
        binding.dateTv.setTextColor("#FFFFFF".toColorInt())
    }

    fun setupLightTheme() {
        binding.playBtn.clipToOutline = true
        binding.header.background = "#F8F8F6".toColorInt().toDrawable()
        binding.btnBack.backgroundTintList = ColorStateList.valueOf("#e4e0d8".toColorInt())
        binding.btnBackTv.setTextColor("#000000".toColorInt())
        binding.btnBackIc.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.back_btn_ic
            )
        )
        binding.btnTextOptions.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.bg_circle_button)
        binding.btnTextOptions.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.font_size_ic
            )
        )
        binding.headerProgressBar.progressDrawable =
            ContextCompat.getDrawable(requireActivity(), R.drawable.custom_progress_bar)
        binding.scrollView.background = "#F8F8F6".toColorInt().toDrawable()
        binding.tv1.background = "#F8F8F6".toColorInt().toDrawable()
        binding.tv1.setTextColor("#1B1B1B".toColorInt())
        binding.tv2.backgroundTintList = ColorStateList.valueOf("#efede6".toColorInt())
        binding.tv2.setTextColor("#1B1B1B".toColorInt())
        binding.tv3.background = "#F8F8F6".toColorInt().toDrawable()
        binding.tv3.setTextColor("#1B1B1B".toColorInt())
        binding.dialogPlayerGradient.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_player_layout_background)
        binding.playBtn.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_player_background)
        binding.feastNameTv.setTextColor("#4a4945".toColorInt())
        binding.dialogPlayerProgressBar.progressDrawable = ContextCompat.getDrawable(
            requireActivity(),
            R.drawable.custom_dialog_player_progress_bar
        )
        play_button_id = R.drawable.play_dialog_ic
        pause_button_id = R.drawable.pause_dialog_ic
        binding.dateTv.setTextColor("#000000".toColorInt())
    }


    private fun setupViews() {
        binding.playBtn.clipToOutline = true
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
        binding.dateTv.text = dialogArguments.date
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
            val dialog = ChooseFontSizeDialogFragment.newInstance(
                actualSetting.fontSize,
                actualSetting.textFragmentTheme
            )
            dialog.callback = object : ChooseFontSizeCallback {
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