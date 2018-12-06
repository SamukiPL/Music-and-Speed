package me.samuki.musicandspeed.activities.listcreation.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.listcreation.adapters.viewholders.HeaderViewHolder
import me.samuki.musicandspeed.activities.listcreation.adapters.viewholders.SongViewHolder
import me.samuki.musicandspeed.base.AutoUpdatableAdapter
import me.samuki.musicandspeed.models.HeaderModel
import me.samuki.musicandspeed.models.ListModel
import me.samuki.musicandspeed.models.SongModel
import kotlin.properties.Delegates


class SongsListAdapter : RecyclerView.Adapter<SongsListAdapter.ListViewHolder>(), AutoUpdatableAdapter {

    data class WrappedListItem(
            val item: ListModel,
            var chosen: Boolean = false
    ) {
        fun getDiff() = "${item.getDiff()} $chosen"
    }

    var itemList: List<WrappedListItem> by Delegates.observable(listOf()) { _, oldValue, newValue ->
        autoNotifyList(oldValue, newValue) { old, new ->
            old.getDiff() == new.getDiff()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (itemList[viewType].item) {
            is SongModel -> {
                val view = inflater.inflate(R.layout.row_song, parent, false)
                SongViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.row_header, parent, false)
                HeaderViewHolder(view)
            }
        }
    }

    override fun getItemCount() = itemList.size

    override fun getItemViewType(position: Int) = itemList[position].item.viewType()

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindView(itemList[position]) {changeItemState(it)}
    }

    private fun changeItemState(wrappedItem: WrappedListItem) {
        val chosen = wrappedItem.chosen.not()
        val newList = itemList.toMutableList()
        newList.remove(wrappedItem)
        newList.add(WrappedListItem(wrappedItem.item, chosen))

        val chosenItems = newList.filter { it.chosen }
                .sortedBy { it.item.getSortValue() }.toMutableList().let {
                    manageHeader(it, R.string.chosenSongsHeader, true)
                }
        val otherItems = newList.filter { !it.chosen }
                .sortedBy { it.item.getSortValue() }.toMutableList().let {
                    manageHeader(it, R.string.otherSongsHeader, false)
                }

        newList.clear()
        newList.addAll(chosenItems)
        newList.addAll(otherItems)

        itemList = newList
    }

    private fun manageHeader(list: MutableList<WrappedListItem>, stringRes: Int, chosen: Boolean): List<WrappedListItem> {
        if (list.isNotEmpty()) {
            list.firstOrNull { it.item is HeaderModel }?.let {
                if (list.size == 1)
                    list.clear()
            } ?: list.add(0, WrappedListItem(
                    HeaderModel(stringRes), chosen)
            )
        }
        return list
    }

    fun getAllChosenItems(): List<WrappedListItem> {
        return itemList.filter { it.item is SongModel && it.chosen }
    }

    abstract class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        abstract fun bindView(wrappedItem: WrappedListItem, onClickAction: ((WrappedListItem) -> Unit)?)
    }
}