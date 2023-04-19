package com.example.listeningeverywhere.ui.viewmodels.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.listeningeverywhere.R
import com.example.listeningeverywhere.adapters.SongAdapter
import com.example.listeningeverywhere.other.Status
import com.example.listeningeverywhere.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var  mainViewModel: MainViewModel


    @Inject
    lateinit var songAdapter: SongAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel= ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

    }

    private fun subscribeToObservers(view: View){
        mainViewModel.mediaItems.observe(viewLifecycleOwner){result ->
            when(result.status){
                Status.SUCCESS -> {
                    allSongsProgressBar.isVisible = false
                    result.data?.let {
                        songs ->
                        songAdapter.songs = songs
                    }
                }
                Status.ERROR -> Unit
                Status.LOADING ->  allSongsProgressBar.isVisible = true

            }
        }
    }
}