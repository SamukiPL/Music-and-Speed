package me.samuki.musicandspeed.activities.main.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.main.adapters.viewholders.MusicListViewHolder
import me.samuki.musicandspeed.base.AutoUpdatableAdapter
import me.samuki.musicandspeed.models.MusicListModel
import me.samuki.musicandspeed.models.SongModel
import kotlin.properties.Delegates


class ListsAdapter(
        private val listener: Listener
) : RecyclerView.Adapter<MusicListViewHolder>(), AutoUpdatableAdapter {

    var itemList: List<MusicListModel> by Delegates.observable(listOf()) { _, oldValue, newValue ->
        autoNotifyList(oldValue, newValue) { old, new ->
            old.getDiff() == new.getDiff()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_music_list, parent, false)
        return MusicListViewHolder(view)
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: MusicListViewHolder, position: Int) {
        holder.bindView(itemList[position], listener)
    }

    interface Listener {
        fun setListAsChosen(listId: Long)
    }
}