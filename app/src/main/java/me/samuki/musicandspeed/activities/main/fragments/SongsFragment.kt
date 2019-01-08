package me.samuki.musicandspeed.activities.main.fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_list.*
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.main.MainActivity
import me.samuki.musicandspeed.activities.main.adapters.SongsAdapter
import me.samuki.musicandspeed.activities.main.viewmodel.MainActivityViewModel
import me.samuki.musicandspeed.base.BaseFragment


class SongsFragment : BaseFragment() {

    private val songsAdapter by lazy {
        SongsAdapter()
    }

    private val vm by lazy {
        provideActivityViewModel<MainActivity, MainActivityViewModel>()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        vm?.songsList?.observe(this, Observer { list ->
            list?.let {
                songsAdapter.itemList = it.sortedBy { song -> song.name }
            }
        })
    }

    private fun initRecyclerView() {
        list?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = songsAdapter
        }
    }

}