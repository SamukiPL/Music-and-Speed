package me.samuki.musicandspeed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class NewListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_list_activity);
        setToolbar();
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
