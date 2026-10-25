package laska.daily.bible.meditation.presentation.homefragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.databinding.FragmentHomeBinding

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding: FragmentHomeBinding
        get() = _binding ?: throw Exception("FragmentHomeBinding is null")

    private val viewModel: HomeFragmentViewModel by viewModels()
    private var scrollAnimator: ValueAnimator? = null

    private var backgroundVidePlayer: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackgroundPlayer()
        setupOnClickListeners()
        observeViewModel()

    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.homeUIState.collect { state ->
                        when (state) {
                            is HomeFragmentState.Content -> {
                                Log.d("COURSES", "CONTENT")
                                binding.dateTv.text = state.date
                                binding.bibleRefTv.text = state.bibleReference
                                binding.tvFeaturedSection.text = state.featuredSectionTitle
                                binding.tvEpisodesCount.text =
                                    state.featuredCourseSessionsNumber.toString()
                                binding.tvFeaturedCourseTitle.text = state.featuredCourseTitle
                                binding.tvFeaturedCourseDescription.text =
                                    state.featuredCourseDescription
                                binding.ivFeaturedCourseLogo.load(
                                    state.featuredCourseImageURL
                                ) {
                                    crossfade(true)
                                    placeholder(R.drawable.home_fragment_courses_section_test_img)
                                    error(R.drawable.home_fragment_courses_section_test_img)
                                }
                                val authorsAdapter = AuthorsAdapter {}
                                binding.rvAuthors.adapter = authorsAdapter
                                authorsAdapter.submitList(state.featuredCourseAuthors)
                                binding.btnToCourse.setOnClickListener {
                                    val action =
                                        HomeFragmentDirections.actionHomeFragmentToCourseFragment(
                                            state.featuredCourseId
                                        )
                                    findNavController().navigate(action)
                                }
                            }
                            is HomeFragmentState.Error -> Log.d("COURSES", "ERROR")
                            HomeFragmentState.Progress -> Log.d("COURSES", "PROGRESS")
                        }
                    }
                }
            }
        }
    }

    private fun setupOnClickListeners() {
        binding.btnToMain.setOnClickListener {
            backgroundVidePlayer?.playWhenReady = false

            scrollToTopSmoothly {
                if (_binding != null &&
                    viewLifecycleOwner.lifecycle.currentState
                        .isAtLeast(Lifecycle.State.CREATED)
                ) {
                    findNavController().navigate(
                        R.id.action_homeFragment_to_mainFragment
                    )
                }
            }
        }


    }

    private fun scrollToTopSmoothly(onFinished: () -> Unit) {
        val scrollView = binding.homeScrollView

        scrollAnimator?.cancel()

        val animator = ValueAnimator.ofInt(scrollView.scrollY, 0).apply {
            duration = 200L
            interpolator = LinearInterpolator()

            addUpdateListener {
                scrollView.scrollTo(0, it.animatedValue as Int)
            }

            addListener(object : AnimatorListenerAdapter() {
                private var cancelled = false

                override fun onAnimationCancel(animation: Animator) {
                    cancelled = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!cancelled && _binding != null) {
                        onFinished()
                    }
                }
            })
        }

        scrollAnimator = animator
        animator.start()
    }

    override fun onResume() {
        super.onResume()
        backgroundVidePlayer?.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        backgroundVidePlayer?.playWhenReady = false
        binding.homeScrollView.scrollTo(0, 0)
    }

    override fun onDestroyView() {
        scrollAnimator?.cancel()
        scrollAnimator = null

        releaseBackgroundPlayer()

        _binding = null
        super.onDestroyView()
    }

    private fun setupBackgroundPlayer() {
        val playerView = binding.playerView
        playerView.alpha = 0f
        val btnToMain = binding.btnToMain

        btnToMain.post {
            val parentHeight = btnToMain.rootView.height

            val targetHeight = parentHeight -
                    (300 * resources.displayMetrics.density).toInt()

            btnToMain.layoutParams = btnToMain.layoutParams.apply {
                height = targetHeight
            }
        }


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
                    _binding?.playerView?.animate()
                        ?.alpha(1f)
                        ?.setDuration(150L)
                        ?.start()

                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        startPostponedEnterTransition()
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

    private fun releaseBackgroundPlayer() {
        backgroundVidePlayer?.let { player ->
            binding.playerView.player = null
            player.stop()
            player.release()
        }

        backgroundVidePlayer = null
    }

}