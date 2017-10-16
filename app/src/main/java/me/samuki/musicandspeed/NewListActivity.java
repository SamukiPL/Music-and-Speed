package me.samuki.musicandspeed;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static me.samuki.musicandspeed.MusicService.audioNames;
import static me.samuki.musicandspeed.MainActivity.DEBUG_TAG;

public class NewListActivity extends AppCompatActivity {

    private String listName;
    private int speed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_list_activity);

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                listName = "";
                speed = 0;
            } else {
                listName = extras.getString(MusicDbAdapter.KEY_NAME);
                speed = extras.getInt(MusicDbAdapter.KEY_SPEED);
            }
        } else {
            listName = (String) savedInstanceState.getSerializable(MusicDbAdapter.KEY_NAME);
            speed = (int) savedInstanceState.getSerializable(MusicDbAdapter.KEY_SPEED);
        }

        setToolbar();
        setAudioNamesList();
        setSpeed(speed);

        EditText listNameEditText = (EditText) findViewById(R.id.settings_newListName);
        listNameEditText.setText(listName);
        setValidationForEditText(listNameEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(listName.equals(""))
            getMenuInflater().inflate(R.menu.new_list_menu, menu);
        else
            getMenuInflater().inflate(R.menu.edit_list_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addList:
                TextView newListNameView = (TextView) findViewById(R.id.settings_newListName);
                String newListName = newListNameView.getText().toString();
                int speed = getSpeed();
                List<String> songsNamesList = new ArrayList<String>();
                List<Boolean> slowDrivingList = new ArrayList<Boolean>();
                List<Boolean> fastDrivingList = new ArrayList<Boolean>();
                getCheckedSongs(songsNamesList, slowDrivingList, fastDrivingList);
                if (    !newListName.equals("")
                        && speed > 0
                        && songsNamesList.size() > 0) {
                    MusicDbAdapter dbAdapter = new MusicDbAdapter(this);
                    dbAdapter.open();
                    if (dbAdapter.isNameExist(newListName)) {
                        //Trzeba dodać alert sprawdzający pewność użytkownika.
                        dbAdapter.dropTable(listName);
                        dbAdapter.deleteTableName(listName);
                    }
                    dbAdapter.insertTableName(newListName, speed);
                    dbAdapter.createTable(newListName);
                    for (int i = 0; i < songsNamesList.size(); i++) {
                        int slowDriving = (slowDrivingList.get(i)) ? 1:0;
                        int fastDriving = (fastDrivingList.get(i)) ? 1:0;
                        dbAdapter.insertData(newListName, songsNamesList.get(i),
                                             slowDriving, fastDriving);
                    }
                    dbAdapter.close();
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.fillInAllFields), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void setToolbar() {
        android.support.v7.widget.Toolbar toolbar =
                (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        if(listName.equals("")) toolbar.setTitle(R.string.newList);
            else  toolbar.setTitle(listName);
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

        Cursor songsInTheList = null;
        MusicDbAdapter dbAdapter = null;
        int count = 0;

        if(!listName.equals("")) {
            dbAdapter = new MusicDbAdapter(this);
            dbAdapter.open();
            songsInTheList = dbAdapter.getAllSongs(listName);
            songsInTheList.moveToFirst();
            count = songsInTheList.getCount();
        }

        LinearLayout container = (LinearLayout) findViewById(R.id.settings_trackContainer);

        for (int i = 0; i < audioNames.size(); i++) {
            RelativeLayout trackRow = (RelativeLayout) inflater.inflate(R.layout.music_row_check_boxes, null);
            TextView audioNameView = trackRow.findViewById(R.id.musicRow_audioTitle);
            String audioName = audioNames.get(i);
            audioNameView.setText(audioName);
            container.addView(trackRow);
            if(songsInTheList != null && count > 0) {
                String name = songsInTheList.getString(songsInTheList.getColumnIndex(MusicDbAdapter.KEY_NAME));
                if(name.equals(audioName)) {
                    CheckBox slowDrivingBox = trackRow.findViewById(R.id.musicRowCheckBoxes_slowDriving);
                    CheckBox fastDrivingBox = trackRow.findViewById(R.id.musicRowCheckBoxes_fastDriving);
                    slowDrivingBox.setChecked((songsInTheList.getInt(
                            songsInTheList.getColumnIndex(MusicDbAdapter.KEY_SLOW_DRIVING)) == 1));
                    fastDrivingBox.setChecked((songsInTheList.getInt(
                            songsInTheList.getColumnIndex(MusicDbAdapter.KEY_FAST_DRIVING)) == 1));
                    songsInTheList.moveToNext();
                    count--;
                }
            }
        }

        if(songsInTheList != null) {
            songsInTheList.close();
            dbAdapter.close();
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

    private void setSpeed(int speed) {
        TextView position0View = (TextView) findViewById(R.id.settings_0PositionNumber);
        TextView position1View = (TextView) findViewById(R.id.settings_1PositionNumber);
        TextView position2View = (TextView) findViewById(R.id.settings_2PositionNumber);
        int position2 = speed/100;
        speed = (speed - (position2*100));
        int position1 = speed/10;
        speed = (speed - (position1*10));
        int position0 = speed;
        position0View.setText(getString(R.string.valueInt, position0));
        position1View.setText(getString(R.string.valueInt, position1));
        position2View.setText(getString(R.string.valueInt, position2));
    }

    private int getSpeed() {
        TextView position0View = (TextView) findViewById(R.id.settings_0PositionNumber);
        TextView position1View = (TextView) findViewById(R.id.settings_1PositionNumber);
        TextView position2View = (TextView) findViewById(R.id.settings_2PositionNumber);
        return  Integer.parseInt(position2View.getText().toString())*100 +
                Integer.parseInt(position1View.getText().toString())*10  +
                Integer.parseInt(position0View.getText().toString());
    }

    private void getCheckedSongs(List<String> songsNamesList, List<Boolean> slowDrivingList,
                                 List<Boolean> fastDrivingList) {
        LinearLayout container = (LinearLayout) findViewById(R.id.settings_trackContainer);

        for(int i = 0; i < container.getChildCount(); i++) {
            RelativeLayout trackRow = (RelativeLayout) container.getChildAt(i);
            CheckBox slowDrivingCheckBox = trackRow.findViewById(R.id.musicRowCheckBoxes_slowDriving);
            boolean slowDrivingChecked = slowDrivingCheckBox.isChecked();
            CheckBox fastDrivingCheckBox = trackRow.findViewById(R.id.musicRowCheckBoxes_fastDriving);
            boolean fastDrivingChecked = fastDrivingCheckBox.isChecked();
            if(slowDrivingChecked || fastDrivingChecked) {
                TextView songNameView = trackRow.findViewById(R.id.musicRow_audioTitle);
                String songName = songNameView.getText().toString();
                songsNamesList.add(songName);
                slowDrivingList.add(slowDrivingChecked);
                fastDrivingList.add(fastDrivingChecked);
            }
        }
    }

    private void setValidationForEditText(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().contains("\"")) {
                    editText.setText(charSequence.toString().replace("\"", ""));
                    editText.setSelection(editText.getText().length());
                } else if (charSequence.toString().contains("\'")) {
                    editText.setText(charSequence.toString().replace("\'", ""));
                    editText.setSelection(editText.getText().length());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
