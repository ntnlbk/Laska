package laska.daily.bible.meditation.presentation.episodefragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.databinding.FragmentEpisodeBinding
import laska.daily.bible.meditation.presentation.homefragment.AuthorsAdapter
import laska.daily.bible.meditation.presentation.mainfragment.DialogArguments
import laska.daily.bible.meditation.presentation.textfragment.EpisodeTextFragmentBottomSheet

@AndroidEntryPoint
class EpisodeFragment : Fragment() {
    private var _binding: FragmentEpisodeBinding? = null

    private val binding: FragmentEpisodeBinding
        get() = _binding ?: throw Exception("FragmentEpisodeBinding is null")

    private val viewModel: EpisodeFragmentViewModel by viewModels()

    private lateinit var gestureDetector: GestureDetector
    private var isSeekBarTouched = false
    private var textBottomSheetInstance: EpisodeTextFragmentBottomSheet? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
        initGestures()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initGestures() {
        gestureDetector = GestureDetector(
            requireContext(),
            object : SimpleOnGestureListener() {

                private val SWIPE_THRESHOLD = 100
                private val SWIPE_VELOCITY_THRESHOLD = 100

                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {

                    val diffX = e2.x - (e1?.x ?: 0f)
                    val diffY = e2.y - (e1?.y ?: 0f)

                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD &&
                            Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD
                        ) {
                            return true
                        }
                    } else {
                        if (Math.abs(diffY) > SWIPE_THRESHOLD &&
                            Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD
                        ) {
                            if (diffY < 0) {
                                onSwipeUp()
                            }
                            return true
                        }
                    }


                    return false
                }
            })
        val root = binding.root

        root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }
    private fun onSwipeUp() {
        binding.showTextBtn.callOnClick()
    }


    private fun setupViews() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.plusBtn.setOnClickListener { viewModel.goForward15Sec() }
        binding.minusBtn.setOnClickListener { viewModel.goBack15Sec() }

        binding.songSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {
                isSeekBarTouched = true
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                viewModel.seekTo(binding.songSeekbar.progress.toLong())
                isSeekBarTouched = false
            }
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.episodeUIState.collect { state ->
                        when (state) {
                            is EpisodeFragmentState.Content -> {
                                binding.progressBar.visibility = View.INVISIBLE
                                binding.ivEpisodeCover.load(state.imageUrl) {
                                    crossfade(true)
                                    error(R.drawable.home_fragment_courses_section_test_img)
                                }
                                binding.dateTv.text = state.title
                                binding.bibleRefTv.text = state.reference
                                binding.feastNameTv.text = state.subtitle
                                val authorsAdapter = AuthorsAdapter({})
                                binding.rvAuthors.adapter = authorsAdapter
                                authorsAdapter.submitList(state.authors)
                                binding.ivEpisodeCover.visibility = View.VISIBLE
                                binding.songTimeTv.text = state.duration
                                binding.showTextBtn.setOnClickListener {
                                    textBottomSheetInstance = EpisodeTextFragmentBottomSheet.newInstance(
                                        DialogArguments(
                                            bibleRef = state.title,
                                            reflectionTextBody = state.text
                                        )
                                    ).apply {
                                        // Привязываем клик к ViewModel родительского фрагмента
                                        onPlayClick = {
                                            viewModel.playButtonClicked(state.audioUrl, state.title)
                                        }
                                    }
                                    textBottomSheetInstance?.show(
                                        requireActivity().supportFragmentManager,
                                        EpisodeTextFragmentBottomSheet.TAG
                                    )
                                }

                                binding.playBtn.setOnClickListener {
                                    viewModel.playButtonClicked(
                                        state.audioUrl,
                                        state.title
                                    )
                                }
                            }

                            is EpisodeFragmentState.Error -> {
                                Log.d("COURSES", state.message)
                            }

                            EpisodeFragmentState.Progress -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                launch {
                    viewModel.playerUIState.collect {
                        textBottomSheetInstance?.updatePlayerState(it)
                        when (it) {
                            is AudioPlayerState.Downloaded -> {
                                binding.progressBar.visibility = View.INVISIBLE
                                binding.minusBtn.isEnabled = true
                                binding.plusBtn.isEnabled = true
                                binding.songSeekbar.isClickable = true
                            }

                            is AudioPlayerState.Downloading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            is AudioPlayerState.Error -> {
                                binding.progressBar.visibility = View.INVISIBLE
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.check_connection_to_listen),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                            is AudioPlayerState.Initial -> {
                                binding.minusBtn.isEnabled = false
                                binding.plusBtn.isEnabled = false
                                binding.songSeekbar.isClickable = false
                                binding.songSeekbar.progress = 0
                                binding.actualTimeTv.text = getString(R.string._00_00)
                                binding.playBtn.setImageDrawable(
                                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_play)
                                )
                            }

                            is AudioPlayerState.Paused -> {
                                binding.songSeekbar.max = it.maxProgress
                                binding.songTimeTv.text = it.songTime
                                binding.actualTimeTv.text = it.currentPosition
                                if (!isSeekBarTouched)
                                    binding.songSeekbar.progress = it.progress
                                binding.playBtn.setImageDrawable(
                                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_play)
                                )
                            }

                            is AudioPlayerState.Playing -> {
                                if (!isSeekBarTouched) {
                                    binding.songSeekbar.progress = it.progress
                                    binding.songSeekbar.max = it.max
                                }
                                binding.songTimeTv.text = it.songTime
                                binding.actualTimeTv.text = it.currentPosition
                                binding.playBtn.setImageDrawable(
                                    ContextCompat.getDrawable(requireContext(), R.drawable.pause_ic)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}