package mobi.laska.daily.bible.meditation.presentation.optionsfragment

import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.graphics.toColorInt
import androidx.fragment.app.DialogFragment
import mobi.laska.daily.bible.meditation.databinding.FragmentChooseLanguageDialogBinding
import mobi.laska.daily.bible.meditation.domain.Language

private const val ARG_PARAM1 = "lang_chosen"
private const val ARG_PARAM3 = "y"

class ChooseLanguageDialogFragment : DialogFragment() {
    private var _binding: FragmentChooseLanguageDialogBinding? = null
    private val binding: FragmentChooseLanguageDialogBinding
        get() = _binding ?: throw Exception("FragmentChooseLanguageDialogBinding is null")


    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.let { window ->
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            val params = window.attributes
            params.gravity = Gravity.TOP
            params.y = y
            window.attributes = params
        }
    }

    private var param1: Language = Language.BY
    private var y: Int = 0

    var callback: ChooseLanguageCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as Language
            y = it.getInt(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseLanguageDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        when (param1) {
            Language.RU -> {
                binding.russianLangOption.setTextColor("#A8A08D".toColorInt())
            }

            Language.BY -> {
                binding.belarusianLangOption.setTextColor("#A8A08D".toColorInt())
            }
        }
        binding.russianLangOption.setOnClickListener {
            callback?.chosenLanguage(Language.RU)
        }
        binding.belarusianLangOption.setOnClickListener {
            callback?.chosenLanguage(Language.BY)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(language: Language, y: Int) =
            ChooseLanguageDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, language)
                    putInt(ARG_PARAM3, y)
                }
            }
    }

}
interface ChooseLanguageCallback {
    fun chosenLanguage(language: Language)
}