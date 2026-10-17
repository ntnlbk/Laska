package laska.daily.bible.meditation.presentation.homefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.databinding.FragmentHomeBinding
import laska.daily.bible.meditation.domain.courses.Author

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding ?: throw Exception("FragmentHomeBinding is null")

    private var backgroundVidePlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = FragmentHomeBinding.inflate(inflater, container, false)
        }
        val parent = binding.root.parent as? ViewGroup
        parent?.removeView(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackgroundPlayer()

        binding.btnToMain.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_mainFragment)
        }

        binding.btnToCourse.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_courseFragment)
        }


        val authorsAdapter = AuthorsAdapter({})
        val sampleAuthors = listOf(
            Author(name = "а. Яўген Голуб", imageResId = R.drawable.test_author_ic),
            Author(name = "a. Имя Фамилия", imageResId = R.drawable.default_author_icon),
            Author(name = "a. Имя Фамилия", imageResId = R.drawable.default_author_icon),
            Author(name = "a. Имя Фамилия", imageResId = R.drawable.default_author_icon)
        )
        binding.rvAuthors.adapter = authorsAdapter
        authorsAdapter.submitList(sampleAuthors)
    }
    override fun onResume() {
        super.onResume()
        backgroundVidePlayer?.playWhenReady = true
    }
    override fun onPause() {
        super.onPause()
        backgroundVidePlayer?.playWhenReady = false
    }

    override fun onDestroy() {
        backgroundVidePlayer?.release()
        backgroundVidePlayer = null
        _binding = null
        super.onDestroy()
    }

    private fun setupBackgroundPlayer() {
        val playerView = binding.playerView

        backgroundVidePlayer = ExoPlayer.Builder(requireContext()).build().apply {
            playerView.player = this

            val mediaItem = MediaItem.fromUri(
                "android.resource://${requireContext().packageName}/${R.raw.background}"
            )
            setMediaItem(mediaItem)
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0f

            playWhenReady = true

            addListener(object : Player.Listener {
                override fun onRenderedFirstFrame() {
                    startPostponedEnterTransition()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        startPostponedEnterTransition()
                        play()
                    }
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    super.onPlayerError(error)
                    seekTo(0)
                    prepare()
                    play()
                    startPostponedEnterTransition()
                }
            })

            prepare()
        }
    }

    private fun findTextureView(viewGroup: android.view.ViewGroup): android.view.TextureView? {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is android.view.TextureView) {
                return child
            } else if (child is android.view.ViewGroup) {
                val found = findTextureView(child)
                if (found != null) return found
            }
        }
        return null
    }

}