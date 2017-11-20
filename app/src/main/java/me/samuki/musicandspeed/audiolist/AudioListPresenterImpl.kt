package me.samuki.musicandspeed.audiolist

import android.provider.MediaStore
import android.view.View

import java.util.*

import me.samuki.musicandspeed.audiolist.AudioListActivity
import me.samuki.musicandspeed.MusicService.audioNames
import me.samuki.musicandspeed.MusicService.audioArtists
import me.samuki.musicandspeed.MusicService.audioPaths
import me.samuki.musicandspeed.MusicService.audioDurations

class AudioListPresenterImpl : AudioListPresenter<AudioListActivity> {
    private var view : AudioListActivity? = null

    override fun attachView(view: AudioListActivity) {
        this.view = view
    }

    override fun detach() {
        this.view = null
    }

    override fun setAudioLists() {
        audioNames = LinkedList<String>()
        audioArtists = LinkedList<String>()
        audioPaths = LinkedList<String>()
        audioDurations = LinkedList<Int>()

        val cr = view?.contentResolver

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cur = cr?.query(uri, null, selection, null, sortOrder)
        val count: Int

        if (cur != null) {
            count = cur!!.getCount()

            if (count > 0) {
                while (cur!!.moveToNext()) {
                    val name = cur!!.getString(cur!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    audioNames.add(name)
                    val artist = cur!!.getString(cur!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    audioArtists.add(artist)
                    val path = cur!!.getString(cur!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    audioPaths.add(path)
                    val duration = cur!!.getInt(cur!!.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    audioDurations.add(duration / 1000)
                }
            }
        }

        if (cur != null)
            cur!!.close()
    }

    override fun getAudioNamesList(): List<String> {
        return audioNames
    }

    override fun getAudioArtistsList(): List<String> {
        return audioArtists
    }

    override fun getAudioPathsList(): List<String> {
        return audioPaths
    }

    override fun getAudioDurationsList(): List<Long> {
        return audioDurations
    }

    override fun askForPermission() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}