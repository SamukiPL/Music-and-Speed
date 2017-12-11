package me.samuki.musicandspeed.player

/**
 * Created by Samuki on 20.11.2017.
 */
interface PlayerManagement {
    fun initMediaPlayer()
    fun initAudioModelsList()
    fun playMusic()
    fun playMusic(whichMusic: Int, isPrevious: Boolean)
    fun playLastMusic()
    fun stopMusic()
    fun pauseMusic()
    fun restartMusic()
    fun nextMusic()
    fun previousMusic()
    fun setLastMusicPlayed()
    fun addToLastMusicPlayed(number: Int)
    fun getLastOnePlayed(): Int
    fun popLastOnePlayed(): Int
}