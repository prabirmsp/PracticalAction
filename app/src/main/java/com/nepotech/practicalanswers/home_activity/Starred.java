package com.nepotech.practicalanswers.home_activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.nepotech.practicalanswers.items.ItemsRecyclerViewAdapter;
import com.nepotech.practicalanswers.R;
import com.nepotech.practicalanswers.items.Item;
import com.nepotech.practicalanswers.items.ItemsDataSource;

import java.util.ArrayList;

public class Starred extends AppCompatActivity {

    RecyclerView mRecyclerView;
    ItemsDataSource mDataSource;
    ArrayList<Item> mStarredItems;
    ItemsRecyclerViewAdapter mRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_recycler_view);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);

        mDataSource = new ItemsDataSource(this);
        mDataSource.open();

        mStarredItems = mDataSource.getAllStarred();

        mRecyclerViewAdapter = new ItemsRecyclerViewAdapter(this, mStarredItems, "Starred");
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mDataSource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDataSource.open();

        mStarredItems = mDataSource.getAllStarred();
        mRecyclerViewAdapter = new ItemsRecyclerViewAdapter(this, mStarredItems, "Starred");
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mDataSource.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_starred, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

}
