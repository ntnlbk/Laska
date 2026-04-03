package com.flynid.laska.presentation.mainfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
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
        chooseReading("20260401", Language.BY)
        setupViews()
    }

    private fun chooseReading(date: String, lang: Language) {
        viewModel.setReading(date, lang)
    }

    private fun setupViews() {
        binding.playBtn.setOnClickListener {
            viewModel.playButtonClicked()
        }

        binding.songSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                p0: SeekBar?,
                p1: Int,
                p2: Boolean,
            ) {
                //player?.seekTo(binding.songSeekbar.progress.toLong())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //player?.seekTo(binding.songSeekbar.progress.toLong())
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                player?.seekTo(binding.songSeekbar.progress.toLong())
            }
        })

        binding.plusBtn.setOnClickListener {
            player?.seekTo((player?.currentPosition ?: 5000) + 5000)
        }

        binding.minusBtn.setOnClickListener {
            player?.seekTo((player?.currentPosition ?: 5000) - 5000)
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
                        binding.dateTv.text = it.date
                        binding.feastNameTv.text = it.bibleReference
                        binding.tvSubtitle.text = it.feastName
                    }

                    is MainFragmentState.Progress -> {}

                    is MainFragmentState.Error -> {
                        Log.d("MY_TEST", it.message)
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
                        Log.d("MY_TEST", "PLAYER DOWNLOADED")
                        preparePlayer(it.fileUrl)
                    }

                    is AudioPlayerState.Downloading -> {
                        Log.d("MY_TEST", "PLAYER DOWNLOADING")
                    }

                    is AudioPlayerState.Error -> {
                        Log.d("MY_TEST", "PLAYER ERROR")
                    }

                    is AudioPlayerState.Initial -> {
                        Log.d("MY_TEST", "PLAYER INITIAL")
                        playerReset()
                    }

                    is AudioPlayerState.Paused -> {
                        Log.d("MY_TEST", "PLAYER PAUSED")
                        //binding.playBtn.text = "Play"
                        pausePlayer()
                    }

                    is AudioPlayerState.Playing -> {
                        Log.d("MY_TEST", "PLAYER PLAYING")
                        //binding.playBtn.text = "Pause"
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


    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}