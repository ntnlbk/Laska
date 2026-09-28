package laska.daily.bible.meditation.presentation.supportfragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.databinding.DialogSupportIntroBinding
import laska.daily.bible.meditation.presentation.mainfragment.MainFragmentDirections

class SupportIntroDialogFragment : DialogFragment() {

    private var _binding: DialogSupportIntroBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSupportIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToSupportFragment(
                        LAUNCHMODE = SupportFragmentLaunchMode.FROM_POPUP
                    )
                )
                delay(180)
                dismiss()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // 1. Force the dialog window to match parent screen bounds
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            // 2. Make system dialog background transparent
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // 3. Remove the dark dim overlay behind/above the window (including status bar)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SupportIntroDialogFragment"
        fun newInstance() = SupportIntroDialogFragment()
    }
}