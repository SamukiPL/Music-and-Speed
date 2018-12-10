package me.samuki.musicandspeed.activities.listcreation.fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_list_creation.*
import kotlinx.android.synthetic.main.fragment_creation_settings.*
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.listcreation.ListCreationActivity
import me.samuki.musicandspeed.activities.listcreation.viewmodel.ListCreationViewModel
import me.samuki.musicandspeed.base.BaseFragment
import me.samuki.musicandspeed.extensions.onClick


class CreationSettingsFragment : BaseFragment() {

    private val vm by lazy {
        provideActivityViewModel<ListCreationActivity, ListCreationViewModel>()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_creation_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onStart() {
        super.onStart()

    }

    private fun initViews() {
        initVolumeText()
        initVolumeBar()
        initSpeedText()
        initSpeedBar()
        initActionButton()
    }

    private fun initVolumeText() {
        volumeText.text = getString(R.string.volume, volumeBar.progress)
    }

    private fun initVolumeBar() {
        volumeBar.apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    volumeText.text = getString(R.string.volume, progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun initSpeedText() {
        speedText.text = getString(R.string.volume, speedBar.progress)
    }

    private fun initSpeedBar() {
        speedBar.apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    speedText.text = getString(R.string.volume, progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun initActionButton() {
        activity?.actionButton?.apply {
            buttonIcon = R.drawable.baseline_done_black_48
            showText = false
            onClick {
                vm?.endSongSelection(volumeBar.progress, speedBar.progress)
            }
        }
    }

}