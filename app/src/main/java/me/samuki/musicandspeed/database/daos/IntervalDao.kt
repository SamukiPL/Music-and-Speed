package me.samuki.musicandspeed.database.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import me.samuki.musicandspeed.database.entities.IntervalEntity
import me.samuki.musicandspeed.extensions.toSongSeed
import me.samuki.musicandspeed.models.IntervalModel

@Dao
abstract class IntervalDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertInterval(interval: IntervalEntity): Long

    @Query("""
            SELECT * FROM intervals WHERE interval_speed = :speed AND
            interval_songs = :songsSeed AND list_id = :listId LIMIT 1
    """)
    abstract fun isIntervalExisting(speed: Int, songsSeed: String, listId: Long): IntervalEntity?

    @Query("""
        SELECT * FROM intervals WHERE list_id = :listId
    """)
    abstract fun getAllIntervals(listId: Long): List<IntervalEntity>

    @Update
    abstract fun updateInterval(interval: IntervalEntity)

    @Delete
    abstract fun deleteInterval(interval: IntervalEntity)

    @Delete
    abstract fun deleteIntervals(intervals: List<IntervalEntity>)

    @Transaction
    open fun insertInterval(interval: IntervalModel, listId: Long): Boolean {
        val songsSeed = interval.songs.toSongSeed()
        val isExisting = isIntervalExisting(interval.intervalSpeed, songsSeed, listId) != null

        return if (isExisting) {
            false
        } else {
            insertInterval(IntervalEntity(
                    0,
                    interval.volume,
                    interval.intervalSpeed,
                    songsSeed,
                    listId
            ))
            true
        }
    }

}