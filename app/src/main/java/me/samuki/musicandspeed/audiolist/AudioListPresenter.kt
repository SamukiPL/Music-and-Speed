package me.samuki.musicandspeed.audiolist

import me.samuki.musicandspeed.BasePresenter

/**
 * Created by Samuki on 20.11.2017.
 */
interface AudioListPresenter<V> : BasePresenter<V> {
    abstract fun setAudioLists()
    abstract fun getAudioNamesList(): List<String>
    abstract fun getAudioArtistsList(): List<String>
    abstract fun getAudioPathsList(): List<String>
    abstract fun getAudioDurationsList(): List<Long>
    abstract fun askForPermission()
    abstract fun initPlayerManager()
}