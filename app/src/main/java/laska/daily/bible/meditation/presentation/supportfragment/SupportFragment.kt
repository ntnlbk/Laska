package laska.daily.bible.meditation.presentation.supportfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.databinding.FragmentSupportBinding

class SupportFragment : Fragment() {

    private var _binding: FragmentSupportBinding? = null
    private val binding: FragmentSupportBinding
        get() = _binding ?: throw Exception("FragmentSupportBinding is null")
    val args: SupportFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mode = args.LAUNCHMODE
        setupViews()
    }

    private fun setupViews() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.closeBtn.setOnClickListener {
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
                R.id.btn10 -> "10 BYN"
                R.id.btn20 -> "20 BYN"
                R.id.btn30 -> "30 BYN"
                else -> ""
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
            // TODO("log selected value")
        }
        fun getValidCustomAmount(): Int? {
            val input = editText.text?.toString()?.trim()
            val value = input?.toIntOrNull()
            return if (value != null && value > 0) value else null
        }

        // 1. Text change listener
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}