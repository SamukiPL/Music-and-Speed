package me.samuki.musicandspeed.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Relation

@Entity(
        tableName = "lists"
)
data class ListEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "list_id")
        val id: Int,

        @ColumnInfo(name = "list_name")
        val name: String
)