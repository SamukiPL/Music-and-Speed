package me.samuki.musicandspeed.player

import android.media.MediaPlayer
import android.provider.MediaStore
import me.samuki.musicandspeed.models.AudioModel
import java.io.IOException

/**
 * Created by Samuki on 11.12.2017.
 */
class PlayerManager() : PlayerManagement, PlayerCommunication {

    var mediaPlayer: MediaPlayer? = null
    var audioModelsList: List<AudioModel>? = null
    var actualMusicPlaying: Int = 0

    init {
        initMediaPlayer()
    }

    override fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
    }

    override fun initAudioModelsList() {
        audioModelsList = ArrayList()
    }

    override fun addAudioModels(audioModelsList: List<AudioModel>) {
        this.audioModelsList = audioModelsList
    }

    @Throws(IOException::class)
    override fun playMusic() {

    }

    override fun playMusic(whichMusic: Int, isPrevious: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changeVolumeUp() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changeVolumeDown() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun playLastMusic() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addProgressBar() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stopMusic() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun prepareProgressBar() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pauseMusic() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun restartMusic() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun nextMusic() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun previousMusic() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setLastMusicPlayed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addToLastMusicPlayed(number: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLastOnePlayed(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun popLastOnePlayed(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}