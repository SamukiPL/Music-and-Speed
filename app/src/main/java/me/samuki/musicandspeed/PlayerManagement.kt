package me.samuki.musicandspeed

/**
 * Created by Samuki on 20.11.2017.
 */
interface PlayerManagement {
    abstract fun playMusic()
    abstract fun playMusic(whichMusic: Int, isPrevious: Boolean)
    abstract fun stopMusic()
    abstract fun pauseMusic()
    abstract fun restartMusic()
    abstract fun nextMusic()
    abstract fun previousMusic()
}