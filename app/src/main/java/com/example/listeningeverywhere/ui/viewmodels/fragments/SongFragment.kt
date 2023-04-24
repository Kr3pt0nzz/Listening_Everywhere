package com.example.listeningeverywhere.ui.viewmodels.fragments


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.example.listeningeverywhere.R
import com.example.listeningeverywhere.data.entities.Song
import com.example.listeningeverywhere.exoplayer.toSong
import com.example.listeningeverywhere.other.Status
import com.example.listeningeverywhere.ui.viewmodels.MainViewModel
import com.example.listeningeverywhere.ui.viewmodels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var mainViewModel: MainViewModel
    private val songViewModel: SongViewModel by viewModels()

    private var curPlayingSong: Song? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        subscribeToObservers()
    }

    private fun updateTitleAndSongImage(song: Song){
        val title = "${song.title} - ${song.subtitle}"
       requireView().findViewById<TextView>(R.id.tvSongName).text = title
        glide.load(song.imageUrl).into(requireView().findViewById(R.id.ivCurSongImage))
    }


    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) {
            it?.let { result ->
                when(result.status) {
                    Status.SUCCESS -> {
                        result.data?.let { songs ->
                            if(curPlayingSong == null && songs.isNotEmpty()) {
                                curPlayingSong = songs[0]
                                updateTitleAndSongImage(songs[0])
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
        mainViewModel.curPlayingSong.observe(viewLifecycleOwner) {
            if(it == null) return@observe
            curPlayingSong = it.toSong()
            updateTitleAndSongImage(curPlayingSong!!)
        }
    }
}