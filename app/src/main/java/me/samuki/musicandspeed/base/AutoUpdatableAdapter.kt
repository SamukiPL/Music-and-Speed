package me.samuki.musicandspeed.base

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView


interface AutoUpdatableAdapter {

    fun <T> RecyclerView.Adapter<*>.autoNotifyList(old: List<T>, new: List<T>, compare: (T, T) -> Boolean) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return old[oldItemPosition] == new[newItemPosition]
            }

            override fun getOldListSize(): Int {
                return old.size
            }

            override fun getNewListSize(): Int {
                return new.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return compare(old[oldItemPosition], new[newItemPosition])
            }
        })
    }

}