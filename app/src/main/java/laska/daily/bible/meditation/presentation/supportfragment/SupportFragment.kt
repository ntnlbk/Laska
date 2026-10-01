package laska.daily.bible.meditation.presentation.supportfragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.databinding.FragmentSupportBinding
import laska.daily.bible.meditation.domain.analytics.CounterType
import laska.daily.bible.meditation.domain.analytics.IncrementCounterUseCase
import laska.daily.bible.meditation.domain.donations.DonationsData
import laska.daily.bible.meditation.domain.donations.GetDonationsDataUseCase
import laska.daily.bible.meditation.domain.usecase.SupportPromptManager
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class SupportFragment : Fragment() {

    private var _binding: FragmentSupportBinding? = null
    private val binding: FragmentSupportBinding
        get() = _binding ?: throw Exception("FragmentSupportBinding is null")
    val args: SupportFragmentArgs by navArgs()

    @Inject
    lateinit var paymentPrefs: SupportPaymentPrefs

    @Inject
    lateinit var getDonationsDataUseCase: GetDonationsDataUseCase

    @Inject
    lateinit var incrementCounterUseCase: IncrementCounterUseCase

    @Inject
    lateinit var supportPromptManager: SupportPromptManager

    private lateinit var donationsData: DonationsData

    private val mode by lazy {
        args.LAUNCHMODE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() {
        viewLifecycleOwner.lifecycleScope.launch {
            donationsData = getDonationsDataUseCase()
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        val radioGroup = binding.toggleGroup

        val editText = binding.customAmountEditText
        var isProgrammaticChange = false

        fun setErrorState(hasError: Boolean) {
            editText.isActivated = hasError
        }

        fun hideKeyboard() {
            val imm =
                editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(editText.windowToken, 0)
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedValue = when (checkedId) {
                R.id.btn10 -> 10
                R.id.btn20 -> 20
                R.id.btn30 -> 30
                else -> Integer.valueOf(editText.text.toString())
            }
            if (isProgrammaticChange) return@setOnCheckedChangeListener

            if (checkedId != -1) {
                // Clear text
                isProgrammaticChange = true
                editText.text?.clear()
                isProgrammaticChange = false
                setErrorState(false)

                // Clear focus from EditText and hide keyboard
                editText.clearFocus()
                hideKeyboard()
            }
        }

        editText.setOnTouchListener { v, event ->
            radioGroup.clearCheck()
            false
        }
        editText.doAfterTextChanged { text ->
            if (isProgrammaticChange) return@doAfterTextChanged

            val input = text?.toString()?.trim() ?: ""
            if (input.isNotEmpty()) {
                // Uncheck RadioButtons safely without re-triggering loop
                if (radioGroup.checkedRadioButtonId != -1) {
                    isProgrammaticChange = true
                    radioGroup.clearCheck()
                    isProgrammaticChange = false
                }

                val value = input.toIntOrNull()
                if (value == null || value <= 0) {
                    setErrorState(true)
                } else {
                    setErrorState(false)
                }
            } else {
                setErrorState(false)
            }
        }

        // 2. Radio button change listener
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (isProgrammaticChange) return@setOnCheckedChangeListener

            if (checkedId != -1) {
                // Clear text safely when radio button is clicked
                isProgrammaticChange = true
                editText.text?.clear()
                editText.clearFocus()
                isProgrammaticChange = false
                setErrorState(false)
            }
        }
        binding.tvNotFromBelarus.setOnClickListener {
            incrementCounterUseCase(CounterType.DONATE_BELARUS_NOT)
            OutsideBySupportDialogFragment.newInstance().show(
                childFragmentManager,
                OutsideBySupportDialogFragment.TAG
            )
        }
        binding.btnSupportErip.setOnClickListener {
            val selectedSum = when (binding.toggleGroup.checkedRadioButtonId) {
                R.id.btn10 -> 10
                R.id.btn20 -> 20
                R.id.btn30 -> 30
                else -> Integer.valueOf(
                    if (binding.customAmountEditText.text.isNullOrEmpty())
                        "0"
                    else
                        binding.customAmountEditText.text.toString()

                )
            }
            if (selectedSum < 1) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.invalid_sum),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val id = UUID.randomUUID().toString()
                paymentPrefs.setPendingPayment(
                    id,
                    selectedSum
                )
                incrementCounterUseCase(CounterType.DONATE_ERIP)
                if (mode == SupportFragmentLaunchMode.FROM_POPUP) {
                    incrementCounterUseCase(CounterType.POP_UP_ERIP_CLICKED)
                }
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, donationsData.donationsUrl.toUri());
                startActivity(browserIntent);
            }
        }

        binding.btnPayQr.setOnClickListener {
            setErrorState(false)
            QrCodeDialogFragment.newInstance(donationsData.donationsUrl).show(
                childFragmentManager,
                QrCodeDialogFragment.TAG
            )
        }

        binding.btnEripPath.setOnClickListener {

            EripPathDialogFragment.newInstance(donationsData).show(
                childFragmentManager,
                EripPathDialogFragment.TAG
            )
        }
        binding.btnBecomeSponsor.setOnClickListener {
            BecomeSponsorDialogFragment.newInstance().show(
                childFragmentManager,
                BecomeSponsorDialogFragment.TAG
            )
        }

        if (mode == SupportFragmentLaunchMode.FROM_POPUP) {
            supportPromptManager.onPromptCompleted()
            binding.ivBottomDots.visibility = View.VISIBLE
            binding.tvDoItLater.visibility = View.VISIBLE
            binding.tvDoItLater.setOnClickListener {
                incrementCounterUseCase(CounterType.POP_UP_LATER_CLICKED)
                supportPromptManager.onPromptDismissedLater()
                DoItLaterSupportFragment.newInstance().show(
                    childFragmentManager,
                    OutsideBySupportDialogFragment.TAG
                )
            }
            binding.btnBack.visibility = View.GONE
            binding.btnClose.visibility = View.VISIBLE
            val params = binding.supportMainTv.layoutParams as? ViewGroup.MarginLayoutParams
            params?.let {
                val marginInDp = 47.5
                val marginInPx = (marginInDp * resources.displayMetrics.density).toInt()
                it.topMargin = marginInPx
                binding.supportMainTv.layoutParams = it
            }
            binding.btnClose.setOnClickListener {
                incrementCounterUseCase(CounterType.POP_UP_DISMISSED)
                findNavController().popBackStack()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (_binding == null) {
            _binding = FragmentSupportBinding.inflate(inflater, container, false)
        }
        val parent = binding.root.parent as? ViewGroup
        parent?.removeView(binding.root)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // Executed whenever the user comes back to the app/fragment
        if (paymentPrefs.isPaymentPending) {
            val feedbackDialog = PaymentFeedbackDialogFragment.newInstance()

            feedbackDialog.onNavigateHomeRequested = {
                findNavController().popBackStack(R.id.mainFragment, inclusive = false)
                paymentPrefs.isPaymentPending = false
            }

            feedbackDialog.onRetryRequested = {
                paymentPrefs.isPaymentPending = false
            }

            feedbackDialog.show(childFragmentManager, PaymentFeedbackDialogFragment.TAG)

        }
    }


    override fun onDestroyView() {
        incrementCounterUseCase(CounterType.DONATE_CLOSE)
        _binding = null
        super.onDestroyView()
    }
}