package me.samuki.musicandspeed.services.musicplayer

import android.media.MediaPlayer
import android.widget.TextView
import me.samuki.musicandspeed.models.IntervalModel
import me.samuki.musicandspeed.models.SongModel
import java.util.*


class MusicPlayerManager : MediaPlayer() {

    var firstServicePlay = true
    var playMusic = false

    var fullSongsList: List<SongModel> = emptyList()
    var intervalsList: List<IntervalModel> = emptyList()
    var currentList: List<SongModel> = emptyList()
    var currentSong: SongModel? = null
    var currentSpeed: Float = 0F

    var lastPlayedSongs: Stack<SongModel> = Stack()

    var nameView: TextView? = null
    var artistView: TextView? = null

    init {
        setOnCompletionListener { nextMusic() }
        setOnPreparedListener {
            if (playMusic) {
                start()
                firstServicePlay = false
            }
        }
    }

    fun startMusic() {
        playMusic(currentList.random(), true, playMusic = true)
    }

    fun startMusic(songId: String) {
        val songModel = currentList.firstOrNull { it.id == songId}
        playMusic(songModel ?: currentList.random(), true, playMusic = true)
    }

    fun playMusic(songId: Int, next: Boolean?, playMusic: Boolean) {
        val songModel = currentList[songId]
        playMusic(songModel, next, playMusic)
    }

    fun playMusic(songModel: SongModel, next: Boolean?) {
        playMusic(songModel, next, isPlaying)
    }

    fun playMusic(songModel: SongModel, next: Boolean?, playMusic: Boolean) {
        stopMusic()

        setDataSource(songModel.path)
        prepareAsync()
        currentSong = songModel
        this.playMusic = playMusic
        if (next == true)
            lastPlayedSongs.push(songModel)
        nameView?.text = currentSong?.name
        artistView?.text = currentSong?.artist
    }

    fun nextMusic() {
        playMusic(currentList.random(), true, isPlaying)
    }

    fun previousMusic() {
        if (lastPlayedSongs.isNotEmpty())
            playMusic(lastPlayedSongs.pop(), false)
    }

    fun restartMusic() {
        currentSong?.let {
            start()
        } ?: startMusic()
    }

    fun steerIntervals(speed: Float) {
        currentSpeed = speed
        currentList = intervalsList.firstOrNull { it.intervalSpeed >= speed }?.songs
                ?: intervalsList.lastOrNull()?.songs ?: fullSongsList
    }

    fun stopMusic() {
        stop()
        reset()
    }

}