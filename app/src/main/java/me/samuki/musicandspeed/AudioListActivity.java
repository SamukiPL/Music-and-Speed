package me.samuki.musicandspeed;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static me.samuki.musicandspeed.MusicService.audioNames;

public class AudioListActivity extends Activity {
    private LayoutInflater inflater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);
        inflater = getLayoutInflater();

        LinearLayout container = findViewById(R.id.musicContainer);
        for(int i = 0; i < audioNames.size(); i++) {
            RelativeLayout musicRow = (RelativeLayout) inflater.inflate(R.layout.music_row, null);
            TextView name = musicRow.findViewById(R.id.musicRow_name);
            name.setText(audioNames.get(i));
            container.addView(musicRow);
        }
    }
}
