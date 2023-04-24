package com.example.listeningeverywhere.ui.viewmodels.fragments

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listeningeverywhere.R
import com.example.listeningeverywhere.adapters.SwipeSongAdapter
import com.example.listeningeverywhere.other.Status
import com.example.listeningeverywhere.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var songAdapter: SwipeSongAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()

        songAdapter.setItemClickListener {
            mainViewModel.playOrToggleSong(it)
        }
    }

    private fun setupRecyclerView() =
        requireView().findViewById<RecyclerView>(R.id.rvAllSongs).apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    requireView().findViewById<ProgressBar>(R.id.allSongsProgressBar).isVisible =
                        false
                    result.data?.let { songs ->
                        songAdapter.songs = songs
                    }
                }
                Status.ERROR -> Unit
                Status.LOADING -> requireView().findViewById<ProgressBar>(R.id.allSongsProgressBar).isVisible =
                    true

            }
        }
    }
}