package mobi.laska.daily.bible.meditation.presentation.mainfragment

import android.annotation.SuppressLint
import android.os.Bundle
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import mobi.laska.daily.bible.meditation.R
import mobi.laska.daily.bible.meditation.databinding.FragmentMainBinding
import mobi.laska.daily.bible.meditation.domain.Language
import mobi.laska.daily.bible.meditation.presentation.mainfragment.MainFragmentViewModel.Companion.TOTAL_DAYS_TO_SHOW
import mobi.laska.daily.bible.meditation.presentation.textfragment.TextFragmentBottomSheet

@UnstableApi
@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() = _binding ?: throw Exception("FragmentMainBinding is null")

    private val viewModel: MainFragmentViewModel by activityViewModels()

    private var backgroundVidePlayer: ExoPlayer? = null

    private var isSeekBarTouched = false
    private lateinit var gestureDetector: GestureDetector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        viewModel.setReading(language = Language.BY)
        setupViews()
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
                            if (diffX > 0) {
                                onSwipeRight()
                            } else {
                                onSwipeLeft()
                            }
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

    private fun onSwipeLeft() {
        try {
            viewModel.goForward()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onSwipeUp() {
        viewModel.showTextButtonClicked()
    }

    private fun onSwipeRight() {
        try {
            viewModel.goBack()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBackgroundPlayer() {
        val playerView = binding.playerView
        backgroundVidePlayer = ExoPlayer.Builder(requireContext()).build()
        playerView.player = backgroundVidePlayer
        val mediaItem = MediaItem.fromUri(
            "android.resource://${requireContext().packageName}/${R.raw.background_video}"
        )
        backgroundVidePlayer?.setMediaItem(mediaItem)
        backgroundVidePlayer?.repeatMode = Player.REPEAT_MODE_ALL
        backgroundVidePlayer?.volume = 0f
        backgroundVidePlayer?.prepare()
        backgroundVidePlayer?.play()
    }

    override fun onPause() {
        super.onPause()
        backgroundVidePlayer?.pause()
    }

    private fun setupViews() {
        setupBackgroundPlayer()
        binding.playBtn.setOnClickListener {
            viewModel.playButtonClicked()
        }

        binding.songSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                p0: SeekBar?,
                p1: Int,
                p2: Boolean,
            ) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                isSeekBarTouched = true
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                viewModel.seekTo(binding.songSeekbar.progress.toLong())
                isSeekBarTouched = false

            }
        })


        binding.plusBtn.setOnClickListener {
            viewModel.goForward15Sec()
        }

        binding.minusBtn.setOnClickListener {
            viewModel.goBack15Sec()
        }

        binding.showTextBtn.setOnClickListener {
            viewModel.showTextButtonClicked()
        }

        binding.forwardBtn.setOnClickListener {
            try {
                viewModel.goForward()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.backBtn.setOnClickListener {
            try {
                viewModel.goBack()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.dotsIndicator.totalDots = TOTAL_DAYS_TO_SHOW
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mainUIState.collect {
                when (it) {
                    is MainFragmentState.Content -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.dateTv.text = it.date
                        binding.feastNameTv.text = it.feastName
                        binding.bibleRefTv.text = it.bibleReference
                        binding.dotsIndicator.animateTo(viewModel.currentDayIndex + 2)
                    }

                    is MainFragmentState.Progress -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is MainFragmentState.Error -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    is MainFragmentState.TextShowed -> {
                        showTextFragment(
                            it.dialogArguments
                        )
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playerUIState.collect {
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
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    is AudioPlayerState.Initial -> {
                        binding.minusBtn.isEnabled = false
                        binding.plusBtn.isEnabled = false
                        binding.songSeekbar.isClickable = false
                        binding.songSeekbar.progress = 0
                        binding.actualTimeTv.text="00:00"
                        binding.playBtn.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_play)
                        )
                    }

                    is AudioPlayerState.Paused -> {
                        binding.songSeekbar.max = it.maxProgress
                        binding.songTimeTv.text = it.songTime
                        binding.actualTimeTv.text = it.currentPosition
                        if(!isSeekBarTouched)
                            binding.songSeekbar.progress = it.progress
                        binding.playBtn.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_play)
                        )
                    }

                    is AudioPlayerState.Playing -> {
                        if(!isSeekBarTouched)
                            binding.songSeekbar.progress = it.progress
                        binding.actualTimeTv.text = it.currentPosition
                        binding.playBtn.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.pause_ic)
                        )
                    }
                }
            }

        }
    }


    private fun showTextFragment(
        it: DialogArguments
    ) {
        val instance = TextFragmentBottomSheet.newInstance(
            it
        )
        instance.show(requireActivity().supportFragmentManager, TextFragmentBottomSheet.TAG)
    }



    override fun onResume() {
        backgroundVidePlayer?.play()
        super.onResume()
    }

    override fun onDestroyView() {
        backgroundVidePlayer?.release()
        _binding = null
        super.onDestroyView()
    }



}