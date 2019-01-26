package me.samuki.musicandspeed.utility

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

const val CHOSEN_LIST_ID = "CHOSEN_LIST_ID"

class AppPreferences @Inject constructor(
        application: Application
){

    private val preferences: SharedPreferences = application.getSharedPreferences("", Context.MODE_PRIVATE)

    var chosenListId: Long
        get() {
            return this.preferences.getLong(CHOSEN_LIST_ID, -1L)
        }
        set(value) {
            this.preferences.edit().putLong(CHOSEN_LIST_ID, value).apply()
        }

}