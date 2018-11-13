package me.samuki.musicandspeed.activities.main.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.music_row.view.*
import me.samuki.musicandspeed.models.SongModel
import java.util.concurrent.TimeUnit


class SongViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun bindView(songModel: SongModel) {
        initViews(songModel)
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
    }

}