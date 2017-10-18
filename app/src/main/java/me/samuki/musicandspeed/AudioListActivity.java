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
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

import static me.samuki.musicandspeed.MusicService.audioNames;
import static me.samuki.musicandspeed.MusicService.paths;
import static me.samuki.musicandspeed.MusicService.slowDrivingSongs;
import static me.samuki.musicandspeed.MusicService.fastDrivingSongs;
import static me.samuki.musicandspeed.MainActivity.DEBUG_TAG;
import static me.samuki.musicandspeed.MusicService.speedToExceed;

public class AudioListActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    static boolean isPermission;
    static LayoutInflater inflater;
    static boolean audioListIsActive;

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
        //setListsNamesList();
    }

    @Override
    protected void onResume() {
        Intent bindIntent = new Intent(this, MusicService.class);
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        setListsNamesList();

        audioListIsActive = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(musicService != null)
            unbindService(serviceConnection);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        audioListIsActive = false;
        if(musicService != null && serviceDisconnected)
            unbindService(serviceConnection);
        super.onDestroy();
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
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(DEBUG_TAG, String.valueOf(event.getAction()));

        return super.onTouchEvent(event);
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

    void setProgressBar() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.player_progressBar);
        MusicService.playerManager.setProgressBar(progressBar);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(DEBUG_TAG, MusicService.playerManager.getActualMusicPlaying() + "");
            MusicService.LocalBinder binder = (MusicService.LocalBinder) iBinder;
            musicService = binder.getService();

            TextView speedView = (TextView) findViewById(R.id.speedView);
            TextView titleView = (TextView) findViewById(R.id.player_trackTitle);
            ImageButton button = (ImageButton) findViewById(R.id.playButton);

            musicService.setSpeedViewAndTitleViewAndPlayButton(speedView, titleView, button);
            setProgressBar();
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

        if(cur != null) {
            count = cur.getCount();

            if(count > 0) {
                while(cur.moveToNext()) {
                    String name = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    int duration = cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    duration = duration/1000;
                    int secondsTens = (duration%60)/10;
                    int secondsOnes = (duration%60) - secondsTens * 10;
                    int minutes = duration/60;
                    audioNames.add(name);
                    String path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    paths.add(path);
                    LinearLayout musicRow = (LinearLayout) inflater.inflate(R.layout.music_row, null);
                    //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) new LinearLayout.LayoutParams(
                    //        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    //params.setMargins(20, 10, 20, 10);
                    //musicRow.setLayoutParams(params);
                    TextView nameView = musicRow.findViewById(R.id.musicRow_audioTitle);
                    TextView artistView = musicRow.findViewById(R.id.musicRow_audioArtist);
                    TextView durationView = musicRow.findViewById(R.id.musicRow_audioDuration);
                    final int trackId = i++;
                    nameView.setText(name);
                    artistView.setText(artist);
                    durationView.setText(getString(R.string.minutesAndSeconds, minutes, secondsTens, secondsOnes));
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

        //Tutaj trzeba wstawić ifa jeśli nasza apka ma pamiętać ostatnią graną listę
        showAllAudioNames();

        assert cur != null;
        cur.close();
    }

    public void showAllAudioNames() {
        slowDrivingSongs = new ArrayList<Integer>();
        fastDrivingSongs = new ArrayList<Integer>();

        LinearLayout container = (LinearLayout) findViewById(R.id.musicContainer);
        for(int i = 0; i < container.getChildCount(); i++) {
            container.getChildAt(i).setVisibility(View.VISIBLE);
            slowDrivingSongs.add(i);
        }
        changeList(findViewById(R.id.musicListChooser));
    }

    public void hideAudioNames(String tableName) {
        LinearLayout container = (LinearLayout) findViewById(R.id.musicContainer);

        slowDrivingSongs = new ArrayList<Integer>();
        fastDrivingSongs = new ArrayList<Integer>();

        MusicDbAdapter dbAdapter = new MusicDbAdapter(this);
        dbAdapter.open();
        Cursor songs = dbAdapter.getAllSongs(tableName);
        songs.moveToFirst();
        int count = 0;

            for (int i = 0; i < container.getChildCount(); i++) {
                LinearLayout songView = (LinearLayout) container.getChildAt(i);
                if(count < songs.getCount()) {
                    String songName = songs.getString(songs.getColumnIndex(MusicDbAdapter.KEY_NAME));
                    String songNameFromView = ((TextView) songView.findViewById(R.id.musicRow_audioTitle))
                            .getText().toString();
                    if (!songName.equals(songNameFromView)
                            && songView.getVisibility() == View.VISIBLE) {
                        songView.setVisibility(View.GONE);
                    } else if (songName.equals(songNameFromView)) {
                        if(songs.getInt(songs.getColumnIndex(MusicDbAdapter.KEY_SLOW_DRIVING)) == 1)
                            slowDrivingSongs.add(i);
                        if(songs.getInt(songs.getColumnIndex(MusicDbAdapter.KEY_FAST_DRIVING)) == 1)
                            fastDrivingSongs.add(i);
                        songs.moveToNext();
                        count++;
                        if(songView.getVisibility() == View.GONE)
                            songView.setVisibility(View.VISIBLE);
                    }
                } else {
                    songView.setVisibility(View.GONE);
                }
            }
        changeList(findViewById(R.id.musicListChooser));

        dbAdapter.close();
        songs.close();
    }

    void setListsNamesList() {
        LinearLayout container = (LinearLayout) findViewById(R.id.listContainer);

        while(container.getChildCount() > 1) {
            container.removeViewAt(1);
        }

        MusicDbAdapter dbAdapter = new MusicDbAdapter(this);
        dbAdapter.open();
        final Cursor tablesNames = dbAdapter.getAllTablesNames();
        int count = 0;

        if(tablesNames != null) {
            count = tablesNames.getCount();

            if(count > 0) {
                while(tablesNames.moveToNext()) {
                    final String name = tablesNames.getString(tablesNames.getColumnIndex(MusicDbAdapter.KEY_NAME));
                    final int speed = tablesNames.getInt(tablesNames.getColumnIndex(MusicDbAdapter.KEY_SPEED));
                    TextView listNameView = (TextView)inflater.inflate(R.layout.list_row, null);
                    listNameView.setText(name);
                    listNameView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hideAudioNames(name);
                            speedToExceed = speed;
                        }
                    });
                    container.addView(listNameView);
                }

            }
        }

        assert tablesNames != null;
        tablesNames.close();
        dbAdapter.close();
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

    public void changeList(View view) {
        View songsList = findViewById(R.id.musicList);
        View songsListChooser = findViewById(R.id.musicListChooser);
        View listsList = findViewById(R.id.listsList);
        View listsListChooser = findViewById(R.id.listsListChooser);
        switch (view.getId()) {
            case R.id.musicListChooser:
                songsList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0f));
                songsListChooser.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                listsList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
                listsListChooser.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                break;
            case R.id.listsListChooser:
                songsList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
                songsListChooser.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                listsList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0f));
                listsListChooser.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                break;
        }
    }

    public void changeToDefault(View view) {
        showAllAudioNames();
    }
}
