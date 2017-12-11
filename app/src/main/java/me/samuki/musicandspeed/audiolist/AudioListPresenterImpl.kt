package me.samuki.musicandspeed.audiolist

import android.media.AudioManager
import android.provider.MediaStore
import me.samuki.musicandspeed.MusicService.*
import me.samuki.musicandspeed.models.AudioModel
import me.samuki.musicandspeed.player.PlayerCommunication
import me.samuki.musicandspeed.player.PlayerManagement
import me.samuki.musicandspeed.player.PlayerManager
import java.util.*
import kotlin.collections.ArrayList
import android.content.ContentUris
import android.net.Uri
import android.graphics.Bitmap




class AudioListPresenterImpl : AudioListPresenter<AudioListView> {

    private var view : AudioListView? = null
    lateinit private var playerManager : PlayerCommunication

    override fun attachView(view: AudioListView) {
        this.view = view
    }

    override fun detach() {
        this.view = null
    }

    override fun initPlayerManager() {
        playerManager = PlayerManager()
    }

    override fun setAudioLists() {
        val audioModelsList: List<AudioModel> = ArrayList()

        val cr = view?.getContext()?.contentResolver

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cur = cr?.query(uri, null, selection, null, sortOrder)
        val count: Int

        if (cur != null) {
            count = cur.count

            if (count > 0) {
                while (cur.moveToNext()) {
                    val name = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val duration = cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val albumID = cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

                    val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                    val uri = ContentUris.withAppendedId(sArtworkUri, albumID)
                    val bitmap = MediaStore.Images.Media.getBitmap(view?.getContext()?.getContentResolver(), uri)

                    val audioModel: AudioModel = AudioModel(count, name, artist, duration / 1000, path, bitmap)
                    audioModelsList.toMutableList().add(audioModel)
                }
            }
        }

        cur?.close()
        playerManager.addAudioModels(audioModelsList)
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