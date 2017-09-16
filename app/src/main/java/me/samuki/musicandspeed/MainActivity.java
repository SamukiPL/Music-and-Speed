package me.samuki.musicandspeed;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String DEBUG_TAG = "Debugujemy";

    static LayoutInflater inflater;

    private TextView speedView, titleView;
    private MusicService musicService;
    private int trackId;
    private boolean playNewSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speedView = (TextView) findViewById(R.id.actualSpeed);
        titleView = (TextView) findViewById(R.id.actualSong);

        setToolbar();

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                trackId = 0;
                playNewSong = false;
            } else {
                trackId = extras.getInt("trackId");
                playNewSong = extras.getBoolean("play");
            }
        } else {
            trackId = (int) savedInstanceState.getSerializable("trackId");
        }

        inflater = getLayoutInflater();

        Intent bindIntent = new Intent(this, MusicService.class);
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    void setToolbar() {android.support.v7.widget.Toolbar toolbar =
            (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(musicService != null)
            unbindService(serviceConnection);
        super.onDestroy();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) iBinder;
            musicService = binder.getService();
            ImageButton button = (ImageButton) findViewById(R.id.playButton);
            musicService.setSpeedViewAndTitleViewAndPlayButton(speedView, titleView, button);
            if(!MusicService.playerManager.isPlaying && playNewSong) {
                playMusic(trackId);
            } else {
                if(trackId != -86 || playNewSong) {
                    button.setContentDescription(getString(R.string.stop));
                    button.setImageResource(android.R.drawable.ic_media_pause);
                    if (trackId >= 0) playMusic(trackId);
                }
            }
            MusicService.playerManager.setProgressBar((ProgressBar)findViewById(R.id.progressBar));
            //Tutaj musi być coś co ma się zrobić jeśli w tle cały czas działałą apka,
            // w sensie jakaś fajna metoda
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void startMusicService(int trackId) {
        Intent startIntent = new Intent(this, MusicService.class);
        startIntent.setAction("Start");
        startIntent.putExtra("trackId", trackId);
        startService(startIntent);
    }

    private void restartMusicService() {
        Intent startIntent = new Intent(this, MusicService.class);
        startIntent.setAction("Restart");
        startService(startIntent);
    }

    private void pauseMusicService() {
        Intent pauseIntent = new Intent(this, MusicService.class);
        pauseIntent.setAction("Pause");
        startService(pauseIntent);
    }

    public void playMusic(View view) {
        ImageButton button = (ImageButton) view;
        if(button.getContentDescription().equals(getString(R.string.play))) {
            restartMusicService();
            button.setContentDescription(getString(R.string.stop));
            button.setImageResource(android.R.drawable.ic_media_pause);
        }
        else {
            pauseMusicService();
            button.setContentDescription(getString(R.string.play));
            button.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    public void playMusic(int trackId) {
        ImageButton button = (ImageButton) findViewById(R.id.playButton);

        startMusicService(trackId);
        button.setContentDescription(getString(R.string.stop));
        button.setImageResource(android.R.drawable.ic_media_pause);
    }

    public void previousMusic(View view) {
        Intent previousIntent = new Intent(this, MusicService.class);
        previousIntent.setAction("Previous");
        startService(previousIntent);

    }

    public void nextMusic(View view) {
        Intent nextIntent = new Intent(this, MusicService.class);
        nextIntent.setAction("Next");
        startService(nextIntent);
    }
}
