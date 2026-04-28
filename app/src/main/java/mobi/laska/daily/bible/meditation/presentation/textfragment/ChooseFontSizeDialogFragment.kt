package mobi.laska.daily.bible.meditation.presentation.textfragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.DialogFragment
import mobi.laska.daily.bible.meditation.R
import mobi.laska.daily.bible.meditation.databinding.FragmentChooseFontSizeDialogBinding
import mobi.laska.daily.bible.meditation.domain.settings.DEFAULT_TEXT_SIZE
import mobi.laska.daily.bible.meditation.domain.settings.TextFragmentTheme

private const val ARG_PARAM1 = "font_chosen"
private const val ARG_PARAM2 = "theme"

class ChooseFontSizeDialogFragment : DialogFragment() {
    private var _binding: FragmentChooseFontSizeDialogBinding? = null
    private val binding: FragmentChooseFontSizeDialogBinding
        get() = _binding ?: throw Exception("FragmentChooseFontSizeDialogBinding is null")


    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.let { window ->

            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

            val params = window.attributes
            params.gravity = Gravity.TOP
            params.y = 200
            window.attributes = params
        }
    }

    private var param1: Float = DEFAULT_TEXT_SIZE
    private var theme: TextFragmentTheme = TextFragmentTheme.LIGHT

    var callback: ChooseFontSizeCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getFloat(ARG_PARAM1)
            theme = it.getSerializable(ARG_PARAM2) as TextFragmentTheme
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseFontSizeDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTheme()
        setupViews()
    }

    private fun setupTheme() {
        when (theme){
            TextFragmentTheme.DARK -> setupDarkTheme()
            TextFragmentTheme.LIGHT -> setupLightTheme()
        }
    }

    private fun setupDarkTheme() {
        binding.mainLayout.background = ContextCompat.getDrawable(requireActivity(), R.drawable.font_size_dialog_background_dark)
        binding.smallLetter.setTextColor("#FFFFFF".toColorInt())
        binding.largeLetter.setTextColor("#FFFFFF".toColorInt())
    }

    private fun setupLightTheme() {
        binding.mainLayout.background = ContextCompat.getDrawable(requireActivity(), R.drawable.menu_options_background_light)
        binding.smallLetter.setTextColor("#000000".toColorInt())
        binding.largeLetter.setTextColor("#000000".toColorInt())
    }

    private fun setupViews() {
        binding.seekBar.progress = param1.toInt()
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                p0: SeekBar?,
                p1: Int,
                p2: Boolean
            ) {
                callback?.chosenFont(binding.seekBar.progress.toFloat())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(fontSize: Float, theme: TextFragmentTheme) =
            ChooseFontSizeDialogFragment().apply {
                arguments = Bundle().apply {
                    putFloat(ARG_PARAM1, fontSize)
                    putSerializable(ARG_PARAM2, theme)
                }
            }
    }

}

interface ChooseFontSizeCallback {
    fun chosenFont(fontSize: Float)
}