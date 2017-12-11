package me.samuki.musicandspeed.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.audiolist.AudioListActivity
import me.samuki.musicandspeed.models.AudioModel
import me.samuki.musicandspeed.viewholders.AudioRowViewHolder

/**
 * Created by Samuki on 11.12.2017.
 */
class AudioListAdapter(val context: Context, val audioModelsList: List<AudioModel>) : RecyclerView.Adapter<AudioRowViewHolder>() {
    private val NORMAL_ROW: Int = 0
    private val LAST_ROW: Int = 1

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AudioRowViewHolder {
        return when (viewType) {
            NORMAL_ROW -> {
                val view: ViewGroup = LayoutInflater.from(context).inflate(R.layout.music_row, parent, false) as ViewGroup
                AudioRowViewHolder(view)
            }
            else -> {
                val view: ViewGroup = LayoutInflater.from(context).inflate(R.layout.music_row_last_element, parent, false) as ViewGroup
                AudioRowViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: AudioRowViewHolder?, position: Int) {
        holder?.audioModel = audioModelsList[position]
        holder?.fillView()
    }

    override fun getItemCount(): Int {
        return audioModelsList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position == audioModelsList.size - 1)
            return LAST_ROW
        return NORMAL_ROW
    }
}