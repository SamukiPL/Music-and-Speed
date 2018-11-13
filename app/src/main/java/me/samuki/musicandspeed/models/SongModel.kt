package me.samuki.musicandspeed.models


data class SongModel(

        var name: String,

        var artist: String,

        var album: String,

        var path: String,

        var albumCover: String,

        var duration: Long

) {
    fun getDiff(): String {
        return "$name $artist $album $path $duration"
    }
}