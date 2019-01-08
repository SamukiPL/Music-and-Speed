package me.samuki.musicandspeed.database.daos

import android.arch.persistence.room.*
import me.samuki.musicandspeed.database.entities.ListEntity
import me.samuki.musicandspeed.models.IntervalModel

@Dao
abstract class ListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertList(list: ListEntity): Long

    @Query("""
            SELECT * FROM lists WHERE list_name = :name LIMIT 1
    """)
    abstract fun isListExisting(name: String): ListEntity?

    @Query("""
            SELECT list_id FROM lists WHERE list_name = :name LIMIT 1
    """)
    abstract fun getListIdFromName(name: String): Long

    @Query("""
        SELECT * FROM lists
    """)
    abstract fun getAllLists(): List<ListEntity>

    @Update
    abstract fun updateList(list: ListEntity)

    @Delete
    abstract fun deleteList(list: ListEntity)

    @Delete
    abstract fun deleteLists(list: List<ListEntity>)

    @Transaction
    open fun insertList(name: String): Long {
        val isExisting = isListExisting(name) != null

        return if (isExisting) {
            -1
        } else {
            insertList(ListEntity(0, name))
        }
    }

}