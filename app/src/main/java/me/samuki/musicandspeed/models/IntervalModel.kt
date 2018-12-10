package me.samuki.musicandspeed.models


data class IntervalModel(
        val volume: Int,
        val intervalSpeed: Int,
        val songs: List<SongModel>
) : ListModel{

    override fun getDiff() = "$intervalSpeed $volume ${songs.size}"

    override fun getSortValue() = intervalSpeed.toString()

    override fun getViewType() = INTERVAL_VIEW
}