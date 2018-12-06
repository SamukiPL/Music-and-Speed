package me.samuki.musicandspeed.activities.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_tile.view.*
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.listcreation.ListCreationActivity
import me.samuki.musicandspeed.activities.main.fragments.MainPagerAdapter
import me.samuki.musicandspeed.activities.main.viewmodel.MainActivityViewModel
import me.samuki.musicandspeed.base.BaseActivity
import me.samuki.musicandspeed.extensions.onClick
import me.samuki.musicandspeed.services.media.MusicLibrary
import javax.inject.Inject


class MainActivity: BaseActivity(true) {

    private val vm by lazy {
        provideViewModel<MainActivityViewModel>()
    }

    private val pagerAdapter by lazy {
        MainPagerAdapter(supportFragmentManager, layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    override fun onStart() {
        super.onStart()
        vm.getSongs()
    }

    private fun initView() {
        initTiles()
        initViewPager()
    }

    private fun initTiles() {
//        songsTile?.tileName?.setText(R.string.songs)
//        listsTile?.tileName?.setText(R.string.lists)
    }

    private fun initViewPager() {
        mainViewPager?.adapter = pagerAdapter
        pagerAdapter.setTabs(tabLayout.apply {
            setupWithViewPager(mainViewPager)
        })
    }

}