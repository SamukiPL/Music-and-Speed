package me.samuki.musicandspeed.models

import android.net.Uri


data class SongModel(

        var name: String,

        var artist: String,

        var album: String,

        var albumArtPath: Uri,

        var path: Uri,

        var albumCover: String,

        var duration: Long

) {
    fun getDiff(): String {
        return "$name $artist $album $path $duration"
    }
}