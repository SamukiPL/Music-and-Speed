package me.samuki.musicandspeed;

import android.Manifest;
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

import java.util.LinkedList;

import static me.samuki.musicandspeed.MusicService.audioNames;
import static me.samuki.musicandspeed.MusicService.paths;

public class MainActivity extends AppCompatActivity {
    public static final String DEBUG_TAG = "Debugujemy";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    static LayoutInflater inflater;
    static boolean isPermission;

    private TextView speedView, titleView;
    private MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speedView = (TextView) findViewById(R.id.actualSpeed);
        titleView = (TextView) findViewById(R.id.actualSong);

        inflater = getLayoutInflater();

        isPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        Log.d(DEBUG_TAG, String.valueOf(isPermission) + "TAL");
        if(isPermission) setAudioNamesList(); else askForPermission();

        Intent bindIntent = new Intent(this, MusicService.class);
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        new CountDownTimer(3000, 1000) {

            @Override
            public void onTick(long l) {}

            @Override
            public void onFinish() {
                Log.d(DEBUG_TAG, musicService+" ");
                if(musicService != null) {
                    musicService.setSpeedViewAndTitleView(speedView, titleView);
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        if(musicService != null)
            unbindService(serviceConnection);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isPermission = true;
                    setAudioNamesList();
                } else {
                    isPermission = false;
                } return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void askForPermission() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            isPermission = true;
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) iBinder;
            musicService = binder.getService();
            musicService.setSpeedViewAndTitleView(speedView, titleView);
            //Tutaj musi być coś co ma się zrobić jeśli w tle cały czas działałą apka,
            // w sensie jakaś fajna metoda
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void startMusicService() {
        Intent startIntent = new Intent(this, MusicService.class);
        startIntent.setAction("Start");
        startService(startIntent);
        musicService.setSpeedViewAndTitleView(speedView, titleView);
    }

    private void stopMusicService() {
        Intent stopIntent = new Intent(this, MusicService.class);
        stopIntent.setAction("Stop");
        startService(stopIntent);
    }

    public void setAudioNamesList() {
        audioNames = new LinkedList<String>();
        paths = new LinkedList<String>();

        ContentResolver cr = this.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;

        if(cur != null)
        {
            count = cur.getCount();

            if(count > 0)
            {
                while(cur.moveToNext())
                {
                    String data = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    Log.d(DEBUG_TAG, data);
                    audioNames.add(data);
                    String path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    paths.add(path);
                }

            }
        }

        assert cur != null;
        cur.close();
    }

    public void goToAudioList(View view) {
        Intent audioListIntent = new Intent(this, AudioListActivity.class);
        startActivity(audioListIntent);
    }

    public void playMusic(View view) {
        Button button = (Button) view;
        if(((Button) view).getText().equals(getString(R.string.play))) {
            startMusicService();
            button.setText(getString(R.string.stop));
        }
        else {
            stopMusicService();
            button.setText(getString(R.string.play));
        }

    }
}
