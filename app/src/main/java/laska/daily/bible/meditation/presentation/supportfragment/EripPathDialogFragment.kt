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
import laska.daily.bible.meditation.domain.donations.DonationsData

class EripPathDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentEripPathDialogBinding? = null
    private val binding get() = _binding!!

    private val donationsData by lazy {
        requireArguments().getParcelable<DonationsData>(DONATION_ARG)
            ?: throw IllegalArgumentException("Argument DonationsData is required")
    }

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

        binding.btnCopyAccount.setOnClickListener {
            val ibanText = donationsData.eripAccount
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("ERIP Account", ibanText)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(requireContext(),
                getString(R.string.erip_copied_text), Toast.LENGTH_SHORT).show()
            dismiss()
        }
        binding.path1.text = "•  " + donationsData.donationsEripPath[0]
        binding.path2.text = "•  " + donationsData.donationsEripPath[1]
        binding.path3.text = "•  " + donationsData.donationsEripPath[2]
        binding.path4.text = "•  " + donationsData.donationsEripPath[3]
        binding.path5.text = "•  " + donationsData.donationsEripPath[4]
        binding.path6.text = "•  " + donationsData.donationsEripPath[5]
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
        private const val DONATION_ARG = "donation data"
        fun newInstance(donationsData: DonationsData): EripPathDialogFragment{
            return EripPathDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(DONATION_ARG, donationsData)
                }
            }
        }
    }
}