package me.samuki.musicandspeed.activities.main.fragments

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import javax.inject.Inject


class MainPagerAdapter @Inject constructor(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    companion object {
        const val FRAGMENT_COUNT = 2
    }

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> {
                SongsFragment()
            }
            else -> {
                ListsFragment()
            }
        }
    }

    override fun getCount(): Int {
        return FRAGMENT_COUNT
    }

}