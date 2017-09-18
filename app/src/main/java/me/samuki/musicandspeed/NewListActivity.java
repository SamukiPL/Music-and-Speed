package me.samuki.musicandspeed;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.zip.Inflater;

import static me.samuki.musicandspeed.MusicService.audioNames;
import static me.samuki.musicandspeed.MusicService.paths;
import static me.samuki.musicandspeed.MainActivity.DEBUG_TAG;

public class NewListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_list_activity);
        setToolbar();
        setAudioNamesList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addList:
                Toast.makeText(this, "Dodane", Toast.LENGTH_SHORT).show();
                //I tutaj ma się robić coś szczególnego, prawdopodobnie SQL :(
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void setToolbar() {
        android.support.v7.widget.Toolbar toolbar =
                (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setTitle(R.string.newList);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void setAudioNamesList() {
        LayoutInflater inflater = getLayoutInflater();

        LinearLayout container = (LinearLayout) findViewById(R.id.settings_trackContainer);

        for (int i = 0; i < audioNames.size(); i++) {
            RelativeLayout trackRow = (RelativeLayout) inflater.inflate(R.layout.music_row_check_boxes, null);
            TextView audioName = trackRow.findViewById(R.id.musicRow_audioTitle);
            audioName.setText(audioNames.get(i));
            container.addView(trackRow);
            Log.d(DEBUG_TAG, "TAK " + i);
        }
    }

    public void changeValue(View view) {
        TextView valueView = (TextView) findViewById(R.id.settings_0PositionNumber);
        switch (view.getId()) {
            case R.id.settings_0Up:
            case R.id.settings_0Down:
                valueView = (TextView) findViewById(R.id.settings_0PositionNumber);
                break;
            case R.id.settings_1Up:
            case R.id.settings_1Down:
                valueView = (TextView) findViewById(R.id.settings_1PositionNumber);
                break;
            case R.id.settings_2Up:
            case R.id.settings_2Down:
                valueView = (TextView) findViewById(R.id.settings_2PositionNumber);
                break;
        }
        int value = Integer.parseInt(valueView.getText().toString());
        if(view.getContentDescription().equals(getString(R.string.up))) value++; else value--;
        if(value == 10) value = 0; else if(value == -1) value = 9;
        valueView.setText(getString(R.string.valueInt, value));
    }
}
