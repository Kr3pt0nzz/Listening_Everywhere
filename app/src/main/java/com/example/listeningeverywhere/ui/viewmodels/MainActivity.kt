package com.example.listeningeverywhere.ui.viewmodels

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.example.listeningeverywhere.R
import com.example.listeningeverywhere.adapters.SwipeSongAdapter
import com.example.listeningeverywhere.data.entities.Song
import com.example.listeningeverywhere.exoplayer.isPlaying
import com.example.listeningeverywhere.exoplayer.toSong
import com.example.listeningeverywhere.other.Status.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager

    private var curPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        subscribeToObservers()

        findViewById<ViewPager2>(R.id.vpSong).adapter = swipeSongAdapter

        findViewById<ViewPager2>(R.id.vpSong).registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(playbackState?.isPlaying == true){
                    mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])

                } else{
                    curPlayingSong = swipeSongAdapter.songs[position]
                }
            }
        })


        findViewById<ImageView>(R.id.ivPlayPause).setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        swipeSongAdapter.setItemClickListener {
            findViewById<FragmentContainerView>(R.id.navHostFragment).findNavController().navigate(
                R.id.globalActionToSongFragment
            )
        }

        findViewById<FragmentContainerView>(R.id.navHostFragment).findNavController().addOnDestinationChangedListener{_, destination, _  ->
           when(destination.id) {
               R.id.songFragment -> hideBottomBar()
               R.id.homeFragment -> showBottomBar()
               else -> showBottomBar()
           }
        }
    }

    private fun hideBottomBar(){
        findViewById<ImageView>(R.id.ivCurSongImage).isVisible = false
        findViewById<ViewPager2>(R.id.vpSong).isVisible = false
        findViewById<ImageView>(R.id.ivPlayPause).isVisible = false
    }
    private fun showBottomBar(){
        findViewById<ImageView>(R.id.ivCurSongImage).isVisible = true
        findViewById<ViewPager2>(R.id.vpSong).isVisible = true
        findViewById<ImageView>(R.id.ivPlayPause).isVisible = true
    }

    private fun switchViewPagerToCurrentSong(song: Song) {
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if (newItemIndex != -1) {
            findViewById<ViewPager2>(R.id.vpSong).currentItem = newItemIndex
            curPlayingSong = song
        }
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(this) {
            it?.let { result ->
                when (result.status) {
                    SUCCESS -> {
                        result.data?.let { songs ->
                            swipeSongAdapter.songs = songs
                            if (songs.isNotEmpty()) {
                                glide.load((curPlayingSong ?: songs[0]).imageUrl)
                                    .into(findViewById(R.id.ivCurSongImage))
                            }
                            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
                        }
                    }
                    ERROR -> Unit
                    LOADING -> Unit
                }
            }
        }
        mainViewModel.curPlayingSong.observe(this) {
            if (it == null) return@observe

            curPlayingSong = it.toSong()
            glide.load(curPlayingSong?.imageUrl)
                .into(findViewById(R.id.ivCurSongImage) ?: return@observe)
        }

        mainViewModel.playbackState.observe(this) {
            playbackState = it
            findViewById<ImageView>(R.id.ivPlayPause).setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
        }

        mainViewModel.networkError.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    ERROR -> Snackbar.make(
                        findViewById(R.id.rootLayout),
                        result.message ?: "An unknown error occured",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }

            }
        }

    }
}