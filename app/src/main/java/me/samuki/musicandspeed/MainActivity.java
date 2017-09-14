package me.samuki.musicandspeed;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.LinkedList;

import static me.samuki.musicandspeed.MusicService.audioNames;
import static me.samuki.musicandspeed.MusicService.paths;

public class MainActivity extends AppCompatActivity {
    public static final String DEBUG_TAG = "Debugujemy";

    static LayoutInflater inflater;

    private TextView speedView, titleView;
    private MusicService musicService;
    private int trackId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speedView = (TextView) findViewById(R.id.actualSpeed);
        titleView = (TextView) findViewById(R.id.actualSong);

        android.support.v7.widget.Toolbar toolbar =
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

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                trackId = 0;
            } else {
                trackId = extras.getInt("trackId");
            }
        } else {
            trackId = (int) savedInstanceState.getSerializable("trackId");
        }

        inflater = getLayoutInflater();

        Intent bindIntent = new Intent(this, MusicService.class);
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
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
            musicService.setSpeedViewAndTitleView(speedView, titleView);
            if(!MusicService.playerManager.isPlaying) {
                playMusic(trackId);
            } else {
                Button button = (Button) findViewById(R.id.playButton);
                button.setText(getString(R.string.stop));
                if(trackId != -1) playMusic(trackId);
            }
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

    private void stopMusicService() {
        Intent stopIntent = new Intent(this, MusicService.class);
        stopIntent.setAction("Stop");
        startService(stopIntent);
    }

    public void playMusic(View view) {
        Button button = (Button) view;
        if(((Button) view).getText().equals(getString(R.string.play))) {
            startMusicService(-1);
            button.setText(getString(R.string.stop));
        }
        else {
            stopMusicService();
            button.setText(getString(R.string.play));
        }

    }

    public void playMusic(int trackId) {
        Button button = (Button) findViewById(R.id.playButton);

        startMusicService(trackId);
        button.setText(getString(R.string.stop));
    }
}
