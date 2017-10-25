package me.samuki.musicandspeed;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static me.samuki.musicandspeed.MainActivity.DEBUG_TAG;
import static me.samuki.musicandspeed.MusicService.audioArtists;
import static me.samuki.musicandspeed.MusicService.audioDurations;
import static me.samuki.musicandspeed.MusicService.audioNames;
import static me.samuki.musicandspeed.MusicService.fastDrivingSongs;
import static me.samuki.musicandspeed.MusicService.slowDrivingSongs;
import static me.samuki.musicandspeed.MusicService.speedToExceed;

public class AudioListFragment extends Fragment {
    private static int position = 0;
    private static LinearLayout audioContainer;

    private LayoutInflater inflater;
    private View songsListChooser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;

        assert container != null;
        RelativeLayout layout = (RelativeLayout) container.getParent();
        songsListChooser = layout.findViewById(R.id.musicListChooser);

        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(position++ == 0)
            fillAudioContainer(view);
        else
            fillListContainer(view);
    }

    private void fillAudioContainer(View view) {
        audioContainer = view.findViewById(R.id.container);
        for(int i = 0; i < audioNames.size(); i++) {
            LinearLayout musicRow;

            if (i + 1 != audioNames.size())
                musicRow = (LinearLayout) inflater.inflate(R.layout.music_row, null);
            else
                musicRow = (LinearLayout) inflater.inflate(R.layout.music_row_last_element, null);

            TextView nameView = musicRow.findViewById(R.id.musicRow_audioTitle);
            nameView.setText(audioNames.get(i));

            TextView artistView = musicRow.findViewById(R.id.musicRow_audioArtist);
            artistView.setText(audioArtists.get(i));

            TextView durationView = musicRow.findViewById(R.id.musicRow_audioDuration);
            int duration = audioDurations.get(i);
            int secondsTens = (duration%60)/10;
            int secondsOnes = (duration%60) - secondsTens * 10;
            int minutes = duration/60;
            durationView.setText(getString(R.string.minutesAndSeconds, minutes, secondsTens, secondsOnes));

            final int trackId = i;
            musicRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToMainActivity(trackId);
                }
            });
            audioContainer.addView(musicRow);
        }
    }

    public void showAllAudioNames() {
        slowDrivingSongs = new ArrayList<>();
        fastDrivingSongs = new ArrayList<>();

        for(int i = 0; i < audioContainer.getChildCount(); i++) {
            audioContainer.getChildAt(i).setVisibility(View.VISIBLE);
            slowDrivingSongs.add(i);
        }
    }

    public void hideAudioNames(String tableName) {

        slowDrivingSongs = new ArrayList<>();
        fastDrivingSongs = new ArrayList<>();

        MusicDbAdapter dbAdapter = new MusicDbAdapter(getActivity());
        dbAdapter.open();
        Cursor songs = dbAdapter.getAllSongs(tableName);
        songs.moveToFirst();
        int count = 0;

        for (int i = 0; i < audioContainer.getChildCount(); i++) {
            LinearLayout songView = (LinearLayout) audioContainer.getChildAt(i);
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

        dbAdapter.close();
        songs.close();
    }

    private void fillListContainer(View view) {
        LinearLayout container = view.findViewById(R.id.container);
        //DEFAULT LIST
        LinearLayout listRowDefault = (LinearLayout) inflater.inflate(R.layout.list_row, null);
        TextView listNameViewDefault = listRowDefault.findViewById(R.id.listRow_listName);
        listNameViewDefault.setText(getString(R.string.defaultList));
        listNameViewDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songsListChooser.callOnClick();
                showAllAudioNames();
                speedToExceed = 30;
            }
        });
        container.addView(listRowDefault);

        while(container.getChildCount() > 1) {
            container.removeViewAt(1);
        }

        MusicDbAdapter dbAdapter = new MusicDbAdapter(getActivity());
        dbAdapter.open();
        final Cursor tablesNames = dbAdapter.getAllTablesNames();
        int count;

        if(tablesNames != null) {
            count = tablesNames.getCount();

            if(count > 0) {
                while(tablesNames.moveToNext()) {
                    final String name = tablesNames.getString(tablesNames.getColumnIndex(MusicDbAdapter.KEY_NAME));
                    final int speed = tablesNames.getInt(tablesNames.getColumnIndex(MusicDbAdapter.KEY_SPEED));
                    LinearLayout listRow = (LinearLayout) inflater.inflate(R.layout.list_row, null);
                    TextView listNameView = listRow.findViewById(R.id.listRow_listName);
                    listNameView.setText(name);
                    listNameView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            songsListChooser.callOnClick();
                            hideAudioNames(name);
                            speedToExceed = speed;
                        }
                    });
                    container.addView(listRow);
                }

            }
        }

        assert tablesNames != null;
        tablesNames.close();
        dbAdapter.close();
    }

    public void goToMainActivity(int trackId) {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        mainIntent.putExtra("trackId", trackId);
        mainIntent.putExtra("play", true);
        startActivity(mainIntent);
    }
}
