package me.samuki.musicandspeed.activities.main

import android.Manifest
import android.Manifest.permission.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_tile.view.*
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.main.fragments.MainPagerAdapter
import me.samuki.musicandspeed.activities.main.viewmodel.MainActivityViewModel
import me.samuki.musicandspeed.base.BaseActivity
import me.samuki.musicandspeed.dialog.ListNameDialogFragment
import me.samuki.musicandspeed.extensions.onClick
import me.samuki.musicandspeed.services.musicplayer.MusicPlayerService
import me.samuki.musicandspeed.utility.BundleConstants


class MainActivity: BaseActivity(true) {

    private var musicService: MusicPlayerService? = null

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
        actionButton.visibility = View.INVISIBLE

        val bindIntent = Intent(this, MusicPlayerService::class.java)
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            vm.getSongs()
            vm.getMusicLists()
            mainViewPager.visibility = View.VISIBLE
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), 0)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            vm.getSongs()
            vm.getMusicLists()
            mainViewPager.visibility = View.VISIBLE
        }
    }

    private fun initView() {
        initTiles()
        initViewPager()
        initPlayerViews()
        initActionButton()
    }

    private fun initTiles() {
//        songsTile?.tileName?.setText(R.string.songs)
//        listsTile?.tileName?.setText(R.string.lists)
    }

    private fun initViewPager() {
        mainViewPager?.adapter = pagerAdapter
        pagerAdapter.setTabs(tabLayout.apply {
            setupWithViewPager(mainViewPager)
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(p0: TabLayout.Tab?) {}

                override fun onTabUnselected(p0: TabLayout.Tab?) {
                    actionButton.hide()
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.customView?.let {
                        if (it.tileName.text == getString(R.string.lists))
                            actionButton.show()
                    }
                }
            })
        })
    }

    private fun initPlayerViews() {
        previousButton.onClick {
            previousMusicService()
        }
        nextButton.onClick {
            nextMusicService()
        }
        playStopButton.onClick {
            if (musicService?.playerManager?.firstServicePlay == true) {
                startMusicService("")
                playStopButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_pause_black_48))
            } else if (musicService == null || musicService?.playerManager?.isPlaying == false) {
                restartMusicService()
                playStopButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_pause_black_48))
            } else if (musicService?.playerManager?.isPlaying == true) {
                pauseMusicService()
                playStopButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_play_arrow_black_48))
            }
        }
    }

    private fun initActionButton() {
        actionButton.apply {
            buttonIcon = R.drawable.baseline_add_black_48
            text = getString(R.string.create)
            onClick {
                ListNameDialogFragment().show(supportFragmentManager, "LIST_NAME")
            }
        }
    }

    override fun onDestroy() {
        if (musicService != null)
            unbindService(serviceConnection)
        super.onDestroy()
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            musicService = (iBinder as MusicPlayerService.LocalBinder).getService()
//            val button = findViewById<View>(R.id.playButton) as ImageButton
//            musicService.setSpeedViewAndTitleViewAndPlayButton(speedView, titleView, button)
//            if (!MusicService.playerManager.isPlaying && playNewSong) {
//                startMusic(trackId)
//            } else {
//                if (MusicService.playerManager.isPlaying) {
//                    button.contentDescription = getString(R.string.stop)
//                    button.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp)
//                }
//                if (trackId >= 0) startMusic(trackId)
//            }
//            MusicService.playerManager.setProgressBar(findViewById<View>(R.id.progressBar) as ProgressBar)
            //Tutaj musi być coś co ma się zrobić jeśli w tle cały czas działałą apka,
            // w sensie jakaś fajna metoda
        }

        override fun onServiceDisconnected(componentName: ComponentName) {

        }
    }

    fun startMusicService(songId: String) {
        musicService?.playerManager?.nameView = playerSongName
        musicService?.playerManager?.artistView = playerSongArtist
        checkPermissions()
        val startIntent = Intent(this, MusicPlayerService::class.java)
        startIntent.putExtra(BundleConstants.SONG_ID, songId)
        startIntent.action = "Start"
        startService(startIntent)
    }

    private fun restartMusicService() {
        checkPermissions()
        val startIntent = Intent(this, MusicPlayerService::class.java)
        startIntent.action = "Restart"
        startService(startIntent)
    }

    private fun pauseMusicService() {
        checkPermissions()
        val pauseIntent = Intent(this, MusicPlayerService::class.java)
        pauseIntent.action = "Pause"
        startService(pauseIntent)
    }

    private fun previousMusicService() {
        checkPermissions()
        val pauseIntent = Intent(this, MusicPlayerService::class.java)
        pauseIntent.action = "Previous"
        startService(pauseIntent)
    }

    private fun nextMusicService() {
        checkPermissions()
        val pauseIntent = Intent(this, MusicPlayerService::class.java)
        pauseIntent.action = "Next"
        startService(pauseIntent)
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), 0)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ActivityCompat.requestPermissions(this, arrayOf(FOREGROUND_SERVICE), 0)
            }
        }
    }

}