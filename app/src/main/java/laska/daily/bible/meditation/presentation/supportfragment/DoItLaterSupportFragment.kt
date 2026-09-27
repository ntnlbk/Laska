package laska.daily.bible.meditation.presentation.supportfragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.databinding.FragmentDoitlaterDialogfragmentBinding
import laska.daily.bible.meditation.domain.analytics.IncrementCounterUseCase
import javax.inject.Inject

@AndroidEntryPoint
class DoItLaterSupportFragment : DialogFragment() {

    private var _binding: FragmentDoitlaterDialogfragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var incrementCounterUseCase: IncrementCounterUseCase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoitlaterDialogfragmentBinding.inflate(inflater, container, false)
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

        binding.btnClose.setOnClickListener { dismiss()
            findNavController().popBackStack(R.id.mainFragment, false)}
        binding.btnConfirm.setOnClickListener { dismiss()
            findNavController().popBackStack(R.id.mainFragment, false)}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "DoItLaterSupportFragment"
        fun newInstance() = DoItLaterSupportFragment()
    }
}