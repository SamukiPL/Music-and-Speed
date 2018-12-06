package me.samuki.musicandspeed.activities.listcreation.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.row_header.view.*
import me.samuki.musicandspeed.activities.listcreation.adapters.SongsListAdapter
import me.samuki.musicandspeed.models.HeaderModel


class HeaderViewHolder(itemView: View) : SongsListAdapter.ListViewHolder(itemView) {

    override fun bindView(wrappedItem: SongsListAdapter.WrappedListItem, onClickAction: ((SongsListAdapter.WrappedListItem) -> Unit)?) {
        if (wrappedItem.item is HeaderModel) {
            itemView.headerText.setText(wrappedItem.item.headerTextRes)
        }
    }

}