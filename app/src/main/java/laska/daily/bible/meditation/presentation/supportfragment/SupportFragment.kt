package laska.daily.bible.meditation.presentation.supportfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedValue = when (checkedId) {
                R.id.btn10 -> "10 BYN"
                R.id.btn20 -> "20 BYN"
                R.id.btn30 -> "30 BYN"
                else -> ""
            }
            // TODO("log selected value")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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