package me.samuki.musicandspeed.activities.listcreation.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.row_interval_information.view.*
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.listcreation.adapters.SummaryListAdapter
import me.samuki.musicandspeed.extensions.onClick
import me.samuki.musicandspeed.models.IntervalModel
import me.samuki.musicandspeed.models.ListModel


class IntervalViewHolder(itemView: View) : SummaryListAdapter.ListViewHolder(itemView) {

    override fun bindView(item: ListModel, listener: SummaryListAdapter.SummaryListener) {
        (item as? IntervalModel)?.let {
            itemView.intervalSpeed.apply {
                text = context.getString(R.string.kmPerHour, it.intervalSpeed)
            }

            itemView.songsCount.apply {
                val songsCount = it.songs.size
                text = resources.getQuantityString(R.plurals.songsCount, songsCount, songsCount)
            }

            itemView.goToInterval.apply {
                onClick {
                    listener.goToInterval(it)
                }
            }
        }
    }

}