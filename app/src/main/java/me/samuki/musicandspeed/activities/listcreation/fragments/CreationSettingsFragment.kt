package me.samuki.musicandspeed.activities.listcreation.fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.listcreation.ListCreationActivity
import me.samuki.musicandspeed.activities.listcreation.viewmodel.ListCreationViewModel
import me.samuki.musicandspeed.base.BaseFragment


class CreationSettingsFragment : BaseFragment() {

    private val vm by lazy {
        provideActivityViewModel<ListCreationActivity, ListCreationViewModel>()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_creation_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

    }

}