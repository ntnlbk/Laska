package laska.daily.bible.meditation.presentation.supportfragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.databinding.FragmentQrCodeDialogBinding
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

class QrCodeDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentQrCodeDialogBinding? = null
    private val binding get() = _binding!!

    private var paymentUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialog)
        paymentUrl = arguments?.getString(ARG_PAYMENT_URL) ?: "https://erip.by"
    }
    override fun onStart() {
        super.onStart()

        // Clear background tint on the parent bottom sheet frame
        dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let { sheet ->
            sheet.background = null
            sheet.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrCodeDialogBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener { dismiss() }

        // Generate and display QR Bitmap asynchronously/dynamically
        val qrBitmap = generateQrCode(paymentUrl, 512, 512)
        binding.ivQrCode.setImageBitmap(qrBitmap)

        // Native share action
        binding.btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, paymentUrl)
            }
            startActivity(Intent.createChooser(shareIntent, "Поделиться ссылкой"))
        }
    }

    private fun generateQrCode(text: String, width: Int, height: Int): Bitmap {
        val bitMatrix = MultiFormatWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            width,
            height
        )
        val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return bitmap
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "QrCodeDialogFragment"
        private const val ARG_PAYMENT_URL = "arg_payment_url"

        fun newInstance(paymentUrl: String) = QrCodeDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PAYMENT_URL, paymentUrl)
            }
        }
    }
}