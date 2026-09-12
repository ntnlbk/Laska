package laska.daily.bible.meditation.presentation.supportfragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.databinding.FragmentEripPathDialogBinding

class EripPathDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentEripPathDialogBinding? = null
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
        _binding = FragmentEripPathDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener { dismiss() }

        // Copy IBAN to clipboard
        binding.btnCopyAccount.setOnClickListener {
            val ibanText = "BY94AKBB30340019293080070000"
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("ERIP Account", ibanText)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(requireContext(), "Счёт скопирован в буфер обмена", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let { sheet ->
            sheet.background = null
            sheet.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "EripPathDialogFragment"
        fun newInstance() = EripPathDialogFragment()
    }
}