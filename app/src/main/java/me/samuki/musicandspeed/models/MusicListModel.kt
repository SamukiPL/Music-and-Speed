package me.samuki.musicandspeed.models


data class MusicListModel (
        val name: String
) : ListModel {

    override fun getDiff() = name

    override fun getSortValue() = name

    override fun getViewType() = MUSIC_LIST_VIEW
}