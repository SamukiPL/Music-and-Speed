package me.samuki.musicandspeed.models


data class MusicListModel (
        val id: Long,
        val name: String,
        val chosen: Boolean
) : ListModel {

    override fun getDiff() = name

    override fun getSortValue() = name

    override fun getViewType() = MUSIC_LIST_VIEW
}