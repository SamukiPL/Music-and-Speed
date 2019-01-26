package me.samuki.musicandspeed.activities.main.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.row_song.view.*
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.main.adapters.SongsAdapter
import me.samuki.musicandspeed.extensions.onClick
import me.samuki.musicandspeed.models.SongModel
import java.util.concurrent.TimeUnit


class SongViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun bindView(songModel: SongModel, listener: SongsAdapter.Listener) {
        initViews(songModel)
        itemView.onClick { listener.playSong(songModel.id) }
    }

    private fun initViews(songModel: SongModel) {
        itemView.songName.apply {
            text = songModel.name
        }

        itemView.songArtist.apply {
            text = songModel.artist
        }

        itemView.songDuration.apply {
            val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(songModel.duration)
            val durationSeconds = (TimeUnit.MILLISECONDS.toSeconds(songModel.duration)
                    - TimeUnit.MILLISECONDS.toSeconds(durationMinutes))
            text = String.format("%02d:%02d", durationMinutes, durationSeconds)
        }

        Glide.with(itemView)
                .load(songModel.albumArtPath)
                .apply(RequestOptions()
                        .placeholder(R.drawable.ic_music_note_black_48dp)
                        .error(R.drawable.ic_music_note_black_48dp))
                .into(itemView.albumCover)
    }

}