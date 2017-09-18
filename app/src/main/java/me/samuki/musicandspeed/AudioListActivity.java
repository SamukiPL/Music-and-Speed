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
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import static me.samuki.musicandspeed.MusicService.audioNames;
import static me.samuki.musicandspeed.MusicService.paths;
import static me.samuki.musicandspeed.MainActivity.DEBUG_TAG;

public class AudioListActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    static boolean isPermission;

    private LayoutInflater inflater;
    private MusicService musicService;
    private boolean serviceDisconnected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);
        inflater = getLayoutInflater();
        setToolbar();

        isPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if(isPermission) setAudioNamesList(); else askForPermission();
    }

    @Override
    protected void onResume() {
        Intent bindIntent = new Intent(this, MusicService.class);
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    void setToolbar() {
        android.support.v7.widget.Toolbar toolbar =
            (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void setTitleAndProgressBar() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.player_progressBar);
        MusicService.playerManager.setProgressBar(progressBar);
        TextView titleView = (TextView) findViewById(R.id.player_trackTitle);
        titleView.setText(audioNames.get(MusicService.playerManager.getActualMusicPlaying()));
    }

    void setPlayButton() {
        ImageButton button = (ImageButton) findViewById(R.id.playButton);
        if(MusicService.playerManager.isPlaying) {
            button.setContentDescription(getString(R.string.stop));
            button.setImageResource(android.R.drawable.ic_media_pause);
        }
        else {
            button.setContentDescription(getString(R.string.play));
            button.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        if(musicService != null)
            unbindService(serviceConnection);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(musicService != null && serviceDisconnected)
            unbindService(serviceConnection);
        super.onDestroy();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(DEBUG_TAG, MusicService.playerManager.getActualMusicPlaying() + "");
            MusicService.LocalBinder binder = (MusicService.LocalBinder) iBinder;
            musicService = binder.getService();
            setTitleAndProgressBar();
            setPlayButton();
            serviceDisconnected = false;
            //Tutaj musi być coś co ma się zrobić jeśli w tle cały czas działałą apka,
            // w sensie jakaś fajna metoda
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceDisconnected = true;
        }
    };

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

    public void setAudioNamesList() {
        audioNames = new LinkedList<String>();
        paths = new LinkedList<String>();

        ContentResolver cr = this.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;

        LinearLayout container = (LinearLayout) findViewById(R.id.musicContainer);
        int i = 0;

        if(cur != null)
        {
            count = cur.getCount();

            if(count > 0)
            {
                while(cur.moveToNext())
                {
                    String name = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    int duration = cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    audioNames.add(name);
                    String path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    paths.add(path);
                    RelativeLayout musicRow = (RelativeLayout) inflater.inflate(R.layout.music_row, null);
                    TextView nameView = musicRow.findViewById(R.id.musicRow_audioTitle);
                    TextView artistView = musicRow.findViewById(R.id.musicRow_audioArtist);
                    TextView durationView = musicRow.findViewById(R.id.musicRow_audioDuration);
                    final int trackId = i++;
                    nameView.setText(name);
                    artistView.setText(artist);
                    durationView.setText(duration + "");
                    musicRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goToMainActivity(trackId);
                        }
                    });
                    container.addView(musicRow);
                }

            }
        }

        assert cur != null;
        cur.close();
    }

    public void goToMainActivity(int trackId) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("trackId", trackId);
        mainIntent.putExtra("play", true);
        startActivity(mainIntent);
    }

    public void goToMainActivity(View view) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("trackId", -86);
        mainIntent.putExtra("play", MusicService.playerManager.isPlaying);
        startActivity(mainIntent);
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
