package me.samuki.musicandspeed.activities.main.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.main.adapters.viewholders.SongViewHolder
import me.samuki.musicandspeed.base.AutoUpdatableAdapter
import me.samuki.musicandspeed.models.SongModel
import kotlin.properties.Delegates


class SongsAdapter : RecyclerView.Adapter<SongViewHolder>(), AutoUpdatableAdapter {

    var itemList: List<SongModel> by Delegates.observable(listOf()) { _, oldValue, newValue ->
        autoNotifyList(oldValue, newValue) { old, new ->
            old.getDiff() == new.getDiff()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_song, parent, false)
        return SongViewHolder(view)
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bindView(itemList[position])
    }

}