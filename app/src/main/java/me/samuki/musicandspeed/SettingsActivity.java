package me.samuki.musicandspeed;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static me.samuki.musicandspeed.AudioListActivity.inflater;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setToolbar();
    }

    @Override
    protected void onResume() {
        setListsNamesList();
        super.onResume();
    }

    void setToolbar() {
        android.support.v7.widget.Toolbar toolbar =
            (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setTitle(R.string.settings);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void setListsNamesList() {
        final LinearLayout container = (LinearLayout) findViewById(R.id.settings_listContainer);

        while(container.getChildCount() > 0) {
            container.removeViewAt(0);
        }

        final MusicDbAdapter dbAdapter = new MusicDbAdapter(this);
        dbAdapter.open();
        Cursor tablesNames = dbAdapter.getAllTablesNames();
        int count;

        if(tablesNames != null) {
            count = tablesNames.getCount();
            if(count > 0) {
                while(tablesNames.moveToNext()) {
                    final String name = tablesNames.getString(
                            tablesNames.getColumnIndex(MusicDbAdapter.KEY_NAME));
                    final int speed = tablesNames.getInt(
                            tablesNames.getColumnIndex(MusicDbAdapter.KEY_SPEED));
                    final LinearLayout listNameContainer = (LinearLayout) inflater.inflate(
                                                        R.layout.list_row_settings, null);

                    TextView listNameView = listNameContainer.findViewById(
                                                        R.id.listRow_listName);
                    listNameView.setText(name);
                    listNameView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent( getApplicationContext(),
                                                        NewListActivity.class);
                            intent.putExtra(MusicDbAdapter.KEY_NAME, name);
                            intent.putExtra(MusicDbAdapter.KEY_SPEED, speed);
                            startActivity(intent);
                        }
                    });
                    ImageView listNameDeletionSign = listNameContainer.findViewById(
                                                                    R.id.listRow_deletionSign);
                    listNameContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteList(name, listNameContainer);
                        }
                    });
                    container.addView(listNameContainer);
                }
            }
        }

        assert tablesNames != null;
        tablesNames.close();
        dbAdapter.close();
    }

    public void newList(View view) {
        Intent intent = new Intent(this, NewListActivity.class);
        intent.putExtra(MusicDbAdapter.KEY_NAME, "");
        startActivity(intent);
    }

    public void newListTest(View view) {
        Intent intent = new Intent(this, NewListActivityFragmented.class);
        intent.putExtra(MusicDbAdapter.KEY_NAME, "");
        startActivity(intent);
    }

    public void deleteList(String listName, LinearLayout listNameContainer) {
        MusicDbAdapter dbAdapter = new MusicDbAdapter(this);
        dbAdapter.open();
        dbAdapter.deleteTableName(listName);
        dbAdapter.dropTable(listName);
        dbAdapter.close();
        listNameContainer.setVisibility(View.GONE);
    }
}
