package mobi.laska.daily.bible.meditation.presentation.optionsfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.fragment.app.DialogFragment
import mobi.laska.daily.bible.meditation.databinding.FragmentChooseFontSizeDialogBinding
import mobi.laska.daily.bible.meditation.domain.settings.FontSize

private const val ARG_PARAM1 = "font_chosen"

class ChooseFontSizeDialogFragment : DialogFragment() {
    private var _binding: FragmentChooseFontSizeDialogBinding? = null
    private val binding: FragmentChooseFontSizeDialogBinding
        get() = _binding ?: throw Exception("FragmentChooseFontSizeDialogBinding is null")


    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private var param1: FontSize = FontSize.NORMAL

    var callback: ChooseFontSizeCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as FontSize
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
        when (param1) {
            FontSize.SMALL -> {
                binding.smallOption.setTextColor("#A8A08D".toColorInt())
            }
            FontSize.NORMAL -> {
                binding.mediumOption.setTextColor("#A8A08D".toColorInt())
            }
            FontSize.LARGE -> {
                binding.largeOption.setTextColor("#A8A08D".toColorInt())
            }
        }
        binding.smallOption.setOnClickListener {
            callback?.chosenFont(FontSize.SMALL)
        }
        binding.mediumOption.setOnClickListener {
            callback?.chosenFont(FontSize.NORMAL)
        }
        binding.largeOption.setOnClickListener {
            callback?.chosenFont(FontSize.LARGE)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(fontSize: FontSize) =
            ChooseFontSizeDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, fontSize)
                }
            }
    }

}
interface ChooseFontSizeCallback {
    fun chosenFont(fontSize: FontSize)
}