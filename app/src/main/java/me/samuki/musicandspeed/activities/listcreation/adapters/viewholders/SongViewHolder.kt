package me.samuki.musicandspeed.activities.listcreation.adapters.viewholders

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.row_song.view.*
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.listcreation.adapters.SongsListAdapter
import me.samuki.musicandspeed.extensions.onClick
import me.samuki.musicandspeed.models.SongModel
import java.util.concurrent.TimeUnit


class SongViewHolder(itemView: View) : SongsListAdapter.ListViewHolder(itemView) {

    override fun bindView(wrappedItem: SongsListAdapter.WrappedListItem, onClickAction: ((SongsListAdapter.WrappedListItem) -> Unit)?) {
        if (wrappedItem.item is SongModel) {
            val song = wrappedItem.item
            initViews(song)
            drawableChange(if (wrappedItem.chosen) R.color.addedGreen else R.color.colorAccent)

            itemView.onClick {
                onClickAction?.invoke(wrappedItem)
            }
        }
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

    private fun drawableChange(color: Int) {
        val drawable = itemView.background.mutate() as LayerDrawable
        val colorDrawable = drawable.findDrawableByLayerId(R.id.drawableColor) as GradientDrawable
        colorDrawable.setColor(ContextCompat.getColor(itemView.context, color))

        itemView.background = drawable
    }

}