package mobi.laska.daily.bible.meditation.presentation.optionsfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import mobi.laska.daily.bible.meditation.databinding.FragmentOptionsBinding
import mobi.laska.daily.bible.meditation.domain.Language
import mobi.laska.daily.bible.meditation.domain.settings.Settings
import mobi.laska.daily.bible.meditation.domain.settings.TextFragmentTheme

@AndroidEntryPoint
class OptionsFragment : Fragment() {

    private var _binding: FragmentOptionsBinding? = null
    private val binding: FragmentOptionsBinding
        get() = _binding ?: throw Exception("FragmentOptionsBinding is null")

    private val viewModel: OptionsFragmentViewModel by viewModels()

    private var actualSettings: Settings = Settings()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOptionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect {
                        when (it) {
                            is OptionsFragmentState.Content -> {
                                actualSettings = it.settings
                                binding.progressBar.visibility = View.INVISIBLE
                                binding.languageChosenTv.text = when (it.settings.language) {
                                    Language.RU -> "Русский"
                                    Language.BY -> "Беларуская"
                                }
                                binding.colorChosenTv.text = when (it.settings.textFragmentTheme) {
                                    TextFragmentTheme.DARK -> "Тёмная"
                                    TextFragmentTheme.LIGHT -> "Светлая"
                                }
                            }

                            is OptionsFragmentState.Error -> {
                                binding.progressBar.visibility = View.INVISIBLE
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                                    .show()
                            }

                            OptionsFragmentState.Progress -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupViews() {
        binding.aboutProjectBtn.setOnClickListener {
            findNavController().navigate(OptionsFragmentDirections.actionOptionsFragmentToAboutUsFragment())
        }
        binding.closeBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.languageBtn.setOnClickListener {
            val dialog = ChooseLanguageDialogFragment.newInstance(actualSettings.language)
            dialog.callback = object : ChooseLanguageCallback {
                override fun chosenLanguage(language: Language) {
                    dialog.dismiss()
                    viewModel.updateSettings(
                        Settings(
                            language,
                            actualSettings.fontSize,
                            actualSettings.textFragmentTheme
                        )
                    )
                }
            }
            dialog.show(childFragmentManager, CHOOSE_LANG_DIALOG_TAG)
        }
        binding.readingColorBtn.setOnClickListener {
            val dialog = ChooseReadingThemeDialogFragment.newInstance(actualSettings.textFragmentTheme)
            dialog.callback = object : ChooseThemeCallback {
                override fun chosenTheme(theme: TextFragmentTheme) {
                    dialog.dismiss()
                    viewModel.updateSettings(
                        Settings(
                            actualSettings.language,
                            actualSettings.fontSize,
                            theme
                        )
                    )
                }
            }
            dialog.show(childFragmentManager, CHOOSE_THEME_DIALOG_TAG)
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val CHOOSE_LANG_DIALOG_TAG = "dialog1"
        private const val CHOOSE_THEME_DIALOG_TAG = "dialog2"
    }
}
