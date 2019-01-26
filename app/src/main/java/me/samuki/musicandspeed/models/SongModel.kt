package me.samuki.musicandspeed.models

import android.net.Uri


data class SongModel(

        var id: String,

        var name: String,

        var artist: String,

        var album: String,

        var albumArtPath: Uri,

        var path: String,

        var albumCover: String,

        var duration: Long

): ListModel {

    override fun getDiff() = "$name $artist $album $path $duration"

    override fun getSortValue() = name

    override fun getViewType() = SONG_VIEW
}