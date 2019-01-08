package me.samuki.musicandspeed.activities.listcreation.fragments

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.DialogInterface
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

    private var defaultSongsSelection: Boolean = false
    private var speedsList: List<Int> = emptyList()

    private var firstSpeed: Int = 0

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
        vm?.defaultSongsSelection?.observe(this, Observer { settings ->
            settings?.let {
                defaultSongsSelection = it.speed == -1
                if (defaultSongsSelection)
                    speedContainer.visibility = View.GONE

                volumeBar.progress = it.volume
                speedBar.progress = it.speed
                firstSpeed = it.speed
            }
        })
        vm?.createdIntervals?.observe(this, Observer { intervals ->
            intervals?.let {
                speedsList = it.map { interval -> interval.intervalSpeed }
            }
        })
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
                val currentSpeed = if (defaultSongsSelection) -1 else speedBar.progress
                if (vm?.intervalIsEditing() == false && speedsList.contains(currentSpeed) ||
                        vm?.intervalIsEditing() == true && firstSpeed != currentSpeed && speedsList.contains(currentSpeed)) {
                    showDialogBeforeCompletion(currentSpeed)
                } else {
                    vm?.endSongSelection(volumeBar.progress, currentSpeed)
                }
            }
        }
    }

    private fun showDialogBeforeCompletion(currentSpeed: Int) {
        AlertDialog.Builder(context)
                .setMessage(R.string.intervalAddingDialogMessage)
                .setPositiveButton(R.string.connectIntervals) { _, _ ->
                    vm?.connectIntervals(volumeBar.progress, currentSpeed)
                }
                .setNeutralButton(R.string.replaceInterval) { _, _ ->
                    vm?.endSongSelection(volumeBar.progress, currentSpeed)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
    }

}