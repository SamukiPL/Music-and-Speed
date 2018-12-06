package me.samuki.musicandspeed.activities.main.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_list_with_button.*
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.listcreation.ListCreationActivity
import me.samuki.musicandspeed.activities.main.MainActivity
import me.samuki.musicandspeed.activities.main.adapters.ListsAdapter
import me.samuki.musicandspeed.activities.main.viewmodel.MainActivityViewModel
import me.samuki.musicandspeed.base.BaseFragment
import me.samuki.musicandspeed.extensions.onClick


class ListsFragment : BaseFragment() {

    private val vm by lazy {
        provideActivityViewModel<MainActivity, MainActivityViewModel>()
    }

    private val listsAdapter by lazy {
        ListsAdapter()
    }

    private val linearLayoutManager = LinearLayoutManager(context)
    private val handler = Handler()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_with_button, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        initRecyclerView()
        initActionButton()
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            adapter = listsAdapter
            layoutManager = linearLayoutManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setOnScrollChangeListener { _, _, _, _, _ ->
                    onScrollChange()
                }
            } else {
                addOnScrollListener(onScrollListener)
            }
        }
    }

    private fun initActionButton() {
        actionButton.apply {
            buttonIcon = R.drawable.baseline_add_black_48
            text = getString(R.string.create)
            onClick {
                startActivity(Intent(context, ListCreationActivity::class.java))
            }
        }
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                actionButton.showText = linearLayoutManager.findFirstVisibleItemPosition() == 0
            }, 100)
        }
    }

    private fun onScrollChange() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            actionButton.showText = linearLayoutManager.findFirstVisibleItemPosition() == 0
        }, 250)
    }

}