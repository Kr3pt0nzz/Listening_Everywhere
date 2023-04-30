package com.example.listeningeverywhere.data.remote

import com.example.listeningeverywhere.data.entities.Song
import com.example.listeningeverywhere.other.Constants.SONG_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MusicDatabase {
    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONG_COLLECTION)

    suspend fun getAllSongs(): List<Song> {
        return try {
            val res = songCollection.get().await().toObjects(Song::class.java)
            res
        } catch(e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}