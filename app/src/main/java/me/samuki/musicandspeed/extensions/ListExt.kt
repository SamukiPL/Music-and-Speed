package me.samuki.musicandspeed.extensions

import android.support.v4.media.MediaMetadataCompat
import me.samuki.musicandspeed.models.SongModel
import me.samuki.musicandspeed.services.media.MusicLibrary.LibraryModel


fun List<LibraryModel>.toSongModelList(): List<SongModel> {
    val songModelList = mutableListOf<SongModel>()
    forEach {
        songModelList.add(SongModel(
                it.metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
                it.metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
                it.metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST),
                it.metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM),
                it.albumArtUri,
                it.musicUri,
                it.metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI),
                it.metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
        ))
    }
    return songModelList
}

fun List<SongModel>.toSongSeed(): String {
    var songSeed = ""
    forEach {
        songSeed += "_" + it.id + "_"
    }
    return songSeed
}