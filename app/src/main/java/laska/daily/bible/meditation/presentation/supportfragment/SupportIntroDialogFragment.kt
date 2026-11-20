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
import laska.daily.bible.meditation.domain.donations.popup.PopUpMode
import laska.daily.bible.meditation.presentation.homefragment.HomeFragmentDirections
import laska.daily.bible.meditation.presentation.mainfragment.MainFragmentDirections

class SupportIntroDialogFragment : DialogFragment() {

    private var _binding: DialogSupportIntroBinding? = null
    private val binding get() = _binding!!

    private var mode = PopUpMode.FIRST_TIME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialog)
        arguments?.let {
            mode = it.getSerializable(POP_UP_MODE_ARG_TAG) as PopUpMode
        }
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
                    HomeFragmentDirections.actionHomeFragmentToSupportFragment(
                        LAUNCHMODE = SupportFragmentLaunchMode.FROM_POPUP
                    )
                )
                delay(180)
                dismiss()
            }
        }

        if (mode == PopUpMode.FIRST_TIME) {
            binding.tv1Mode2.visibility = View.GONE
            binding.tv2Mode2.visibility = View.GONE
            binding.tvDescription.visibility = View.VISIBLE

        } else if (mode == PopUpMode.SECOND_TIME) {
            binding.tv1Mode2.visibility = View.VISIBLE
            binding.tv2Mode2.visibility = View.VISIBLE
            binding.tvDescription.visibility = View.GONE
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val POP_UP_MODE_ARG_TAG = "pop_up_mode_arg_tag"
        const val TAG = "SupportIntroDialogFragment"
        fun newInstance(
            popUpMode: PopUpMode
        ) = SupportIntroDialogFragment().apply {
            arguments = Bundle().apply {
                putSerializable(POP_UP_MODE_ARG_TAG, popUpMode)
            }
        }
    }
}