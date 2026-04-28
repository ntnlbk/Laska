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
import mobi.laska.daily.bible.meditation.databinding.FragmentChooseReadingThemeDialogBinding
import mobi.laska.daily.bible.meditation.domain.settings.TextFragmentTheme

private const val ARG_PARAM1 = "theme_chosen"
private const val ARG_PARAM2 = "y"

class ChooseReadingThemeDialogFragment : DialogFragment() {
    private var _binding: FragmentChooseReadingThemeDialogBinding? = null
    private val binding: FragmentChooseReadingThemeDialogBinding
        get() = _binding ?: throw Exception("FragmentChooseReadingThemeDialogBinding is null")


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

    private var param1: TextFragmentTheme = TextFragmentTheme.LIGHT
    private var y: Int = 0
    var callback: ChooseThemeCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as TextFragmentTheme
            y = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseReadingThemeDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        when (param1) {
            TextFragmentTheme.DARK -> {
                binding.darkThemeOption.setTextColor("#A8A08D".toColorInt())
            }

            TextFragmentTheme.LIGHT -> {
                binding.lightThemeOption.setTextColor("#A8A08D".toColorInt())
            }
        }
        binding.lightThemeOption.setOnClickListener {
            callback?.chosenTheme(TextFragmentTheme.LIGHT)
        }
        binding.darkThemeOption.setOnClickListener {
            callback?.chosenTheme(TextFragmentTheme.DARK)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(theme: TextFragmentTheme, y: Int) =
            ChooseReadingThemeDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, theme)
                    putInt(ARG_PARAM2, y)
                }
            }
    }

}

interface ChooseThemeCallback {
    fun chosenTheme(theme: TextFragmentTheme)
}