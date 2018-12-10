package me.samuki.musicandspeed.activities.listcreation.fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_list_creation.*
import kotlinx.android.synthetic.main.fragment_creation_settings.*
import kotlinx.android.synthetic.main.fragment_creation_summary.*
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.listcreation.ListCreationActivity
import me.samuki.musicandspeed.activities.listcreation.adapters.SummaryListAdapter
import me.samuki.musicandspeed.activities.listcreation.viewmodel.ListCreationViewModel
import me.samuki.musicandspeed.base.BaseFragment
import me.samuki.musicandspeed.extensions.onClick
import me.samuki.musicandspeed.models.IntervalModel


class CreationSummaryFragment: BaseFragment() {

    private val vm by lazy {
        provideActivityViewModel<ListCreationActivity, ListCreationViewModel>()
    }

    private val summaryAdapter by lazy {
        SummaryListAdapter(summaryListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_creation_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onStart() {
        super.onStart()
        vm?.createdIntervals?.observe(this, Observer { intervals ->
            intervals?.let {
                summaryAdapter.setItems(it.toMutableList())
            }
        })
    }

    private fun initViews() {
        initRecyclerView()
        initActionButton()
    }

    private fun initRecyclerView() {
        summaryRecycler.apply {
            adapter = summaryAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initActionButton() {
        activity?.actionButton?.apply {
            buttonIcon = R.drawable.baseline_done_black_48
            showText = false
            onClick {
//                vm?.endSongSelection(volumeBar.progress, speedBar.progress)
            }
        }
    }

    private val summaryListener = object : SummaryListAdapter.SummaryListener {

        override fun addNewInterval() {
            TODO("Łukasz weź się do roboty!")
        }

        override fun goToInterval(interval: IntervalModel) {
            TODO("Łukasz weź się do roboty!")
        }

    }
}