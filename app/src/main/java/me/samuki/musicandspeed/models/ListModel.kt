package me.samuki.musicandspeed.models


const val HEADER_VIEW = 0
const val SONG_VIEW = 1
const val MUSIC_LIST_VIEW = 2

interface ListModel {

    fun getDiff(): String
    fun getSortValue(): String
    fun viewType(): Int

}