package laska.daily.bible.meditation.presentation.supportfragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.databinding.FragmentOutsideBySupportDialogfragmentBinding

// 1. Extend standard DialogFragment, NOT BottomSheetDialogFragment
class OutsideBySupportDialogFragment : DialogFragment() {

    private var _binding: FragmentOutsideBySupportDialogfragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOutsideBySupportDialogfragmentBinding.inflate(inflater, container, false)
        return binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener { dismiss() }

        binding.rgRegion.setOnCheckedChangeListener { _, checkedId ->
            binding.btnConfirm.isEnabled = checkedId != -1
        }

        binding.btnConfirm.setOnClickListener {
            val selectedRegion = when (binding.rgRegion.checkedRadioButtonId) {
                R.id.rbEc -> "EC"
                R.id.rbRussia -> "Russia"
                R.id.rbOther -> "Other"
                else -> null
            }

            if (selectedRegion != null) {
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "OutsideBySupportDialogFragment"
        fun newInstance() = OutsideBySupportDialogFragment()
    }
}