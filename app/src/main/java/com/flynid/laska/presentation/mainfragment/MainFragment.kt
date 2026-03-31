package com.flynid.laska.presentation.mainfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupViews()
    }

    private fun setupViews() {
        binding.button.setOnClickListener {
            viewModel.showReadingText("20260330", Language.BY)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mainUIState.collect {
                when (it) {
                    is MainFragmentState.Content -> {
                        showTextFragment(it.readingText)
                    }

                    is MainFragmentState.Progress -> {

                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playerState.collect {
                when (it) {
                    is AudioPlayerState.Downloading -> {
                        Log.d("TEST", "downloading")
                    }
                    is AudioPlayerState.Error -> {
                        Log.d("TEST", "error")
                    }
                    is AudioPlayerState.Paused -> {
                        Log.d("TEST", "paused")
                        binding.testPlay.text = "play"
                        binding.testPlay.setOnClickListener {
                            resumePlayer()
                        }
                    }

                    is AudioPlayerState.Playing -> {
                        Log.d("TEST", "playing")
                        binding.testPlay.text = "pause"
                        binding.testPlay.setOnClickListener {
                            pausePlayer()
                        }
                    }

                    AudioPlayerState.Initial -> {
                        binding.testPlay.text = "play"
                        Log.d("TEST", "init")
                        binding.testPlay.setOnClickListener {
                            viewModel.play("20260330", Language.BY)
                        }
                    }

                    is AudioPlayerState.Downloaded -> {
                        startPlayer(it.fileUrl)
                    }
                }
            }
        }
    }

    private fun resumePlayer() {
        player?.play()
        viewModel.updatePlayerState(true)
    }

    private fun showTextFragment(readingText: String) {
        val instance = TextFragmentBottomSheet.newInstance(readingText)
        instance.show(requireActivity().supportFragmentManager, TextFragmentBottomSheet.TAG)
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun startPlayer(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
        Log.d("TEST", player.toString())
        viewModel.updatePlayerState(true)
    }
    private fun pausePlayer(){
        player?.pause()
        viewModel.updatePlayerState(false)
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