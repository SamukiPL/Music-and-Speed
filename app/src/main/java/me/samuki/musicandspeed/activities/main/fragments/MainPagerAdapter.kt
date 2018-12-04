package me.samuki.musicandspeed.activities.main.fragments

import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.navigation_tile.view.*
import me.samuki.musicandspeed.R
import javax.inject.Inject


class MainPagerAdapter (
        fragmentManager: FragmentManager,
        private val layoutInflater: LayoutInflater
) : FragmentPagerAdapter(fragmentManager) {

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

    fun setTabs(tabLayout: TabLayout) {
        val songsTile = layoutInflater.inflate(R.layout.navigation_tile, null)
        songsTile.tileName.setText(R.string.songs)
        tabLayout.getTabAt(0)?.customView = songsTile

        val listsTile = layoutInflater.inflate(R.layout.navigation_tile, null)
        listsTile.tileName.setText(R.string.lists)
        tabLayout.getTabAt(1)?.customView = listsTile
    }

}