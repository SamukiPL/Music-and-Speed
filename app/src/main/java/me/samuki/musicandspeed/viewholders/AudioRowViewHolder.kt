package me.samuki.musicandspeed.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.models.AudioModel

/**
 * Created by Samuki on 11.12.2017.
 */
class AudioRowViewHolder(itemView: ViewGroup?) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.musicRow_audioTitle)
    lateinit var titleView: TextView
    @BindView(R.id.musicRow_audioArtist)
    lateinit var artistView: TextView
    @BindView(R.id.musicRow_audioDuration)
    lateinit var durationView: TextView
    @BindView(R.id.musicRow_albumCover)
    lateinit var albumCover: ImageView

    lateinit var audioModel: AudioModel

    public fun fillView() {
        titleView.text = audioModel.audioName
        artistView.text = audioModel.audioArtist
        durationView.text = changeNumberToTime(audioModel.audioDuration)
        albumCover.setImageBitmap(audioModel.audioCover)
    }

    private fun changeNumberToTime(number: Long): String {
        return "1:00"
    }
}