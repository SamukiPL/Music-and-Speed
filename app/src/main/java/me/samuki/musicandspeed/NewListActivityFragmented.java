//package me.samuki.musicandspeed;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.Menu;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import java.util.LinkedList;
//import java.util.List;
//
//import static me.samuki.musicandspeed.MainActivity.DEBUG_TAG;
//
//public class NewListActivityFragmented extends AppCompatActivity {
//    static List<String> summaryTitles;
//    static List<String> summaryNumbers;
//    static List<Integer> selectedSongs;
//    static List<List> intervalsSongs;
//    static List<Integer> intervalsSpeed;
//    static boolean firstAudioList;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_fragmented_new_list);
//        setToolbar();
//
//        summaryTitles = new LinkedList<>();
//        summaryNumbers = new LinkedList<>();
//        selectedSongs = new LinkedList<>();
//        intervalsSongs = new LinkedList<>();
//        intervalsSpeed = new LinkedList<>();
//        firstAudioList = true;
//
//        NewListFragment firstFragment = new NewListFragment();
//
//        Bundle args = new Bundle();
//        args.putString("title", getString(R.string.nameFragment));
//
//        firstFragment.setArguments(args);
//
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.newList_viewPager, firstFragment).commit();
//    }
//
//    void setToolbar() {
//        android.support.v7.widget.Toolbar toolbar =
//                (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
//        toolbar.setTitle(getString(R.string.newList));
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//    }
//
//    public void setListName(View view) {
//        EditText listNameEditText = (EditText) ((View)view.getParent().getParent())
//                                    .findViewById(R.id.settings_newListName);
//        Log.d(DEBUG_TAG, String.valueOf(listNameEditText));
//        summaryTitles.add(listNameEditText.getText().toString());
//
//        summaryNumbers.add("");
//
//        NewListFragment anotherActivity = new NewListFragment();
//        Bundle args = new Bundle();
//        args.putString("title", getString(R.string.songsFragment));
//        anotherActivity.setArguments(args);
//        getSupportFragmentManager().beginTransaction().replace(R.id.newList_viewPager, anotherActivity).commit();
//
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }
//
//    public void setSelectedSongs(View view) {
//        LinearLayout selectedSongsContainer = (LinearLayout) ((View)view.getParent().getParent())
//                                                            .findViewById(R.id.selectedContainer);
//        List<Integer> selectedSongsInInterval = new LinkedList<>();
//
//        for(int i = 0; i < selectedSongsContainer.getChildCount(); i++) {
//            TextView audioNumberView = ((View)selectedSongsContainer.getChildAt(i))
//                    .findViewById(R.id.musicRow_audioNumber);
//
//            int audioNumber = Integer.parseInt(audioNumberView.getText().toString());
//            if (firstAudioList) {
//                if (audioNumber >= 0) selectedSongs.add(audioNumber);
//            } else {
//                if (audioNumber >= 0) selectedSongsInInterval.add(audioNumber);
//            }
//        }
//
//        int count;
//
//        if (firstAudioList) count = selectedSongs.size();
//        else count = selectedSongsInInterval.size();
//
//        if (firstAudioList) {
//            summaryTitles.add(getResources().getQuantityString(
//                R.plurals.summaryTitleList, count, count));
//            summaryNumbers.add("");
//        }
//        else {
//            summaryTitles.add(getResources().getQuantityString(
//                    R.plurals.summaryTitleInterval, count, count));
//            intervalsSongs.add(selectedSongsInInterval);
//            summaryNumbers.add(getString(R.string.intervalSpeed,
//                    intervalsSpeed.get(intervalsSongs.size()-1)));
//        }
//
//        NewListFragment anotherActivity = new NewListFragment();
//        Bundle args = new Bundle();
//        args.putString("title", getString(R.string.summaryFragment));
//        anotherActivity.setArguments(args);
//        getSupportFragmentManager().beginTransaction().replace(R.id.newList_viewPager, anotherActivity).commit();
//
//        firstAudioList = false;
//    }
//
//    public void addNewInterval(View view) {
//        NewListFragment anotherActivity = new NewListFragment();
//        Bundle args = new Bundle();
//        args.putString("title", getString(R.string.speedFragment));
//        anotherActivity.setArguments(args);
//        getSupportFragmentManager().beginTransaction().replace(R.id.newList_viewPager, anotherActivity).commit();
//    }
//
//    public void setSongsForInterval(View view) {
//        NewListFragment anotherActivity = new NewListFragment();
//        Bundle args = new Bundle();
//        args.putString("title", getString(R.string.songsFragment));
//        intervalsSpeed.add(getSpeed((View) view.getParent().getParent()));
//        anotherActivity.setArguments(args);
//        getSupportFragmentManager().beginTransaction().replace(R.id.newList_viewPager, anotherActivity).commit();
//    }
//
//    public void changeValue(View view) {
//        TextView valueView = (TextView) findViewById(R.id.settings_0PositionNumber);
//        switch (view.getId()) {
//            case R.id.settings_0Up:
//            case R.id.settings_0Down:
//                valueView = (TextView) findViewById(R.id.settings_0PositionNumber);
//                break;
//            case R.id.settings_1Up:
//            case R.id.settings_1Down:
//                valueView = (TextView) findViewById(R.id.settings_1PositionNumber);
//                break;
//            case R.id.settings_2Up:
//            case R.id.settings_2Down:
//                valueView = (TextView) findViewById(R.id.settings_2PositionNumber);
//                break;
//        }
//        int value = Integer.parseInt(valueView.getText().toString());
//        if(view.getContentDescription().equals(getString(R.string.up))) value++; else value--;
//        if(value == 10) value = 0; else if(value == -1) value = 9;
//        valueView.setText(getString(R.string.valueInt, value));
//    }
//
//    private int getSpeed(View view) {
//        TextView position0View = (TextView) view.findViewById(R.id.settings_0PositionNumber);
//        TextView position1View = (TextView) view.findViewById(R.id.settings_1PositionNumber);
//        TextView position2View = (TextView) view.findViewById(R.id.settings_2PositionNumber);
//        return  Integer.parseInt(position2View.getText().toString())*100 +
//                Integer.parseInt(position1View.getText().toString())*10  +
//                Integer.parseInt(position0View.getText().toString());
//    }
//}
