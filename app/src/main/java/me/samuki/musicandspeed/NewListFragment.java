package me.samuki.musicandspeed;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.LinkedList;

import static me.samuki.musicandspeed.MainActivity.DEBUG_TAG;
import static me.samuki.musicandspeed.NewListActivityFragmented.firstAudioList;
import static me.samuki.musicandspeed.NewListActivityFragmented.selectedSongs;
import static me.samuki.musicandspeed.NewListActivityFragmented.summaryNumbers;
import static me.samuki.musicandspeed.NewListActivityFragmented.summaryTitles;
import static me.samuki.musicandspeed.MusicService.audioArtists;
import static me.samuki.musicandspeed.MusicService.audioDurations;
import static me.samuki.musicandspeed.MusicService.audioNames;

public class NewListFragment extends Fragment {

    private LayoutInflater inflater;
    private String title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        savedInstanceState = getArguments();
        if(savedInstanceState != null) {
            title = savedInstanceState.getString("title");
            if (title.equals(getString(R.string.nameFragment)))
                return inflater.inflate(R.layout.new_list_fragment_list_name, container, false);

            else if (title.equals(getString(R.string.speedFragment)))
                return inflater.inflate(R.layout.new_list_fragment_speed, container, false);

            else if (title.equals(getString(R.string.songsFragment)))
                return inflater.inflate(R.layout.new_list_fragment_audio_list, container, false);

            else if (title.equals(getString(R.string.summaryFragment)))
                return inflater.inflate(R.layout.new_list_fragment_summary, container, false);
        }
        return inflater.inflate(R.layout.new_list_fragment_list_name, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (title.equals(getString(R.string.nameFragment)))
            newListNameFragment();
        else if (title.equals(getString(R.string.speedFragment)))
            newListSpeedFragment();
        else if (title.equals(getString(R.string.songsFragment)))
            newListAudioListFragment(view);
        else if (title.equals(getString(R.string.summaryFragment)))
            newListSummaryFragment(view);
    }

    private void newListNameFragment() {

    }

    private void newListSpeedFragment() {

    }

    private void newListAudioListFragment(View view) {
        fillAudioContainer((LinearLayout) view.findViewById(R.id.selectedContainer),
                        (LinearLayout) view.findViewById(R.id.notSelectedContainer));
    }

    private void newListSummaryFragment(View view) {
        fillSummaryContainer((LinearLayout) view.findViewById(R.id.container));
    }

    private void fillAudioContainer(LinearLayout selectedParentView, LinearLayout notSelectedParentView) {
        int index = 0;
        for(int i = 0; i < audioNames.size(); i++) {
            if(selectedSongs.size() != 0) {
                if(index >= selectedSongs.size())
                    break;
                if (!firstAudioList && selectedSongs.get(index) != i)
                    continue;
                else
                    index++;
            }
            final LinearLayout musicRowSelected;
            final LinearLayout musicRowNotSelected;

            if (i + 1 != audioNames.size()) {
                musicRowSelected = (LinearLayout) inflater.inflate(R.layout.music_row, null);
                musicRowNotSelected = (LinearLayout) inflater.inflate(R.layout.music_row, null);
            }
            else {
                musicRowSelected = (LinearLayout) inflater.inflate(R.layout.music_row, null);
                musicRowNotSelected = (LinearLayout) inflater.inflate(R.layout.music_row_last_element, null);
            }
            //SELECTED
            musicRowSelected.setVisibility(View.GONE);
            final TextView selectedId = musicRowSelected.findViewById(R.id.musicRow_audioNumber);
            selectedId.setText(getString(R.string.valueInt, -1));

            TextView nameViewSelected = musicRowSelected.findViewById(R.id.musicRow_audioTitle);
            nameViewSelected.setText(audioNames.get(i));

            TextView artistViewSelected = musicRowSelected.findViewById(R.id.musicRow_audioArtist);
            artistViewSelected.setText(audioArtists.get(i));

            TextView durationViewSelected = musicRowSelected.findViewById(R.id.musicRow_audioDuration);

            //NOT SELECTED
            TextView nameViewNotSelected = musicRowNotSelected.findViewById(R.id.musicRow_audioTitle);
            nameViewNotSelected.setText(audioNames.get(i));

            TextView artistViewNotSelected = musicRowNotSelected.findViewById(R.id.musicRow_audioArtist);
            artistViewNotSelected.setText(audioArtists.get(i));

            TextView durationViewNotSelected = musicRowNotSelected.findViewById(R.id.musicRow_audioDuration);

            int duration = audioDurations.get(i);
            int secondsTens = (duration%60)/10;
            int secondsOnes = (duration%60) - secondsTens * 10;
            int minutes = duration/60;

            durationViewSelected.setText(getString(R.string.minutesAndSeconds, minutes, secondsTens, secondsOnes));
            durationViewNotSelected.setText(getString(R.string.minutesAndSeconds, minutes, secondsTens, secondsOnes));

            final int trackId = i;
            musicRowSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedId.setText(getString(R.string.valueInt, -1));
                    musicRowSelected.setVisibility(View.GONE);
                    musicRowNotSelected.setVisibility(View.VISIBLE);
                }
            });
            musicRowNotSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    musicRowSelected.setVisibility(View.VISIBLE);
                    selectedId.setText(getString(R.string.valueInt, trackId));
                    musicRowNotSelected.setVisibility(View.GONE);
                }
            });
            selectedParentView.addView(musicRowSelected);
            notSelectedParentView.addView(musicRowNotSelected);
        }
        NewListActivityFragmented.firstAudioList = false;
    }

    private void fillSummaryContainer(LinearLayout view) {
        for (int i = 0; i < summaryTitles.size(); i++) {
            LinearLayout summaryRow = (LinearLayout) inflater.inflate(R.layout.summary_row, null);
            TextView descriptionView = summaryRow.findViewById(R.id.summaryRow_description);
            descriptionView.setText(summaryTitles.get(i));
            TextView numberView = summaryRow.findViewById(R.id.summaryRow_number);
            numberView.setText(summaryNumbers.get(i));
            view.addView(summaryRow, view.getChildCount()-1);
        }
    }
}
