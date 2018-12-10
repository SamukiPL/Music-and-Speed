package me.samuki.musicandspeed.models


data class HeaderModel(
        val headerTextRes: Int
) : ListModel{

    override fun getDiff() = headerTextRes.toString()

    override fun getSortValue() = ""

    override fun getViewType() = HEADER_VIEW
}