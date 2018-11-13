package me.samuki.musicandspeed.activities.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_tile.view.*
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.main.fragments.MainPagerAdapter
import me.samuki.musicandspeed.activities.main.viewmodel.MainActivityViewModel
import me.samuki.musicandspeed.base.BaseActivity
import javax.inject.Inject


class MainActivity: BaseActivity(true) {

    private val vm by lazy {
        ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
    }

    private val pagerAdapter by lazy {
        MainPagerAdapter(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    override fun onStart() {
        super.onStart()
        vm.getSongs()
        vm?.songsList?.observe(this, Observer { list ->
            list?.let {
//                songsAdapter.itemList = it
                Log.d("TEST", list.toString())
            }
        })
    }

    private fun initView() {
        initTiles()
        initViewPager()
    }

    private fun initTiles() {
        songsTile?.tileName?.setText(R.string.songs)
        listsTile?.tileName?.setText(R.string.lists)
    }

    private fun initViewPager() {
        mainViewPager?.adapter = pagerAdapter
    }

}