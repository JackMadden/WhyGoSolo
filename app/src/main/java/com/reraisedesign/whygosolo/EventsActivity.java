package com.reraisedesign.whygosolo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class EventsActivity extends AppCompatActivity {

    private TextView mMsgNotification;
    private ListView mEventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CustomAdapter mAdapter;

        mEventList = (ListView) findViewById(R.id.event_list);

        //TODO: CustomAdapter generates the cards, rows and sections, needs to use JSON when available.
        mAdapter = new CustomAdapter(this);
        for (int i = 1; i < 30; i++) {
            mAdapter.addItem("Row Item #" + i);
            if (i % 4 == 0) {
                mAdapter.addSectionHeaderItem("Section #" + i);
            }
        }
        mEventList.setAdapter(mAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_events, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            //when back button is pressed return the user to the activity that started it.
            case R.id.action_filter:
                Toast.makeText(this,"Filter Pressed", Toast.LENGTH_SHORT).show();
                filterActivityIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void filterActivityIntent(){
        Intent intent = new Intent(this, FilterActivity.class);
        startActivity(intent);
    }
}
