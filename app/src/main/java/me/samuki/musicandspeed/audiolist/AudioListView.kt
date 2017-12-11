package me.samuki.musicandspeed.audiolist

import android.content.Context
import me.samuki.musicandspeed.BaseView

/**
 * Created by Samuki on 20.11.2017.
 */
interface AudioListView : BaseView {
    fun showProgressBar()
    fun getContext(): Context
}