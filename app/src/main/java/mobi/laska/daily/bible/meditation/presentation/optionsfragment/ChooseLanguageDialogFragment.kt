package mobi.laska.daily.bible.meditation.presentation.optionsfragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import mobi.laska.daily.bible.meditation.R
import mobi.laska.daily.bible.meditation.databinding.FragmentChooseLanguageDialogBinding
import mobi.laska.daily.bible.meditation.domain.Language

private const val ARG_PARAM1 = "lang_chosen"
private const val ARG_PARAM3 = "y"
private const val ARG_WIDTH = "width"


class ChooseLanguageDialogFragment : DialogFragment() {
    private var _binding: FragmentChooseLanguageDialogBinding? = null
    private val binding: FragmentChooseLanguageDialogBinding
        get() = _binding ?: throw Exception("FragmentChooseLanguageDialogBinding is null")


    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.let { window ->
           // dialog?.window?.attributes?.dimAmount = 0.01f
            val params = window.attributes
            params.gravity = Gravity.TOP
            params.y = y
            window.attributes = params
        }
        dialog?.window?.setLayout(
            width,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }

    private var param1: Language = Language.BY
    private var y: Int = 0
    private var width: Int = 0

    var callback: ChooseLanguageCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as Language
            y = it.getInt(ARG_PARAM3)
            width = it.getInt(ARG_WIDTH)
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
                binding.firstOption.text =
                    ContextCompat.getString(requireActivity(), R.string.russian_language)
                binding.secondOption.text =
                    ContextCompat.getString(requireActivity(), R.string.belarusian_language)
                binding.secondOption.setOnClickListener {
                    callback?.chosenLanguage(Language.BY)
                }
                binding.firstOption.setOnClickListener {
                    callback?.chosenLanguage(Language.RU)
                }
            }

            Language.BY -> {
                binding.firstOption.text =
                    ContextCompat.getString(requireActivity(), R.string.belarusian_language)
                binding.secondOption.text =
                    ContextCompat.getString(requireActivity(), R.string.russian_language)
                binding.secondOption.setOnClickListener {
                    callback?.chosenLanguage(Language.RU)
                }
                binding.firstOption.setOnClickListener {
                    callback?.chosenLanguage(Language.BY)
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(language: Language, y: Int, width: Int) =
            ChooseLanguageDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, language)
                    putInt(ARG_PARAM3, y)
                    putInt(ARG_WIDTH, width)
                }
            }
    }

}

interface ChooseLanguageCallback {
    fun chosenLanguage(language: Language)
}