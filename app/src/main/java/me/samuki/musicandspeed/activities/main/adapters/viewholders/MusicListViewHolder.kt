package me.samuki.musicandspeed.activities.main.adapters.viewholders

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.row_music_list.view.*
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.main.adapters.ListsAdapter
import me.samuki.musicandspeed.extensions.onClick
import me.samuki.musicandspeed.models.MusicListModel


class MusicListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindView(musicListModel: MusicListModel, listener: ListsAdapter.Listener) {
        itemView.onClick { listener.setListAsChosen(musicListModel.id) }
        itemView.listName.text = musicListModel.name
        drawableChange(if (musicListModel.chosen) R.color.addedGreen else R.color.colorAccent)
    }

    private fun drawableChange(color: Int) {
        val drawable = itemView.background.mutate() as LayerDrawable
        val colorDrawable = drawable.findDrawableByLayerId(R.id.drawableColor) as GradientDrawable
        colorDrawable.setColor(ContextCompat.getColor(itemView.context, color))

        itemView.background = drawable
    }

}