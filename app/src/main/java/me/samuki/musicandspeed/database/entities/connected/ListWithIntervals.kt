package me.samuki.musicandspeed.database.entities.connected

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Relation
import me.samuki.musicandspeed.database.entities.IntervalEntity
import me.samuki.musicandspeed.database.entities.ListEntity

@Entity
class ListWithIntervals {
    @Embedded
    var list: ListEntity? = null
    @Relation(
            parentColumn = "list_id",
            entityColumn = "list_id",
            entity = IntervalEntity::class)
    var intervals: List<IntervalEntity> = emptyList()
}