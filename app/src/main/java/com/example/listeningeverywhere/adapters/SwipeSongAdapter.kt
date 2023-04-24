package com.example.listeningeverywhere.adapters

import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import com.example.listeningeverywhere.R

class SwipeSongAdapter  : BaseSongAdapter(R.layout.swipe_item) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            val text = "${song.title} - ${song.subtitle}"
            findViewById<TextView>(R.id.tvPrimary).text = text

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }


}
