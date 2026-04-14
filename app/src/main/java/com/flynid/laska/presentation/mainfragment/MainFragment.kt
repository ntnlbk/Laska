package com.flynid.laska.presentation.mainfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.flynid.laska.R
import com.flynid.laska.databinding.FragmentMainBinding
import com.flynid.laska.domain.Language
import com.flynid.laska.presentation.textfragment.TextFragmentBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() = _binding ?: throw Exception("FragmentMainBinding is null")

    private val viewModel: MainFragmentViewModel by viewModels()

    @Inject
    lateinit var cacheDataSourceFactory: CacheDataSource.Factory
    private var player: ExoPlayer? = null
    private var backgroundVidePlayer: ExoPlayer? = null

    override fun onStart() {
        super.onStart()
        initPlayer()
    }

    private fun initPlayer() {
        if (player == null) {
            val mediaSourceFactory = DefaultMediaSourceFactory(requireContext())
                .setDataSourceFactory(cacheDataSourceFactory)

            player = ExoPlayer.Builder(requireContext())
                .setMediaSourceFactory(mediaSourceFactory)
                .build()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            while (player != null) {
                val posSec = ((player?.currentPosition ?: 0) / 1000).toInt()
                val durSec =
                    if ((player?.duration ?: 0) < 0) 0 else ((player?.duration ?: 0) / 1000).toInt()
                binding.songTimeTv.text = formatTime(durSec)
                binding.actualTimeTv.text = formatTime(posSec)

                binding.songSeekbar.max = player?.duration?.toInt() ?: 0
                binding.songSeekbar.progress = player?.currentPosition?.toInt() ?: 0
                delay(300)

            }
        }

    }


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
    }

    private fun setupBackgroundPlayer() {
        val playerView = binding.playerView
        backgroundVidePlayer = ExoPlayer.Builder(requireContext()).build()
        playerView.player = backgroundVidePlayer
        val mediaItem = MediaItem.fromUri(
            "android.resource://${requireContext().packageName}/${R.raw.background}"
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
                player?.seekTo(binding.songSeekbar.progress.toLong())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        binding.plusBtn.setOnClickListener {
            player?.seekTo(
                (player?.currentPosition
                    ?: PLAYER_BUTTONS_CHANGE_TIME_IN_MILLS) + PLAYER_BUTTONS_CHANGE_TIME_IN_MILLS
            )
        }

        binding.minusBtn.setOnClickListener {
            val currentPosition = (player?.currentPosition ?: 0)
            player?.seekTo(
                if (currentPosition - PLAYER_BUTTONS_CHANGE_TIME_IN_MILLS < 0L) 0L
                else currentPosition - PLAYER_BUTTONS_CHANGE_TIME_IN_MILLS
            )
        }

        binding.showTextBtn.setOnClickListener {
            viewModel.showTextButtonClicked()
        }

        binding.forwardBtn.setOnClickListener {
            viewModel.goForward()
        }

        binding.backBtn.setOnClickListener {
            viewModel.goBack()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mainUIState.collect {
                when (it) {
                    is MainFragmentState.Content -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.dateTv.text = it.date
                        binding.tvSubtitle.text = it.feastName
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
                            it.textsToShow
                        )
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playerState.collect {
                when (it) {
                    is AudioPlayerState.Downloaded -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.minusBtn.isEnabled = true
                        binding.plusBtn.isEnabled = true
                        preparePlayer(it.fileUrl)
                    }

                    is AudioPlayerState.Downloading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is AudioPlayerState.Error -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    is AudioPlayerState.Initial -> {
                        binding.songTimeTv.text = "00:00"
                        binding.actualTimeTv.text = "00:00"
                        binding.minusBtn.isEnabled = false
                        binding.plusBtn.isEnabled = false
                        binding.songSeekbar.progress = 0
                        binding.playBtn.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_play)
                        )
                        playerReset()
                    }

                    is AudioPlayerState.Paused -> {
                        binding.playBtn.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_play)
                        )
                        pausePlayer()
                    }

                    is AudioPlayerState.Playing -> {
                        binding.playBtn.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.pause_ic)
                        )
                        resumePlayer()
                    }
                }
            }

        }
    }

    private fun playerReset() {
        player?.clearMediaItems()
        player?.seekTo(0)
    }

    private fun resumePlayer() {
        if (player?.isPlaying == false) {
            player?.play()
        }
    }

    private fun formatTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%02d:%02d".format(m, s)
    }

    private fun showTextFragment(
        it: TextsToShow
    ) {
        val instance = TextFragmentBottomSheet.newInstance(
            it
        )
        instance.show(requireActivity().supportFragmentManager, TextFragmentBottomSheet.TAG)
    }


    private fun preparePlayer(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player?.setMediaItem(mediaItem)
        player?.prepare()
    }

    private fun pausePlayer() {
        player?.pause()
    }


    override fun onResume() {
        backgroundVidePlayer?.play()
        super.onResume()
    }

    override fun onDestroyView() {
        backgroundVidePlayer?.release()
        player?.release()
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val PLAYER_BUTTONS_CHANGE_TIME_IN_MILLS = 15000L
    }

}