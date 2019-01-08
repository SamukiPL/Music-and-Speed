package me.samuki.musicandspeed.activities.listcreation.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.row_new_interval.view.*
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.listcreation.adapters.SummaryListAdapter
import me.samuki.musicandspeed.models.ListModel


class NewIntervalViewHolder(itemView: View) : SummaryListAdapter.ListViewHolder(itemView) {

    override fun bindView(item: ListModel, listener: SummaryListAdapter.SummaryListener) {
        itemView.newIntervalText.text = itemView.context.getString(R.string.addNewInterval)
        itemView.setOnClickListener {
            listener.addNewInterval()
        }
    }
}