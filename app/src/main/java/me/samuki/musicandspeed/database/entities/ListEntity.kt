package me.samuki.musicandspeed.database.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class ListEntity(
        @PrimaryKey
        val id: Int,

        val name: String
)