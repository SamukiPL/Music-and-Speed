package me.samuki.musicandspeed.extensions

import android.support.v4.media.MediaMetadataCompat
import com.google.gson.Gson
import me.samuki.musicandspeed.database.entities.IntervalEntity
import me.samuki.musicandspeed.models.IntervalModel
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

fun List<SongModel>.songsToJson(): String {
    return Gson().toJson(this.map { it.id })
}

fun List<IntervalEntity>.entitiesToIntervals(songs: List<SongModel>): List<IntervalModel> {
    val gson = Gson()
    val intervals = mutableListOf<IntervalModel>()
    this.forEach {
        val songsId: List<String> = gson.fromJson(it.songs)
        intervals.add(
                IntervalModel(it.volume, it.speed,
                        songs.filter { songModel -> songsId.contains(songModel.id) })
        )
    }
    return intervals
}