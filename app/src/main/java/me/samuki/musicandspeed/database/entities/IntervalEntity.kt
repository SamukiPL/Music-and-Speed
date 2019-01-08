package me.samuki.musicandspeed.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(
        tableName = "intervals",
        foreignKeys = [
            ForeignKey(
                    entity = ListEntity::class,
                    parentColumns = arrayOf(
                            "list_id"
                    ),
                    childColumns = arrayOf(
                            "list_id"
                    ),
                    onDelete = ForeignKey.CASCADE
            )
        ]
)
data class IntervalEntity (

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "interval_id")
        val id: Long,

        @ColumnInfo(name = "interval_volume")
        val volume: Int,

        @ColumnInfo(name = "interval_speed")
        val speed: Int,

        @ColumnInfo(name = "interval_songs")
        val songs: String,

        @ColumnInfo(name = "list_id")
        val listId: Long

)