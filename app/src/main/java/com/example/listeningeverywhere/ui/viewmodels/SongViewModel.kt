package com.example.listeningeverywhere.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listeningeverywhere.exoplayer.MusicService
import com.example.listeningeverywhere.exoplayer.MusicServiceConnection
import com.example.listeningeverywhere.exoplayer.currentPlaybackPosition
import com.example.listeningeverywhere.other.Constants.UPDATE_PLAYER_POSITION_INTERVAL
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SongViewModel @Inject constructor(musicServiceConnection: MusicServiceConnection) :
    ViewModel() {

    private val playbackState = musicServiceConnection.playbackState

    private val _curSongDuration = MutableLiveData<Long>()
    val curSongDuration: LiveData<Long> = _curSongDuration

    private val _curPlayerPosition = MutableLiveData<Long>()
    val curPlayerPosition: LiveData<Long> = _curPlayerPosition

    init {
        updateCurrentPlayerPosition()
    }

    private fun updateCurrentPlayerPosition() {
        viewModelScope.launch {
            while (true) {
                val pos = playbackState.value?.currentPlaybackPosition
                if (curPlayerPosition.value != pos) {
                    _curPlayerPosition.postValue(pos!!)
                    _curSongDuration.postValue(MusicService.curSongDuration)
                }
                delay(UPDATE_PLAYER_POSITION_INTERVAL)
            }
        }
    }
}