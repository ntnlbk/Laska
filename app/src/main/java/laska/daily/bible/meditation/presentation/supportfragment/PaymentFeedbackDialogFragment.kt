package laska.daily.bible.meditation.presentation.supportfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.databinding.DialogPaymentFeedbackBinding

class PaymentFeedbackDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogPaymentFeedbackBinding? = null
    private val binding get() = _binding!!

    var onNavigateHomeRequested: (() -> Unit)? = null
    var onRetryRequested: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPaymentFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Step 1 -> "Так, атрымалася" -> Advance ViewFlipper to Thank You view
        binding.btnSuccess.setOnClickListener {
            binding.viewFlipper.showNext()
        }

        // Step 1 -> "Не, паспрабаваць яшчэ раз" -> Dismiss and handle retry
        binding.btnRetry.setOnClickListener {
            dismiss()
            onRetryRequested?.invoke()
        }

        // Step 2 -> "На галоўную" -> Dismiss and navigate home
        binding.btnToMain.setOnClickListener {
            dismiss()
            onNavigateHomeRequested?.invoke()
        }
    }

    override fun onStart() {
        super.onStart()

        // Explicitly specify  on findViewById
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)

        if (bottomSheet != null) {
            bottomSheet.background = null
            bottomSheet.setBackgroundColor(android.graphics.Color.TRANSPARENT)

            val layoutParams = bottomSheet.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            bottomSheet.layoutParams = layoutParams

            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.isFitToContents = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PaymentFeedbackDialogFragment"
        fun newInstance() = PaymentFeedbackDialogFragment()
    }
}