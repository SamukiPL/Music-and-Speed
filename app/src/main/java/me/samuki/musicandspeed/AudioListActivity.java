//package me.samuki.musicandspeed;
//
//import android.Manifest;
//import android.content.ComponentName;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.*;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import java.util.LinkedList;
//
//import static me.samuki.musicandspeed.MainActivity.DEBUG_TAG;
//import static me.samuki.musicandspeed.MusicService.audioArtists;
//import static me.samuki.musicandspeed.MusicService.audioDurations;
//import static me.samuki.musicandspeed.MusicService.audioNames;
//import static me.samuki.musicandspeed.MusicService.audioPaths;
//
//public class AudioListActivity extends AppCompatActivity {
//    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
//    private static final int PAGES_COUNT = 2;
//
//    static boolean isPermission;
//    static boolean audioListIsActive;
//    static LayoutInflater inflater;
//
//    private ViewPager viewPager;
//    private PagerAdapter viewPagerAdapter;
//    private MusicService musicService;
//    private boolean serviceDisconnected;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        setToolbar();
//
//        inflater = getLayoutInflater();
//
//        viewPager = (ViewPager) findViewById(R.id.viewPager);
//        viewPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(viewPagerAdapter);
//        viewPager.setPageTransformer(true, new DepthPageTransformer());
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                View songsListChooser = findViewById(R.id.musicListChooser);
//                View listsListChooser = findViewById(R.id.listsListChooser);
//
//                if (position == 0) {
//                    songsListChooser.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
//                    listsListChooser.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
//                } else if (position == 1) {
//                    songsListChooser.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
//                    listsListChooser.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//        isPermission = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//        if(isPermission) setAudioLists(); else askForPermission();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (viewPager.getCurrentItem() == 0)
//            super.onBackPressed();
//        else
//            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
//    }
//
//    @Override
//    protected void onResume() {
//        Intent bindIntent = new Intent(this, MusicService.class);
//        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//
//        audioListIsActive = true;
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        if(musicService != null)
//            unbindService(serviceConnection);
//        super.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        audioListIsActive = false;
//        if(musicService != null && serviceDisconnected)
//            unbindService(serviceConnection);
//        super.onDestroy();
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.settings_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.settings:
//                Intent intent = new Intent(this, SettingsActivity.class);
//                startActivity(intent);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    void setToolbar() {
//        Toolbar toolbar =
//                (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//    }
//
//    void setProgressBar() {
//        ProgressBar progressBar = (ProgressBar) findViewById(R.id.player_progressBar);
//        MusicService.playerManager.setProgressBar(progressBar);
//    }
//
//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            Log.d(DEBUG_TAG, MusicService.playerManager.getActualMusicPlaying() + "");
//            MusicService.LocalBinder binder = (MusicService.LocalBinder) iBinder;
//            musicService = binder.getService();
//
//            TextView speedView = (TextView) findViewById(R.id.speedView);
//            TextView titleView = (TextView) findViewById(R.id.player_trackTitle);
//            ImageButton button = (ImageButton) findViewById(R.id.playButton);
//
//            musicService.setSpeedViewAndTitleViewAndPlayButton(speedView, titleView, button);
//            setProgressBar();
//            serviceDisconnected = false;
//            //Tutaj musi być coś co ma się zrobić jeśli w tle cały czas działałą apka,
//            // w sensie jakaś fajna metoda
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            serviceDisconnected = true;
//        }
//    };
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    isPermission = true;
//                    setAudioLists();
//                } else {
//                    isPermission = false;
//                } return;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    private void askForPermission() {
//        if(ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
//                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//        } else {
//            isPermission = true;
//        }
//    }
//
//    public void setAudioLists() {
//        audioNames = new LinkedList<>();
//        audioArtists = new LinkedList<>();
//        audioPaths = new LinkedList<>();
//        audioDurations = new LinkedList<>();
//
//        ContentResolver cr = this.getContentResolver();
//
//        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
//        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
//        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
//        int count;
//
//        if(cur != null) {
//            count = cur.getCount();
//
//            if(count > 0) {
//                while(cur.moveToNext()) {
//                    String name = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
//                    audioNames.add(name);
//                    String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
//                    audioArtists.add(artist);
//                    String path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
//                    audioPaths.add(path);
//                    int duration = cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DURATION));
//                    audioDurations.add(duration/1000);
//                }
//            }
//        }
//
//        if (cur != null)
//            cur.close();
//    }
//
//    public void goToMainActivity(View view) {
//        Intent mainIntent = new Intent(this, MainActivity.class);
//        mainIntent.putExtra("trackId", -86);
//        mainIntent.putExtra("play", MusicService.playerManager.isPlaying);
//        startActivity(mainIntent);
//    }
//
//    private void restartMusicService() {
//        Intent startIntent = new Intent(this, MusicService.class);
//        startIntent.setAction("Restart");
//        startService(startIntent);
//    }
//
//    private void pauseMusicService() {
//        Intent pauseIntent = new Intent(this, MusicService.class);
//        pauseIntent.setAction("Pause");
//        startService(pauseIntent);
//    }
//
//    public void startMusic(View view) {
//        ImageButton button = (ImageButton) view;
//        if(button.getContentDescription().equals(getString(R.string.play))) {
//            restartMusicService();
//            button.setContentDescription(getString(R.string.stop));
//            button.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
//        }
//        else {
//            pauseMusicService();
//            button.setContentDescription(getString(R.string.play));
//            button.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
//        }
//        Log.d(DEBUG_TAG, "PLAY MUSIC");
//    }
//
//    public void previousMusic(View view) {
//        Intent previousIntent = new Intent(this, MusicService.class);
//        previousIntent.setAction("Previous");
//        startService(previousIntent);
//
//    }
//
//    public void nextMusic(View view) {
//        Intent nextIntent = new Intent(this, MusicService.class);
//        nextIntent.setAction("Next");
//        startService(nextIntent);
//    }
//
//    public void changeList(View view) {
//        View songsListChooser = findViewById(R.id.musicListChooser);
//        View listsListChooser = findViewById(R.id.listsListChooser);
//        switch (view.getId()) {
//            case R.id.musicListChooser:
//                songsListChooser.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
//                listsListChooser.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
//                viewPager.setCurrentItem(0);
//                break;
//            case R.id.listsListChooser:
//                songsListChooser.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
//                listsListChooser.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
//                viewPager.setCurrentItem(1);
//                break;
//        }
//    }
//
//    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
//
//        ScreenSlidePagerAdapter(FragmentManager fragmentManager) {
//            super(fragmentManager);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return new AudioListFragment();
//        }
//
//        @Override
//        public int getCount() {
//            return PAGES_COUNT;
//        }
//    }
//
//    private class DepthPageTransformer implements ViewPager.PageTransformer {
//        private static final float MIN_SCALE = 0.75f;
//
//        @Override
//        public void transformPage(View page, float position) {
//            int pageWidth = page.getWidth();
//
//            if (position < -1) {
//                page.setAlpha(0);
//            } else if (position <= 0) {
//                page.setAlpha(1);
//                page.setTranslationX(0);
//                page.setScaleY(1);
//                page.setScaleY(1);
//            } else if (position <= 1) {
//                page.setAlpha(1 - position);
//                page.setTranslationX(pageWidth * -position);
//                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
//                page.setScaleX(scaleFactor);
//                page.setScaleY(scaleFactor);
//            } else {
//                page.setAlpha(0);
//            }
//        }
//    }
//}
