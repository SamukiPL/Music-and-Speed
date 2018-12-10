package me.samuki.musicandspeed.activities.listcreation.adapters

import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.listcreation.adapters.viewholders.IntervalViewHolder
import me.samuki.musicandspeed.activities.listcreation.adapters.viewholders.NewIntervalViewHolder
import me.samuki.musicandspeed.base.AutoUpdatableAdapter
import me.samuki.musicandspeed.extensions.inflate
import me.samuki.musicandspeed.models.AddIntervalModel
import me.samuki.musicandspeed.models.INTERVAL_VIEW
import me.samuki.musicandspeed.models.IntervalModel
import me.samuki.musicandspeed.models.ListModel
import kotlin.properties.Delegates


class SummaryListAdapter(
        private val listener: SummaryListener
) : RecyclerView.Adapter<SummaryListAdapter.ListViewHolder>(), AutoUpdatableAdapter {

    private var itemList: List<ListModel> by Delegates.observable(listOf()) { _, oldValue, newValue ->
        autoNotifyList(oldValue, newValue) { old, new ->
            old.getDiff() == new.getDiff()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return when (viewType) {
            INTERVAL_VIEW -> {
                val view = parent.inflate(R.layout.row_interval_information, false)
                IntervalViewHolder(view)
            }
            else -> {
                val view = parent.inflate(R.layout.row_new_interval, false)
                NewIntervalViewHolder(view)
            }
        }
    }

    override fun getItemCount() = itemList.size

    override fun getItemViewType(position: Int) = itemList[position].getViewType()

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindView(itemList[position], listener)
    }

    fun setItems(newList: MutableList<ListModel>) {
        newList.add(AddIntervalModel())
        itemList = newList
    }

    abstract class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        abstract fun bindView(item: ListModel, listener: SummaryListener)
    }

    interface SummaryListener {
        fun addNewInterval()
        fun goToInterval(interval: IntervalModel)
    }
}