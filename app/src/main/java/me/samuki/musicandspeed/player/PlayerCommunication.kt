package me.samuki.musicandspeed.player

import me.samuki.musicandspeed.models.AudioModel

/**
 * Created by Samuki on 11.12.2017.
 */
interface PlayerCommunication {
    fun addAudioModels(audioModelList: List<AudioModel>)
    fun changeVolumeUp()
    fun changeVolumeDown()
    fun addProgressBar()
    fun prepareProgressBar()
}