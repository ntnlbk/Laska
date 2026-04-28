package mobi.laska.daily.bible.meditation.presentation.textfragment

import android.R
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import mobi.laska.daily.bible.meditation.databinding.FragmentChooseFontSizeDialogBinding
import mobi.laska.daily.bible.meditation.domain.settings.DEFAULT_TEXT_SIZE

private const val ARG_PARAM1 = "font_chosen"

class ChooseFontSizeDialogFragment : DialogFragment() {
    private var _binding: FragmentChooseFontSizeDialogBinding? = null
    private val binding: FragmentChooseFontSizeDialogBinding
        get() = _binding ?: throw Exception("FragmentChooseFontSizeDialogBinding is null")


    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog?.window?.let { window ->

            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

            val params = window.attributes
            params.gravity = Gravity.TOP
            params.y = 200
            window.attributes = params
        }
    }

    private var param1: Float = DEFAULT_TEXT_SIZE

    var callback: ChooseFontSizeCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getFloat(ARG_PARAM1)
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
        setupViews()
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
        fun newInstance(fontSize: Float) =
            ChooseFontSizeDialogFragment().apply {
                arguments = Bundle().apply {
                    putFloat(ARG_PARAM1, fontSize)
                }
            }
    }

}

interface ChooseFontSizeCallback {
    fun chosenFont(fontSize: Float)
}